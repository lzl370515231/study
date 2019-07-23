# Java的锁

## 锁的内存语义

- 锁可以让临界区互斥执行，还可以让释放锁的线程向同一个锁的线程发送消息
- 锁的释放要遵循Happens-before原则（`锁规则：解锁必然发生在随后的加锁之前`）
- 锁在Java中的具体表现是 `Synchronized` 和 `Lock`

## 锁操作

### 锁的释放

线程A释放锁后，会将共享变更操作刷新到主内存中。

![](.\..\png\java_lock_release.jpg)

### 锁的获取

线程B获取锁时，JMM会将该线程的本地内存置为无效，被监视器保护的临界区必须从主内存中读取共享变量

![](.\..\png\java_lock_acquire.jpg)

### 锁的释放与获取过程

- 锁获取与 volatile 读有相同的内存语义，可见volatile.md 文档
- 线程A释放一个锁，实质是线程A告知下一个获取到该锁的某个线程其已变更该共享变量
- 线程B获取一个锁，实质是线程B得到了线程A告知其(在释放锁之前)变更共享变量的消息
- 线程A释放锁，随后线程B竞争到该锁，实质是线程A通过主内存向线程B发消息告知其变更了共享变量

## 锁对象、类锁、私有锁

### 对象锁

使用 synchronized 修饰**非静态**的方法以及 synchronized(this) 同步代码块使用的锁是对象锁。

### 类锁 

使用 synchronized 修饰**静态**的方法以及 synchronized(**class**) 同步代码块使用的锁是类锁

### 私有锁

在类内部声明一个私有属性如private Object lock，在需要加锁的同步块使用 synchronized(lock）

特殊的instance变量

```java
private byte[] lock = new byte[0];	//特殊的instance变量
public void method(){
    synchronized(lock){
        //TODO 同步代码块
    }
}
```

#### 说明

零长度的byte数组对象创建起来将比任何对象都经济――查看编译后的字节码：生成零长度的byte[]对象只需3条操作码，而Object lock = new Object()则需要7行操作码。

### 特性

- 对象锁具有可重入性
- 当一个线程获得了某个对象的对象锁，则该线程仍然可以调用其他任何需要该对象锁的 synchronized 方法或 synchronized(this) 同步代码块
- 当一个线程访问某个对象的一个 synchronized(this) 同步代码块时，其他线程对该对象中所有其它 synchronized(this) 同步代码块的访问将被阻塞，因为访问的是同一个对象锁。
- 每个类只有一个类锁，但是类可以实例化成对象，因此每一个对象对应一个对象锁
- 类锁和对象锁不会产生竞争。
- 私有锁和对象锁也不会产生竞争。
- 使用私有锁可以减小锁的细粒度，减少由锁产生的开销。

由私有锁实现的等待/通知机制：

```java
Object lock = new Object();
//由等待方线程实现
synchronized(lock){
    while(条件不满足){
        lock.wait();
    }
}

//由通知方线程实现
synchronized(lock){
    //条件发生改变
    lock.notify();
}
```

## 死锁

### 概念

死锁是两个或更多线程阻塞着等待其它处于死锁状态的线程所持有的锁。死锁通常发生在多个线程同时但以不同的顺序请求**同一组锁**的时候。

### 避免死锁

#### 加锁顺序

如果一个线程（比如线程 3）需要一些锁，那么它必须按照确定的顺序获取锁。它只有获得了从顺序上排在前面的锁之后，才能获取后面的锁。

```java
Thread 1:
	lock A 
	lock B

Thread 2:
	wait for A
	lock C (when A locked)

Thread 3:
	wait for A
	wait for B
	wait for C
```

按照顺序加锁是一种有效的死锁预防机制。但是，这种方式需要你事先知道所有可能会用到的锁，但总有些时候是无法预知的。

#### 加锁时限

在尝试**获取锁的时候加一个超时时间**，这也就意味着在尝试获取锁的过程中若超过了这个时限该线程则放弃对该锁请求。若一个线程没有在给定的时限内成功获得所有需要的锁，则会进行回退并释放所有已经获得的锁，然后等待一段随机的时间再重试。这段随机的等待时间让其它线程有机会尝试获取相同的这些锁，并且让该应用在没有获得锁的时候可以继续运行。

#### 死锁检测

死锁检测是一个更好的死锁预防机制，它主要是针对那些不可能实现按序加锁并且锁超时也不可行的场景。

##### 原理

每当一个线程获得了锁，会在线程和锁相关的数据结构中（map、graph 等等）将其记下。除此之外，每当有线程请求锁，也需要记录在这个数据结构中。

当一个线程请求锁失败时，这个线程可以遍历锁的关系图看看是否有死锁发生。

## 锁分类（概念）

![](.\..\png\java_lock.png)

### 乐观锁 VS 悲观锁

#### 悲观锁

对于同一个数据的并发操作，悲观锁认为自己在使用数据的时候一定有别的线程来修改数据，因此在获取数据的时候会先加锁，确保数据不会被别的线程修改。Java中，synchronized关键字和Lock的实现类都是悲观锁。

#### 乐观锁

乐观锁认为自己在使用数据时不会有别的线程修改数据，所以不会添加锁，只是在更新数据的时候去判断之前有没有别的线程更新了这个数据。如果这个数据没有被更新，当前线程将自己修改的数据成功写入。如果数据已经被其他线程更新，则根据不同的实现方式执行不同的操作（例如报错或者自动重试）。

乐观锁在Java中是通过使用无锁编程来实现，最常采用的是**CAS算法**，Java原子类中的递增操作就通过CAS自旋实现的。

jdk5增加了并发包java.util.concurrent.*,其下面的类使用CAS算法实现了区别于synchronouse同步锁的一种乐观锁。

##### CAS算法（比较与交换）

CAS是一种无锁算法，CAS有3个操作数，内存值V，旧的预期值A，要修改的新值B。当且仅当预期值A和内存值V相同时，将内存值V修改为B，否则什么都不做。

就是指当两者进行比较时，如果相等，则证明共享数据没有被修改，替换成新值，然后继续往下运行；如果不相等，说明共享数据已经被修改，放弃已经所做的操作，然后重新执行刚才的操作。容易看出 CAS 操作是基于共享数据不会被修改的假设，采用了类似于数据库的commit-retry 的模式。当同步冲突出现的机会很少时，这种假设能带来较大的性能提升。

![](.\..\png\java_lock_悲观和乐观.png)

##### 特性

- 通过调用JNI的代码实现
- 非阻塞算法
- 非独占锁

##### 乐观锁CAS缺点

- ABA问题。

  CAS需要在操作值的时候检查内存值是否发生变化，没有发生变化才会更新内存值。但是如果内存值原来是A，后来变成了B，然后又变成了A，那么CAS进行检查时会发现值没有发生变化，但是实际上是有变化的。ABA问题的解决思路就是在变量前面添加版本号，每次变量更新的时候都把版本号加一，这样变化过程就从“A－B－A”变成了“1A－2B－3A”。

  - JDK从1.5开始提供了**AtomicStampedReference**类来解决ABA问题，具体操作封装在compareAndSet()中。compareAndSet()首先检查当前引用和当前标志与预期引用和预期标志是否相等，如果都相等，则以原子方式将引用值和标志的值设置为给定的更新值。

- 循环时间长开销大。

  CAS操作如果长时间不成功，会导致其一直自旋，给CPU带来非常大的开销。

- 只能保证一个共享变量的原子操作。

  对一个共享变量执行操作时，CAS能够保证原子操作，但是对多个共享变量操作时，CAS是无法保证操作的原子性的。

  - Java从1.5开始JDK提供了**AtomicReference**类来保证引用对象之间的原子性，可以把多个变量放在一个对象里来进行CAS操作。

#### 使用场景

- 悲观锁适合写操作多的场景，先加锁可以保证写操作时数据正确。
- 乐观锁适合读操作多的场景，不加锁的特点能够使其读操作的性能大幅提升。

#### 示例

```java
// ------------------------- 悲观锁的调用方式 -------------------------
// synchronized
public synchronized void testMethod() {
	// 操作同步资源
}
// ReentrantLock
private ReentrantLock lock = new ReentrantLock(); // 需要保证多个线程使用的是同一个锁
public void modifyPublicResources() {
	lock.lock();
	// 操作同步资源
	lock.unlock();
}

// ------------------------- 乐观锁的调用方式 -------------------------
private AtomicInteger atomicInteger = new AtomicInteger();  // 需要保证多个线程使用的是同一个AtomicInteger
atomicInteger.incrementAndGet(); //执行自增1
```

java.util.concurrent包中的原子类，就是通过CAS来实现了乐观锁，那么我们进入原子类AtomicInteger的源码，看一下AtomicInteger的定义：

```java
public class AtomicInteger extends Number implements java.io.Serializable{
	private static final long serialVersionUID = 6214790243416807050L;
	
	//setup to use Unsafe.compareAndSwapInt for updates
	private static final Unsafe unsafe = Unsafe.getUnsafe();
	private static final long valueOffset;
    
    static{
        try{
            valueOffset = unsafe.objectFieldOffset(AtomicInteger.class.getDeclaredField("value"));
        }catch(Exception e){
            throw new Error(ex);
        }
    }
    private volatile int value;
}
```

unsafe：获取并操作内存的数据

valueOffset：存储value在AtomicInteger中的偏移量

value：存储AtomicInteger的int值，该属性需要借助volatile关键字保证其在线程间是可见的。

```java
// ------------------------- JDK 8 -------------------------
// AtomicInteger 自增方法
public final int incrementAndGet() {
  return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
}

// Unsafe.class
public final int getAndAddInt(Object var1, long var2, int var4) {
  int var5;
  do {
      var5 = this.getIntVolatile(var1, var2);
  } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
  return var5;
}

// ------------------------- OpenJDK 8 -------------------------
// Unsafe.java
public final int getAndAddInt(Object o, long offset, int delta) {
   int v;
   do {
       v = getIntVolatile(o, offset);
   } while (!compareAndSwapInt(o, offset, v, v + delta));
   return v;
}
```

getAndAddInt()循环获取给定对象o中的偏移量处的值v，然后判断内存值是否等于v。如果相等则将内存值设置为 v + delta，否则返回false，继续循环进行重试，直到设置成功才能退出循环，并且将旧值返回。整个“比较+更新”操作封装在compareAndSwapInt()中，在JNI里是借助于一个CPU指令完成的，属于原子操作，可以保证多个线程都能够看到同一个变量的修改值。

##### 总结

乐观锁的使用：通过CAS算法保证数据的同步性或者 CAS算法+版本号来保证数据的同步性。

### 自旋锁 VS 适应性自旋锁

#### 自旋锁

阻塞或唤醒一个Java线程需要操作系统切换CPU状态来完成，这种状态转换需要耗费处理器时间。如果同步代码块中的内容过于简单，状态转换消耗的时间有可能比用户代码执行的时间还要长。

在许多场景中，同步资源的锁定时间很短，为了这一小段时间去切换线程，线程挂起和恢复现场的花费可能会让系统得不偿失。如果物理机器有多个处理器，能够让两个或以上的线程同时并行执行，我们就可以让后面那个请求锁的线程不放弃CPU的执行时间，看看持有锁的线程是否很快就会释放锁。

而为了让当前线程“稍等一下”，我们需让当前线程进行自旋，如果在自旋完成后前面锁定同步资源的线程已经释放了锁，那么当前线程就可以不必阻塞而是直接获取同步资源，从而避免切换线程的开销。这就是自旋锁。

![](.\..\png\java_自旋锁.png)

自旋锁本身是有缺点的，它不能代替阻塞。自旋等待虽然避免了线程切换的开销，但它要占用处理器时间。如果锁被占用的时间很短，自旋等待的效果就会非常好。反之，如果锁被占用的时间很长，那么自旋的线程只会白浪费处理器资源。所以，自旋等待的时间必须要有一定的限度，如果自旋超过了限定次数（默认是10次，可以使用-XX:PreBlockSpin来更改）没有成功获得锁，就应当挂起线程。

**自旋锁的实现原理同样也是CAS**，AtomicInteger中调用unsafe进行自增操作的源码中的do-while循环就是一个自旋操作，如果修改数值失败则通过循环来执行自旋，直至修改成功。

```java
public final int getAndAddInt(Object var1,long var2,int var4){
    int var5;
    do{
        var5 =  this.getIntVolatile(var1,var2);
    }while(!this.compareAndSwapInt(var1,var2,var5,var5+var4));
    return var5;
}
```

自旋锁在JDK1.4.2中引入，使用-XX:+UseSpinning来开启。JDK 6中变为默认开启，并且引入了自适应的自旋锁（适应性自旋锁）。

#### 适应性自旋锁

自适应意味着自旋的时间（次数）不再固定，而是由前一次在同一个锁上的自旋时间及锁的拥有者的状态来决定。如果在同一个锁对象上，自旋等待刚刚成功获得过锁，并且持有锁的线程正在运行中，那么虚拟机就会认为这次自旋也是很有可能再次成功，进而它将允许自旋等待持续相对更长的时间。如果对于某个锁，自旋很少成功获得过，那在以后尝试获取这个锁时将可能省略掉自旋过程，直接阻塞线程，避免浪费处理器资源。

在自旋锁中另有三种常见的锁形式：TicketLock、CLHlock 和MCSlock。

### 无锁 VS 偏向锁 VS 轻量级锁 VS 重量级锁

| 锁状态   | 存储内容                                                | 存储内容 |
| -------- | ------------------------------------------------------- | -------- |
| 无锁     | 对象的hashCode、对象分代年龄、是否是偏向锁（0）         | 01       |
| 偏向锁   | 偏向线程ID、偏向时间戳、对象分代年龄、是否是偏向锁（1） | 01       |
| 轻量级锁 | 指向栈中锁记录的指针                                    | 00       |
| 重量级锁 | 指向互斥量（重量级锁）的指针                            | 10       |

#### 无锁

无锁没有对资源进行锁定，所有的线程都能访问并修改同一个资源，但同时只有一个线程能修改成功。

无锁的特点就是修改操作在循环内进行，线程会不断的尝试修改共享资源。如果没有冲突就修改成功并退出，否则就会继续循环尝试。如果有多个线程修改同一个值，必定会有一个线程能修改成功，而其他修改失败的线程会不断重试直到修改成功。上面我们介绍的CAS原理及应用即是无锁的实现。无锁无法全面代替有锁，但无锁在某些场合下的性能是非常高的。

#### 偏向锁

偏向锁是指**一段同步代码一直被一个线程所访问**，那么该线程会自动获取锁，降低获取锁的代价。

在大多数情况下，锁总是由同一线程多次获得，不存在多线程竞争，所以出现了偏向锁。其目标就是在只有一个线程执行同步代码块时能够提高性能。

当一个线程访问同步代码块并获取锁时，会在Mark Word里存储锁偏向的线程ID。在线程进入和退出同步块时不再通过CAS操作来加锁和解锁，而是检测Mark Word里是否存储着指向当前线程的偏向锁。引入偏向锁是为了在无多线程竞争的情况下尽量减少不必要的轻量级锁执行路径，因为轻量级锁的获取及释放依赖多次CAS原子指令，而偏向锁只需要在置换ThreadID的时候依赖一次CAS原子指令即可。

偏向锁只有遇到其他线程尝试竞争偏向锁时，持有偏向锁的线程才会释放锁，线程不会主动释放偏向锁。偏向锁的撤销，需要等待全局安全点（在这个时间点上没有字节码正在执行），它会首先暂停拥有偏向锁的线程，判断锁对象是否处于被锁定状态。撤销偏向锁后恢复到无锁（标志位为“01”）或轻量级锁（标志位为“00”）的状态。

偏向锁在JDK 6及以后的JVM里是默认启用的。可以通过JVM参数关闭偏向锁：-XX:-UseBiasedLocking=false，关闭之后程序默认会进入轻量级锁状态。

#### 轻量级锁

是指当锁是偏向锁的时候，被另外的线程所访问，**偏向锁就会升级为轻量级锁，其他线程会通过自旋的形式尝试获取锁，不会阻塞，从而提高性能**。

在代码进入同步块的时候，如果同步对象锁状态为无锁状态（锁标志位为“01”状态，是否为偏向锁为“0”），虚拟机首先将在当前线程的栈帧中建立一个名为锁记录（Lock Record）的空间，用于存储锁对象目前的Mark Word的拷贝，然后拷贝对象头中的Mark Word复制到锁记录中。

拷贝成功后，虚拟机将使用CAS操作尝试将对象的Mark Word更新为指向Lock Record的指针，并将Lock Record里的owner指针指向对象的Mark Word。

如果这个更新动作成功了，那么这个线程就拥有了该对象的锁，并且对象Mark Word的锁标志位设置为“00”，表示此对象处于轻量级锁定状态。

如果轻量级锁的更新操作失败了，虚拟机首先会检查对象的Mark Word是否指向当前线程的栈帧，如果是就说明当前线程已经拥有了这个对象的锁，那就可以直接进入同步块继续执行，否则说明多个线程竞争锁。

若当前只有一个等待线程，则该线程通过自旋进行等待。但是当自旋超过一定的次数，或者一个线程在持有锁，一个在自旋，又有第三个来访时，轻量级锁升级为重量级锁。

#### 重量锁

升级为重量级锁时，锁标志的状态值变为“10”，此时Mark Word中存储的是指向重量级锁的指针，此时等待锁的线程都会进入阻塞状态。



综上，偏向锁通过**对比Mark Word**解决加锁问题，避免执行CAS操作。而轻量级锁是通过用**CAS操作和自旋**来解决加锁问题，避免线程阻塞和唤醒而影响性能。重量级锁是将除了拥有锁的线程以外的线程都阻塞。

### 公平锁 VS 非公平锁

#### 公平锁

公平锁是指多个线程按照申请锁的顺序来获取锁，**线程直接进入队列中排队，队列中的第一个线程才能获得锁**。公平锁的优点是等待锁的线程不会饿死。缺点是整体吞吐效率相对非公平锁要低，等待队列中除第一个线程以外的所有线程都会阻塞，CPU唤醒阻塞线程的开销比非公平锁大。

#### 非公平锁

非公平锁是**多个线程加锁时直接尝试获取锁，获取不到才会到等待队列的队尾等待**。但如果此时锁刚好可用，那么这个线程可以无需阻塞直接获取到锁，所以非公平锁有可能出现后申请锁的线程先获取锁的场景。非公平锁的优点是可以减少唤起线程的开销，整体的吞吐效率高，因为线程有几率不阻塞直接获得锁，CPU不必唤醒所有线程。缺点是处于等待队列中的线程可能会饿死，或者等很久才会获得锁。

通过ReentrantLock的源码来讲解公平锁和非公平锁

```java
public class ReentrantLock implements Lock,java.io.Serializable{
    private static final long serialVersion = 7373984872572414699L;
    private final Sync sync;
    
    abstarct static class Sync extends AbstractQueuedSynchronizer{...}
    
    static final class NonfairSync extends Sync{...}
    
    static final class FairSync extends Sync{...}
    
    public ReentrantLock(){ sync = new  NonfairSync();}
    
    public ReentrantLock(boolean fair){sync = fair ? new FairSync():new NonfairSync();}
}
```

根据代码可知，ReentrantLock里面有一个内部类Sync，Sync继承AQS（AbstractQueuedSynchronizer），添加锁和释放锁的大部分操作实际上都是在Sync中实现的。它有公平锁FairSync和非公平锁NonfairSync两个子类。ReentrantLock默认使用非公平锁，也可以通过构造器来显示的指定使用公平锁。

#### 公平锁代码

```java
protected final boolean tryAcquire(int acquires){
    final Thread current = Thread.currentThread();
    int c = getState();
    if(c == 0){
        if(!hasQueuePrecessors() && compareAndSetState(0,acquires)){
            setExclusiveOwnerThread(current);
            return true;
        }
    }else if(current == getExclusiveOwnerThread()){
        int nextc = c+ acquires;
        if(next < 0){
            throw new Error("Maxium lock count exceeded");
        }
        setState(nextc);
        return true;
    }
    return false;
}
```

#### 非公平锁代码

```java
final boolean nonfairTryAcquire(int acquires){
    final Thread current = Thread.currentThread();
    int c = getState();
    if(c == 0){
        if(compareAndSetState(0,acquires)){
            setExclusiveOwnerThread(current);
            return true;
        }
    }else if(current == getExclusiveOwnerThread()){
        int nextc = c +acquires;
        if(nextc<0){
            throw new Error("Maxium lock count exceeded");
        }
        setState(nextc);
        return true;
    }
    return false;
}
```

#### 同步与非同步的差别

```java
public final boolean hasQueuedPredecessors(){
    //...
    Node t = tail;
    Node h = head;
    Node s;
    return h != t && ((s = h.next) == null || s.thread != Thread.currentThread());
}
```

该方法主要判断当前线程是否位于同步队列中的第一个。如果是则返回true，否则返回false。

### 可重入锁 VS 非可重入锁

#### 可重入锁

可重入锁又名递归锁，是指在同一个线程在外层方法获取锁的时候，再进入该线程的内层方法会自动获取锁（前提锁对象得是同一个对象或者class），不会因为之前已经获取过还没释放而阻塞。Java中**ReentrantLock和synchronized都是可重入锁**，可重入锁的一个优点是可一定程度避免死锁。

```java
public class Widget{
    public synchronized void doSomething(){
        System.out.println("方法1执行....");
        doOthers();
    }
    public synchronized void doOthers(){
        System.out.println("方法2执行...");
    }
}
```

因为内置锁是可重入的，所以同一个线程在调用doOthers()时可以直接获得当前对象的锁，进入doOthers()进行操作。

#### 非可重入锁

NonReentrantLock为非可重入锁。

通过重入锁ReentrantLock以及非可重入锁NonReentrantLock的源码来对比分析一下为什么非可重入锁在重复调用同步资源时会出现死锁。

首先ReentrantLock和NonReentrantLock都继承父类**AQS**，其父类AQS中维护了一个同步状态status来计数重入次数，status初始值为0。

##### 非可重入锁死锁的原因

###### 获取锁

当线程尝试获取锁时，可重入锁先尝试获取并更新status值，如果status == 0表示没有其他线程在执行同步代码，则把status置为1，当前线程开始执行。如果status != 0，则判断当前线程是否是获取到这个锁的线程，如果是的话执行status+1，且当前线程可以再次获取锁。而非可重入锁是直接去获取并尝试更新当前status的值，如果status != 0的话会导致其获取锁失败，当前线程阻塞。

###### 释放锁

释放锁时，可重入锁同样先获取当前status的值，在当前线程是持有锁的线程的前提下。如果status-1 == 0，则表示当前线程所有重复获取锁的操作都已经执行完毕，然后该线程才会真正释放锁。而非可重入锁则是在确定当前线程是持有锁的线程之后，直接将status置为0，将锁释放。

#### 可重入锁代码

```java
final boolean nonfairTryAcquire(int acquires){
    final Thread current = Thread.currentThread();
    int c = getState();
    if(c == 0){
        if(compareAndSetState(0,acquires)){
            setExclusiveOwnerThread(current);
            return true;
        }
    }else if(current == getExclusiveOwnerThread()){
        int nextc = c+acquires;
        if(nextc <0){
            throw new Error("Maxium lock count exceeded");
        }
        setState(nextc);
        return true;
    }
    return false;
}
//获取锁时先判断，如果当前线程就是已经占有锁的线程，则status值+1，并返回true。

protected final boolean tryRelease(){
    int c =  getState() - release;
    if(Thread.currentThread() != getExclusiveOwnerThread()){
        throw new IllegalMonitorStateException();
    }
    boolean free = false;
    if(c == 0){
        free = false;
        setExclusiveOwnerThread(null);
    }
    setState(c);
    return free;
}
//释放锁时也是先判断当前线程是否已占有锁的线程，然后再判断status，如果status等于0，才真正的释放锁
```

#### 非可重入锁

```java
protected boolean tryAcquire(int acquires){
    if(this.compareAndSetState(0,1)){
        this.owner = Thread.currentThread();
        return true;
    }else {
        return false;
    }
}
//非重入锁是直接尝试获取锁

protected boolean tryRelease(int releases){
    if(Thread.currentThread() != this.owner){
        throw new IllegalMonitorStateException();
    }else{
        this.owner = null;
        this.setState(0);
        return true;
    }
}
//释放锁时直接将status 置为0
```

### 分段锁

分段锁其实是一种锁的设计，并不是具体的一种锁，对于`ConcurrentHashMap`而言，其并发的实现就是通过分段锁的形式来实现高效的并发操作。

`ConcurrentHashMap`中的分段锁称为Segment，它即类似于HashMap（JDK7与JDK8中HashMap的实现）的结构，即内部拥有一个Entry数组，数组中的每个元素又是一个链表；同时又是一个ReentrantLock（Segment继承了ReentrantLock)。

当需要put元素的时候，并不是对整个hashmap进行加锁，而是先通过hashcode来知道他要放在那一个分段中，然后对这个分段进行加锁，所以当多线程put的时候，只要不是放在一个分段中，就实现了真正的并行的插入。

但是，在统计size的时候，可就是获取hashmap全局信息的时候，就需要获取所有的分段锁才能统计。

分段锁的设计目的是细化锁的粒度，当操作不需要更新整个数组的时候，就仅仅针对数组中的一项进行加锁操作。

###  独享锁 VS 共享锁

#### 独享锁

独享锁也叫排他锁，是指该锁一次只能被一个线程所持有。如果线程T对数据A加上排它锁后，则其他线程不能再对A加任何类型的锁。获得排它锁的线程即能读数据又能修改数据。JDK中的synchronized和JUC（java.utils.current）中Lock的实现类就是互斥锁。

#### 共享锁

共享锁是指该锁可被多个线程所持有。如果线程T对数据A加上共享锁后，则其他线程只能对A再加共享锁，不能加排它锁。获得共享锁的线程只能读数据，不能修改数据。**ReentrantReadWriteLock**

#### 实现

独享锁与共享锁也是通过AQS来实现的，通过实现不同的方法，来实现独享或者共享。

##### ReentrantReadWriteLock的部分源码：

```java
public class ReentrantReadWriteLock implements ReadWriteLock,java.io.Serializable{
    private static final long serialVersionUID = -5992448646407608164L;
    
    private final ReentrantReadWriteLock.ReadLock readerLock;
    
    private final ReentrantWriteLock.WriteLock writeLock;
    
    final Sync sync;
    
    public ReentrantReadWriteLock(this(false));
    
    public ReentrantReadWriteLock(boolean fair){
        sync= fair ? new FairSync() : new NonFairSync();
        readLock = new ReadLock(this);
        writeLock = new WriteLock(this);
    }
    public ReentrantReadWriteLock.WriteLock writeLock(){
        return writeLock;
    }
    public ReentrantReadWriteLock.ReadLock readLock(){
        return readLock;
    }
}
```

##### 读锁

读锁为一个可重入的共享锁，能够被多个线程同时持有，在没有其他写线程访问时，读锁总是获取成功。

###### ReadLock

```java
public static class ReadLock implements Lock,java.io.Serializable{
    private static final long serialVersionUID = -5992648646407699164L;
    private final Sync sync;
    
    protected ReadLock(ReentrantReadWriteLock lock){
        sync = lock.sync;
    }
}
```

###### 读锁的获取

```java
public void lock(){
	sync.acquireShared(1);
}
```

```java
public final void acquireShared(int arg){
	if(tryAcquireShared(arg)<0){
		doAcquireShared(arg);
	}
}
```

###### 读锁的释放

```java
public void unlock(){
	sync.releaseShared(1);
}
```

```java
public final boolean releaseShared(int arg) {
	if (tryReleaseShared(arg)) {
		doReleaseShared();
		return true;
	}
	return false;
}
```

##### 写锁

写锁就是一个支持可重入的排他锁。

###### WriteLock

```java
public static class WriteLock implements Lock,java.io.Serializable{
    private static final long serialVersionUID = -4992448646487698164L;
    private final Sync sync;
    
    protected WriteLock(ReentrantReadWriteLock lock){
        sync = lock.sync;
    }
}
```

###### 写锁的获取

写锁的获取最终会调用tryAcquire(int arg)，该方法在内部类Sync中实现。（见写锁的加锁源码）

###### 写锁的释放

获取了写锁用完了则需要释放，WriteLock提供了unlock()方法释放写锁：

```java
public void unlock(){
    sync.release(1);
}
public final boolean release(int arg){
    if(tryRelease(arg)){
        Node h = head;
        if(h!=null && h.waitStatus !== 0){
            unparkSuccessor(h);
        }
        return true;
    }
    return false;
}
```

可以看到ReentrantReadWriteLock有两把锁：ReadLock和WriteLock，由词知意，一个读锁一个写锁，合称“读写锁”。再进一步观察可以发现ReadLock和WriteLock是靠内部类Sync实现的锁。Sync是AQS的一个子类，这种结构在CountDownLatch、ReentrantLock、Semaphore里面也都存在。

在ReentrantReadWriteLock里面，读锁和写锁的锁主体都是Sync，但读锁和写锁的加锁方式不一样。读锁是共享锁，写锁是独享锁。读锁的共享锁可保证并发读非常高效，而读写、写读、写写的过程互斥，因为读锁和写锁是分离的。所以ReentrantReadWriteLock的并发性相比一般的互斥锁有了很大提升。

在独享锁中这个值通常是0或者1（如果是重入锁的话state值就是重入的次数），在共享锁中state就是持有锁的数量。但是在ReentrantReadWriteLock中有读、写两把锁，所以需要在一个整型变量state上分别描述读锁和写锁的数量（或者也可以叫状态）。于是将state变量“按位切割”切分成了两个部分，高16位表示读锁状态（读锁个数），低16位表示写锁状态（写锁个数）。如下图：

![](.\..\png\java_sync_state_共享锁.png)

##### 读写锁的主要特性

1. 公平性：支持公平性和非公平性
2. 重入性：支持重入。读写锁最多支持 65535 个递归写入锁和 65535 个递归读取锁。
3. 锁降级：遵循**获取写锁、获取读锁再释放写锁**的次序，写锁能够降级成为读锁。（占有写锁的线程可以获取读锁）

##### 写锁的加锁源码

```java
protected final boolean tryAcquire(int acquires){
    Thread current = Thread.currentThread();
    int c = getState();	//获取当前锁的个数
    int w = exclusiveCount(c);	//获取写锁的个数
    if(c != 0){//如果已经有线程持有了锁 (c!=0)
// (Note: if c != 0 and w == 0 then shared count != 0)
		if (w == 0 || current != getExclusiveOwnerThread()) // 如果写线程数（w）为0（换言之存在读锁） 或者持有锁的线程不是当前线程就返回失败
			return false;
		if (w + exclusiveCount(acquires) > MAX_COUNT)    // 如果写入锁的数量大于最大数（65535，2的16次方-1）就抛出一个Error。
			throw new Error("Maximum lock count exceeded");
		// Reentrant acquire
		setState(c + acquires);
		return true;
	}
	if (writerShouldBlock() || !compareAndSetState(c, c + acquires)) 
	// 如果当且写线程数为0，并且当前线程需要阻塞那么就返回失败；或者如果通过CAS增加写线程数失败也返回失败。
		return false;
	setExclusiveOwnerThread(current); 
    // 如果c=0，w=0或者c>0，w>0（重入），则设置当前线程或锁的拥有者
	return true;
}
```

- 首先取到当前锁的个数c，然后再通过c来获取写锁的个数w。因为写锁是低16位，所以取低16位的最大值与当前的c做与运算（ int w = exclusiveCount©; ），高16位和0与运算后是0，剩下的就是低位运算的值，同时也是持有写锁的线程数目。
- 在取到写锁线程的数目后，首先判断是否已经有线程持有了锁。如果已经有线程持有了锁(c!=0)，则查看当前写锁线程的数目，如果写线程数为0（即此时存在读锁）或者持有锁的线程不是当前线程就返回失败（涉及到公平锁和非公平锁的实现）。
- 如果写入锁的数量大于最大数（65535，2的16次方-1）就抛出一个Error
- 如果当且写线程数为0（那么读线程也应该为0，因为上面已经处理c!=0的情况），并且当前线程需要阻塞那么就返回失败；如果通过CAS增加写线程数失败也返回失败
- 如果c=0,w=0或者c>0,w>0（重入），则设置当前线程或锁的拥有者，返回成功

只有等待其他读线程都释放了读锁，写锁才能被当前线程获取，而写锁一旦被获取，则其他读写线程的后续访问均被阻塞。写锁的释放与ReentrantLock的释放过程基本类似，每次释放均减少写状态，当写状态为0时表示写锁已被释放，然后等待的读写线程才能够继续访问读写锁，同时前次写线程的修改对后续的读写线程可见。

##### 读锁的代码

```java
protected final int tryAcquireShared(int unused) {
    Thread current = Thread.currentThread();
    int c = getState();
    if (exclusiveCount(c) != 0 &&
        getExclusiveOwnerThread() != current)
        return -1;                                   
    // 如果其他线程已经获取了写锁，则当前线程获取读锁失败，进入等待状态
    int r = sharedCount(c);
    if (!readerShouldBlock() &&
        r < MAX_COUNT &&
        compareAndSetState(c, c + SHARED_UNIT)) {
        if (r == 0) {
            firstReader = current;
            firstReaderHoldCount = 1;
        } else if (firstReader == current) {
            firstReaderHoldCount++;
        } else {
            HoldCounter rh = cachedHoldCounter;
            if (rh == null || rh.tid != getThreadId(current))
                cachedHoldCounter = rh = readHolds.get();
            else if (rh.count == 0)
                readHolds.set(rh);
            rh.count++;
        }
        return 1;
    }
    return fullTryAcquireShared(current);
}
```

在tryAcquireShared(int unused)方法中，如果其他线程已经获取了写锁，则当前线程获取读锁失败，进入等待状态。如果当前线程获取了写锁或者写锁未被获取，则当前线程（线程安全，依靠CAS保证）增加读状态，成功获取读锁。读锁的每次释放（线程安全的，可能有多个读线程同时释放读锁）均减少读状态，减少的值是“1<<16”。所以读写锁才能实现读读的过程共享，而读写、写读、写写的过程互斥。

##### HoldCounter

与线程有关的状态计数器。

## synchronized

### 基础

Java中的每一个对象都可以作为锁。

### 使用方式

- 修饰代码块
- 修饰方法
- 修饰一个静态方法
- 修饰一个类

### 状态

synchronized 同步锁一共包含四种状态：无锁、偏向锁、轻量级锁、重量级锁，它会随着竞争情况逐渐升级。synchronized 同步锁**可以升级但是不可以降级**，目的是为了提高获取锁和释放锁的效率。

### 特性

java中synchronized关键字是同步锁，同步锁是依赖于对象而存在的，而且每一个对象有且仅有一个同步锁。当我们调用某对象的synchronized方法时，就获取了该对象的同步锁。

### 概念锁

无锁->偏向锁->轻量锁->重量锁

悲观锁

可重入锁

## ReentrantLock

ReentrantLock 是一个独占/排他锁。相对于 synchronized，它更加灵活。但是需要自己写出加锁和解锁的过程。它的灵活性在于它拥有很多特性。

### 特性

- 公平性：支持公平锁和非公平锁。默认使用了非公平锁。
- 可重入：
- 可中断：相对于synchronized，它湿可中断的锁，能够对中断作出响应。
- 超时机制：超时后不能获得锁，因此不会造成死锁。

### 注

1. ReentrantLock 需要显示地进行释放锁。特别是在程序异常时，synchronized 会自动释放锁，而 ReentrantLock 并不会自动释放锁，所以必须在 finally 中进行释放锁。
2. ReentrantLock 是很多类的基础，例如 ConcurrentHashMap 内部使用的 Segment 就是继承 ReentrantLock，CopyOnWriteArrayList 也使用了 ReentrantLock。

### 概念锁

公平锁

非公平锁

可重入锁

## NonReentrantLock

### 概念锁

非可重入锁

## ReentrantReadWriteLock

它拥有读锁(ReadLock)和写锁(WriteLock)，读锁是一个共享锁，写锁是一个排他锁。

### 特性

- 公平性：支持公平锁和非公平锁。默认使用了非公平锁。
- 可重入：读线程在获取读锁之后能够再次获取读锁。写线程在获取写锁之后能够再次获取写锁，同时也可以获取读锁（锁降级）。
- 锁降级：先获取写锁，再获取读锁，然后再释放写锁的过程。锁降级是为了保证数据的可见性。

### 概念锁

共享锁

## Object

wait()、notify()、notifyAll()。

### 特别说明

wait() 调用时会释放锁。

## Condition

Condition 用于替代传统的 Object 的 wait()、notify() 实现线程间的协作。

### 说明

在Condition 对象中，与 wait、notify、notifyAll 方法对应的分别是 await、signal 和 signalAll。

### 特性

- 一个 Lock 对象可以创建多个 Condition 实例，所以可以支持多个等待队列。
- Condition 在使用 await、signal 或 signalAll 方法时，必须先获得 Lock 的 lock()
- 支持响应中断
- 支持的定时唤醒功能

### 注

Condition 必须要配合 Lock 一起使用，一个 Condition 的实例必须与一个 Lock 绑定。

## Semaphore

Semaphore、CountDownLatch、CyclicBarrier 都是并发工具类。

Semaphore 可以指定多个线程同时访问某个资源，而 synchronized 和 ReentrantLock 都是一次只允许一个线程访问某个资源。由于 Semaphore 适用于限制访问某些资源的线程数目，因此可以使用它来做**限流**。

### 注

Semaphore 并不会实现数据的同步，数据的同步还是需要使用 synchronized、Lock 等实现。

### 特性

- 基于AQS 的共享模式
- 公平性：支持公平模式和非公平模式。默认使用了非公平模式。

## CountDownLatch

CountDownLatch 可以看成是一个倒计数器，它允许一个或多个线程等待其他线程完成操作。因此，CountDownLatch 是共享锁。

CountDownLatch 的 countDown() 方法将计数器减1，await() 方法会阻塞当前线程直到计数器变为0。

## 总结

![](.\..\png\java锁_小结.png)





