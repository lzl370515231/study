Thread:
	
	线程栈模型：
		是指某时刻时内存中线程调度的栈信息，当前调用的方法总是位于栈顶。
	
	
	线程状态：
		线程包括以下这几个状态：创建(new)、就绪(runnable)、运行(running)、阻塞(blocked)、time waiting、waiting、消亡（dead）。
	
		线程创建之后，不会立即进入就绪状态，因为线程的运行需要一些条件（比如内存资源，在前面的JVM内存区域划分一篇博文中知道程序计数器、Java栈、本地方法栈都是线程私有的，所以需要为线程分配一定的内存空间），只有线程运行需要的所有条件满足了，才进入就绪状态。
	
		当线程进入就绪状态后，不代表立刻就能获取CPU执行时间，也许此时CPU正在执行其他的事情，因此它要等待。当得到CPU执行时间之后，线程便真正进入运行状态。

		线程在运行状态过程中，可能有多个原因导致当前线程不继续运行下去，比如用户主动让线程睡眠（睡眠一定的时间之后再重新执行）、用户主动让线程等待，或者被同步块给阻塞，此时就对应着多个状态：time waiting（睡眠或等待一定的事件）、waiting（等待被唤醒）、blocked（阻塞）。

		当由于突然中断或者子任务执行完毕，线程就会被消亡。
		
		新建状态:
			使用 new 关键字和 Thread 类或其子类建立一个线程对象后，该线程对象就处于新建状态。它保持这个状态直到程序 start() 这个线程。

		就绪状态:
			当线程对象调用了start()方法之后，该线程就进入就绪状态。就绪状态的线程处于就绪队列中，要等待JVM里线程调度器的调度。

		运行状态:
			如果就绪状态的线程获取 CPU 资源，就可以执行 run()，此时线程便处于运行状态。处于运行状态的线程最为复杂，它可以变为阻塞状态、就绪状态和死亡状态。

		阻塞状态:
			如果一个线程执行了sleep（睡眠）、suspend（挂起）等方法，失去所占用资源之后，该线程就从运行状态进入阻塞状态。在睡眠时间已到或获得设备资源后可以重新进入就绪状态。可以分为三种：

				等待阻塞：运行状态中的线程执行 wait() 方法，使线程进入到等待阻塞状态。

				同步阻塞：线程在获取 synchronized 同步锁失败(因为同步锁被其他线程占用)。

				其他阻塞：通过调用线程的 sleep() 或 join() 发出了 I/O 请求时，线程就会进入到阻塞状态。当sleep() 状态超时，join() 等待线程终止或超时，或者 I/O 处理完毕，线程重新转入就绪状态。

		死亡状态:
			一个运行状态的线程完成任务或者其他终止条件发生时，该线程就切换到终止状态。
		
	线程的优先级：
		Java 线程的优先级是一个整数，其取值范围是 1 （Thread.MIN_PRIORITY ） - 10 （Thread.MAX_PRIORITY ）。默认情况下，每一个线程都会分配一个优先级 NORM_PRIORITY（5）。具有较高优先级的线程对程序更重要，并且应该在低优先级的线程之前分配处理器资源。但是，线程优先级不能保证线程执行的顺序，而且非常依赖于平台。
		
		每个线程在执行时都具有一定的优先级，优先级高的线程具有较多的执行机会。每个线程默认的优先级都与创建它的线程的优先级相同。main线程默认具有普通优先级。
		设置线程优先级：setPriority(int priorityLevel)。参数priorityLevel范围在1-10之间，常用的有如下三个静态常量值：
			MAX_PRIORITY:10
			MIN_PRIORITY:1
			NORM_PRIORITY:5

		获取线程优先级：getPriority()。
		
		注：
			具有较高线程优先级的线程对象仅表示此线程具有较多的执行机会，而非优先执行
	
	
	上下文切换：（此处切换时间也很需要考虑的）
		当在运行一个线程的过程中转去运行另外一个线程，这个叫做线程上下文切换（对于进程也是类似）。一般来说，线程上下文切换过程中会记录程序计数器、CPU寄存器状态等数据。
		说简单点的：
			对于线程的上下文切换实际上就是存储和恢复CPU状态的过程，它使得线程执行能够从中断点恢复执行。虽然多线程可以使得任务执行的效率得到提升，但是由于在线程切换时同样会带来一定的开销代价，并且多个线程会导致系统资源占用的增加，所以在进行多线程编程时要注意这些因素。
		
	Thread:
		private char name[];
		private int priority;
		private Thread threadQ;
		private long eetop;
		
		//Whether or not to single_step this thread
		private boolean single_step;
		
		//Whether or not the thread is a daemon thread.
		private boolean daemon = false;		//是否是守护线程
		
		//JVM state
		private boolean stillborn = false;
		
		//What will be run
		private Runnable target;		//要执行的任务
		
		//The group of this thread
		private ThreadGroup group;
		
		//The context ClassLoader for this thread
		private ClassLoader contextClassLoader;
		
		//The inherited AccessControlContext of this thread
		private AccessControlContext inheritedAccessControlContext;
		
	
	Thread方法：
		sleep:
			sleep(long millis)     //参数为毫秒
			sleep(long millis,int nanoseconds)    //第一参数为毫秒，第二个参数为纳秒
			sleep相当于让线程睡眠，交出CPU，让CPU去执行其他的任务。不会释放锁，也就是说如果当前线程持有对某个对象的锁，则即使调用sleep方法，其他线程也无法访问这个对象。
			当调用了新建的线程的start()方法后，线程进入到就绪状态，可能会在接下来的某个时间获取CPU时间片得以执行，如果希望这个新线程必然性的立即执行，直接调用原来线程的sleep(1)即可。
		
		yield（线程让步）:
			yield()
			调用yield方法会让当前线程交出CPU权限，让CPU去执行其他的线程。它跟sleep方法类似，同样不会释放锁。但是yield不能控制具体的交出CPU的时间，另外，yield方法只能让拥有相同优先级的线程有获取CPU执行时间的机会。

			注意，调用yield方法并不会让线程进入阻塞状态，而是让线程重回就绪状态，它只需要等待重新获取CPU执行时间，这一点是和sleep方法不一样的。
		
		join:
			join()
			join(long millis)	//参数为毫秒
			join(long millis,int nanoseconds)	//第一参数为毫秒，第二参数为纳秒
			让一个线程等待另一个线程完成才继续执行。
			假如在main线程中，调用thread.join方法，则main方法会等待thread线程执行完毕或者等待一定的时间。如果调用的是无参join方法，则等待thread执行完毕，如果调用的是指定了时间参数的join方法，则等待一定的时间。
			实际上调用join()方法是调用了Object的wait()方法。相关源码如下：
				while(isAlive()){
					wait(0);
				}
			wait方法会让线程进入阻塞状态，并且会释放线程占有的锁，并交出CPU执行权限。由于wait方法会让线程释放对象锁，所以join方法同样会让线程释放对一个对象持有的锁。
		
		interrupt:
			interrupt()
			单独调用interrupt方法可以使得处于阻塞状态的线程抛出一个异常，也就说，它可以用来中断一个正处于阻塞状态的线程；另外，通过interrupt方法和isInterrupted()方法来停止正在运行的线程。如下：
				public class Test {
					 
					public static void main(String[] args) throws IOException  {
						Test test = new Test();
						MyThread thread = test.new MyThread();
						thread.start();
						try {
							Thread.currentThread().sleep(2000);
						} catch (InterruptedException e) {
							 
						}
						thread.interrupt();
					} 
					 
					class MyThread extends Thread{
						@Override
						public void run() {
							try {
								System.out.println("进入睡眠状态");
								Thread.currentThread().sleep(10000);
								System.out.println("睡眠完毕");
							} catch (InterruptedException e) {
								System.out.println("得到中断异常");
							}
							System.out.println("run方法执行完毕");
						}
					}
				}
				
			输出结果：
				进入睡眠状态
				得到中断异常
				run方法执行完毕
			
			非阻塞状态的线程，单独调用 interrupt()不能中断。可配合 interrupted() 综合使用来达到中断的目的。如下：
				public class Test {
     
					public static void main(String[] args) throws IOException  {
						Test test = new Test();
						MyThread thread = test.new MyThread();
						thread.start();
						try {
							Thread.currentThread().sleep(2000);
						} catch (InterruptedException e) {
							 
						}
						thread.interrupt();
					} 
					 
					class MyThread extends Thread{
						@Override
						public void run() {
							int i = 0;
							while(!isInterrupted() && i<Integer.MAX_VALUE){
								System.out.println(i+" while循环");
								i++;
							}
						}
					}
				}
			
		getId
			用来得到线程ID
		
		getName和setName
			用来得到或者设置线程名称。

		getPriority和setPriority
			用来获取和设置线程优先级。

		setDaemon和isDaemon
			用来设置线程是否成为守护线程和判断线程是否是守护线程。
			
			守护线程和用户线程的区别在于：守护线程依赖于创建它的线程，而用户线程则不依赖。举个简单的例子：如果在main线程中创建了一个守护线程，当main方法运行完毕之后，守护线程也会随着消亡。而用户线程则不会，用户线程会一直运行直到其运行完毕。在JVM中，像垃圾收集器线程就是守护线程。
		
		currentThread()
			静态方法currentThread()用来获取当前线程。
			
		holdsLock：
			holdsLock()
			当且仅当当前线程在指定的对象上保持监视器锁时，才返回 true。
			Object o = new Object();
			@Test
			public void test1() throws Exception {
				new Thread(new Runnable() {
					@Override
					public void run() {
						synchronized(o) {
							System.out.println("child thread: holdLock: " + 
								Thread.holdsLock(o));
						}
					}
				}).start();
				System.out.println("main thread: holdLock: " + Thread.holdsLock(o));
				Thread.sleep(2000);
			}
			
			main thread: holdLock: false
			child thread: holdLock: true
		
		
		dumpStack：
			dumpStack()
			将当前线程的堆栈跟踪打印至标准错误流。

			
	
	sleep()方法和 wait()的区别：
		sleep()主要的意义就是让当前线程停止执行，让出cpu给其他的线程，但是不会释放对象锁资源以及监控的状态，当指定的时间到了之后又会自动恢复运行状态。
		
		wait()主要的意义就是让线程放弃当前的对象的锁，进入等待此对象的等待锁定池，只有针对此对象调动notify方法后本线程才能够进入对象锁定池准备获取对象锁进入运行状态。
	
	
	
	后台线程（Daemon Thread）
		概念/目的：
		后台线程主要是为其他线程（相对可以称之为前台线程）提供服务，或“守护线程”。如JVM中的垃圾回收线程。
		
		生命周期：
		后台线程的生命周期与前台线程生命周期有一定关联。主要体现在：当所有的前台线程都进入死亡状态时，后台线程会自动死亡。
		
		设置后台线程：
		调用Thread对象的setDaemon(true)方法可以将指定的线程设置为后台线程。
		
		判断线程是否是后台线程：
		调用thread对象的isDeamon()方法
		
		注：
			main线程默认是前台线程，前台线程创建中创建的子线程默认是前台线程，后台线程中创建的线程默认是后台线程。调用setDeamon(true)方法将前台线程设置为后台线程时，需要在start()方法调用之前。前台线程都死亡后，JVM通知后台线程死亡，但从接收指令到作出响应，需要一定的时间。
				
	
	创建一个线程：
		Java提供了三种创建线程的方法：
			1. 通过实现Runnable接口
			2. 通过继承Thread类本身
			3. 通过Callable 和 Future创建线程
			
		
		通过 Callable 和 Future 创建线程
			1. 创建 Callable 接口的实现类，并实现 call() 方法，该 call() 方法将作为线程执行体，并且有返回值。
			2. 创建 Callable 实现类的实例，使用 FutureTask 类来包装 Callable 对象，该 FutureTask 对象封装了该 Callable 对象的 call() 方法的返回值。
			3. 使用 FutureTask 对象作为 Thread 对象的 target 创建并启动新线程。
			4. 调用 FutureTask 对象的 get() 方法来获得子线程执行结束后的返回值。
			
		public class CallableThreadTest implements Callable<Integer> {
			public static void main(String[] args)  
			{  
				CallableThreadTest ctt = new CallableThreadTest();  
				FutureTask<Integer> ft = new FutureTask<>(ctt);  
				for(int i = 0;i < 100;i++)  
				{  
					System.out.println(Thread.currentThread().getName()+" 的循环变量i的值"+i);  
					if(i==20)  
					{  
						new Thread(ft).start();  
					}
				}  
				try  
				{  
					System.out.println("子线程的返回值："+ft.get());  
				} catch (InterruptedException e)  
				{  
					e.printStackTrace();  
				} catch (ExecutionException e)  
				{  
					e.printStackTrace();  
				}  
		  
			}
			@Override  
			public Integer call() throws Exception  
			{  
				int i = 0;  
				for(;i<100;i++)  
				{  
					System.out.println(Thread.currentThread().getName()+" "+i);  
				}  
				return i;  
			}  
		}
		
		
		public class FutureTask<V> implements RunnableFuture<V> {
			//.... 
		}
		
		public interface RunnableFuture<V> extends Runnable, Future<V> {
			void run();
		}
		于是，我们发现FutureTask类实际上是同时实现了Runnable和Future接口，由此才使得其具有Future和Runnable双重特性。通过Runnable特性，可以作为Thread对象的target，而Future特性，使得其可以取得新创建线程中的call()方法的返回值。
		
		注：
			原因在于通过ft.get()方法获取子线程call()方法的返回值时，当子线程此方法还未执行完毕，ft.get()方法会一直阻塞，直到call()方法执行完毕才能取到返回值。
	
	
	线程几个重要概念：
		线程同步
		线程间通信
		线程死锁
		线程控制：挂起、停止和恢复
	
	
	线程安全：
		是指多线程环境下对共享资源的访问可能会引起此共享资源的不一致性。
		
		1. 同步方法
			锁对象为当前方法所在的对象自身。
			
			public synchronized void run(){
				//....
			}
			
		2. 同步代码块
			
			synchronized(obj){
				//....
			}
			
			选择哪一个对象作为锁是至关重要的。一般情况下，都是选择此共享资源对象作为锁对象。
		
		3. Lock对象同步锁
			方便同步锁对象与共享资源解耦。需要注意的是Lock对象需要与资源对象同样具有一对一的关系。
			
			class X{
				//显示定义Lock同步锁对象，此对象与共享资源具有一对一关系
				private final Lock lock = new ReentrantLock();
				
				private void m(){
					//加锁
					lock.lock();
					
					//....需要进行线程安全同步的代码
					
					//释放Lock锁
					lock.unlock();
				}
			}
		
		4. wait()/notify()/notifyAll() 线程通信
			都是Object类中的本地方法。
			wait():
				导致当前线程等待并使其进入到等待阻塞状态。直到其他线程调用该同步锁对象的notify()或notifyAll()方法来唤醒此线程。

			notify()：
				唤醒在此同步锁对象上等待的单个线程，如果有多个线程都在此同步锁对象上等待，则会任意选择其中某个线程进行唤醒操作，只有当前线程放弃对同步锁对象的锁定，才可能执行被唤醒的线程。

			notifyAll()：
				唤醒在此同步锁对象上等待的所有线程，只有当前线程放弃对同步锁对象的锁定，才可能执行被唤醒的线程。
			
			
			注：
				1.wait()方法执行后，当前线程立即进入到等待阻塞状态，其后面的代码不会执行；

				2.notify()/notifyAll()方法执行后，将唤醒此同步锁对象上的（任意一个-notify()/所有-notifyAll()）线程对象，但是，此时还并没有释放同步锁对象，也就是说，如果notify()/notifyAll()后面还有代码，还会继续进行，知道当前线程执行完毕才会释放同步锁对象；

				3.notify()/notifyAll()执行后，如果右面有sleep()方法，则会使当前线程进入到阻塞状态，但是同步对象锁没有释放，依然自己保留，那么一定时候后还是会继续执行此线程，接下来同2；

				4.wait()/notify()/nitifyAll()完成线程间的通信或协作都是基于不同对象锁的，因此，如果是不同的同步对象锁将失去意义，同时，同步对象锁最好是与共享资源对象保持一一对应关系；

				5.当wait线程唤醒后并执行时，是接着上次执行到的wait()方法代码后面继续往下执行的。
		
		
		
	
	