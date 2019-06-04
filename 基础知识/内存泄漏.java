避免内存泄露
	
	Java内存回收方式：
		Java判断对象是否可以回收使用的而是可达性分析算法。这个算法的基本思路就是通过一系列名为"GC Roots"的对象作为起始点，从这些节点开始向下搜索，搜索所走过的路径称为引用链(Reference Chain)，当一个对象到GC Roots没有任何引用链相连时，则证明此对象是不可用的。
		
		在Java语言里，可作为GC Roots对象的包括如下几种：
			a.虚拟机栈(栈桢中的本地变量表)中的引用的对象
			b.方法区中的类静态属性引用的对象
			c.方法区中的常量引用的对象
			d.本地方法栈中JNI的引用的对象 
	
	
	Java内存管理和四种引用类型：
		1. Java内存管理
			Java 内存管理就是对象的分配和释放问题。在 Java 中，内存的分配是由「程序」完成的，而内存的释放是由 Java 垃圾回收器（GC）完成的。为了能够正确释放对象，GC 必须监控每一个对象的运行状态，包括对象的申请、引用、被引用、赋值等，监控对象状态是为了更加准确地、及时地释放对象，而释放对象的根本原则就是该对象不再被引用。
			
			1) Java内存分配策略
				Java 程序运行时的内存分配策略有三种，分别是静态分配、栈式分配和堆式分配，三种方式所使用的内存空间分别是静态存储区（方法区）、栈区和堆区。
				1. 静态存储区（方法区）：主要存放静态变量。这块「内存」在程序编译时就已经分配好了，并且在程序整个运行期间都存在。
				2. 栈区：当方法被执行时，方法体内的局部变量（包括基础数据类型、对象的引用）都在栈上创建，并在方法执行结束时。这些局部变量所持有的内存将会自动被释放。因为栈内存分配运算内置于处理器的指令集中，效率很高，但是分配的内存容量有限。
				3. 堆区：又称动态内存分配，通常就是指程序运行时直接 new 出来的内存，也就是对象的实例，这部分「内存」在不使用时将会被 Java 垃圾回收器来负责回收。
			
				结论：
					1. 局部变量的基本数据类型和引用存储于栈中，引用的对象实体存储在堆中 —— 因为他们属于方法中的变量，生命周期随方法而结束。
					2. 成员变量全部存储于堆中（包括基本数据类型，引用和引用的对象实体）—— 因为它们属于类，类对象终究是要被 new 出来使用的。
			
			2) Java垃圾回收器
				在 Java堆和静态存储区（方法区）中，一个接口中的多个实现类需要的内存可能不一样，一个方法中的多个分支需要的内存也可能不一样，我们只有在程序处于运行期间时才能知道会创建哪些对象，这部分内存的分配和回收都是动态的，垃圾回收器所关注的便是这部分的内存。
				
				判断对象是否存活的办法：
					1. 引用计数算法
						给对象添加一个引用计数器，每当有一个地方引用它时，计数器就加 1，当引用失效时，就减 1。任何时刻计数器为 0 的对象就是不可能再被使用的。
						
						实现很简单，判断效率也比较高。但是，主流的 Java 虚拟机里面没有选用引用计数算法来管理内存，其中最主要的原因是它很难解决对象之间相互循环引用的问题。
					
					2. 可达性分析算法
					
				垃圾收集算法：
					1. 标记 — 清除算法
						标记出所有需要回收的对象。
						在标记完成后统一回收所有被标记的对象。
						
						之所以说它是最基础的收集算法，是因为后续的收集算法都是基于这种思路并对其不足进行改进而得到的。它的主要不足主要有两个：
							效率问题，标记和清除两个过程的效率都不高。
							空间问题，标记清除之后会产生大量不连续的内存碎片。
						
						内存碎片太多，可能会导致以后在程序运行过程中需要分配较大对象时，无法找到足够的连续内存而不得不提前触发另一次垃圾收集动作。
					
					2. 复制算法
						为了解决效率问题，一种称为「复制」的收集算法出现了，它将可用内存按容量划分为大小相等的两块，每次只使用其中的一块。当这一块的内存用完了，就将还存活着的对象复制到另外一块上面，然后再把已使用过的内存空间一次清理掉。
						
						只是这种算法的代价是将内存缩小为原来的一半。
					
					3. 标记 — 整理算法
						复制算法在对象存活率较高时就要进行较多的复制操作，效率将会变低。更关键的是，如果不想浪费 50 % 的空间，就需要有额外的空间进行担保，以应对被使用的内存中所有对象都 100% 存活的极端情况，所以在老年代一般不能直接选用这种算法。

						根据老年代的特点，提出了另一种「标记 — 整理」算法，标记过程仍然与「标记 — 清理」算法一样，但后续步骤不是直接对可回收对象进行清理，而是让所有存活的对象都向一端移动，然后直接清理掉边界以外的内存。
						
					4. 分代收集算法
						当前商业虚拟机的垃圾收集都采用「分代收集」算法，这种算法并没有什么新的思想，只是根据对象存活周期的不同将内存划分为几块，一般是把 Java 堆分为新生代和老年代，这样就可以根据各个年代的特点采用最适当的收集算法。

						在新生代中，每次垃圾收集都发现有大批对象死去，只有少量存活，那就选用复制算法，只需要付出少量存活对象的复制成本就可以完成收集，而老年代中因为对象存活率高、没有额外空间对它进行担保，就必须采用「标记 — 清理」或者「标记 — 整理」算法来回收。
						
		
		2. 四种引用类型
			如果 reference 类型的数据中存储的数值代表的是另外一块内存的起始地址，就称这块内存代表着一个引用。一个对象在这种定义下只有被引用或没有被引用两种状态。当内存空间还足够时，则能保留在内存之中，如果内存空间在进行垃圾回收后还是非常紧张，则可以抛弃这些对象，很多系统的缓存功能都符合这样的应用场景。
			
			在 JDK 1.2 之后，Java 对引用的概念进行了扩充，将引用分为强引用（Strong Reference）、软引用（Soft Reference）、弱引用（Weak Reference）、虚引用（Phantom Reference）4 中，这四种引用强度一次逐渐减弱。
			
				1) 强引用：指在程序代码之中普遍存在的，类似 Object obj = new Object() 这类的引用，只要强引用还存在，垃圾回收器「永远」不会回收掉被引用的对象
				
				2) 软引用：用来描述一些还有用但并非必需的对象。对于软引用关联着的对象，在系统将要发生内存溢出异常之前，将会把这些对象列进回收范围之中进行第二次回收。如果这次回收还没有足够的内存，才会抛出内存溢出异常

				3) 弱引用：用来描述非必须对象的，但是它的强度比软引用更弱一些，被弱引用关联的对象只能生存到下一次垃圾收集之前。当垃圾收集器工作时，无论当前内存是否足够，都会回收掉只被弱引用关联的对象

				4) 虚引用：也被称为幽灵引用或幻影引用，它是最弱的一种引用关系。一个对象是否有虚引用的存在，完全不会对其生存时间构成影响，也无法通过虚引用来取得一个对象实例。为一个对象设置虚引用关联的 唯一目的就是能在这个对象被收集器回收时收到一个系统通知。
				
	

	内存泄漏概念：
		一个不会被使用的对象,因为另一个正在使用的对象持有该对象的引用（生命周期较长的对象持有生命周期较短的对象的引用）,导致它不能正常被回收,而停留在堆内存中,内存泄漏就产生了Android系统为每个应用分配的内存是有限的,内存泄漏会使我们的应用内存随着时间不断的增加,造成应用OOM(Out Of Memory)错误,使应用崩溃。
	
	Java内存泄漏的原因：
		对象可达但不可用。
	
	Android内存泄漏的原因：
		生命周期较长的对象持有生命周期较短的对象的引用。
	
	Android应用被分配的堆的大小限制为16MB。与手机相关。
	
	
	常见Android内存泄漏场景：
		1. 持有Context造成的内存泄漏
			作为工作的一部分，在Android应用中遇到过得内存泄露问题，它们大多数时候是因为同一个错误:
				对Context维持了一个长时间的引用
			在Android中，Context可用于很多操作，但主要是用于加载和访问资源。这就是为什么所有的widget都要在它们的构造方法中接收Context参数。在一个常规的Android应用中，通常有两种Context:
				Activity和Application

			一般开发者将前者传递给需要Context的类和方法:
				@Override
				protected void onCreate(Bundle state) {
					super.onCreate(state);
				  
					TextView label = new TextView(this);
					label.setText("Leaks are bad");
				  
					setContentView(label);
				}
			这意味着View有一整个Activity的引用。因此可访问到Activity持有的任何东西。因此，如果你泄露了Context（泄露(leak)意味着你维持了一个指向它的引用导致GC不能回收它），你就泄露了很多内存。如果你不小心的话，非常容易就会泄露整个Activity。
			
			当屏幕的方向改变时，系统默认会销毁当前的Activity并保存它的状态，然后创建一个新的Activity。在这个过程中，Android会从资源中重新加载应用的UI。现在假设你写了一个有大bitmap的应用，你不想每次旋转时都加载bitmap。维持这个实例、不需要每次重新加载的最简单的办法就是将它维持在一个静态域中。
			
				private static Drawable sBackground;

				@Override
				protected void onCreate(Bundle state) {
					super.onCreate(state);
			  
					TextView label = new TextView(this);
					label.setText("Leaks are bad");
					if (sBackground == null) {
						sBackground = getDrawable(R.drawable.large_bitmap);
					}
					label.setBackgroundDrawable(sBackground);
					setContentView(label);
				}
			这个代码是非常便捷但是也非常错误。它泄露了第一次因屏幕方向改变而创建的Activity。当一个Drawable对象依附到一个View对象上时，View对象被作为一个callback设置到drawable对象上了。在上面的代码片段中，这就意味着drawable有一个TextView的引用，而TextView有activity的引用（Context），activity有很多东西的原因（取决于你的代码）。
			
			这个例子是Context泄露的最简单的例子，你可以看看我们在Home screen的源码中是如何处理这种问题的（看unbindDrawables()方法),通过在activity销毁时，将存储的drawable的callback为null。有趣的是，有多种情况能导致我们创建泄露的Context，这样很糟糕。它们使得我们很快就耗尽内存。
			
			有两种简单的方法可避免Context相关的内存泄露:
				1. 最明显的方法是避免在Context的作用域之外使用它，对context的引用不要超过他本身的生命周期。上面那个例子展示了静态引用或是内部类对外部类的隐式引用都是同样危险的。
				2. 第二种方法就是使用Application Context。这个Context会一直存活只要你的应用是活着的，并且不依赖于Activity的生命周期。如果你打算维持一个长时间存在的并且需要Context的对象时，记住使用应用的Context。获取方法：
					Context.getApplicationContext()或Activity.getApplication()。
				
			为了避免Context相关的内存泄漏，记住下面几点：
				1. 不要维持一个长时间存在对Activity的Context的引用(Activity的引用和Activity有着一样的生命周期)
				2. 使用Application的Context而不是Activity的Context
				3. 避免在Activity中使用非静态内部类，如果你不想控制他们的生命周期。使用静态内部类，并在它的内部创建一个对Activity的弱引用。
			
	
		2. Handler造成的内存泄漏：
			1. 可以把Handler放到单独的类中,或者使用静态的内部类(静态内部类不会引用activity)避免泄漏
			2. 如果想要在handler内部去调用Activity中的资源,可以在Handler中使用弱引用的方式指向所在的Activity,使用static+WeakReference的方式断开handler与activity的关系
	
			使用非静态内部类，Android Studio报可能内存泄露的警告：
				//解决办法：使用静态内部类，并在其中创建对Activity的弱引用
				private static class MyHandler extends Handler{

					//对Activity的弱引用
					private final WeakReference<HandlerActivity> mActivity;

					public MyHandler(HandlerActivity activity){
						mActivity = new WeakReference<HandlerActivity>(activity);
					}

					@Override
					public void handleMessage(Message msg) {
						HandlerActivity activity = mActivity.get();
						if(activity==null){
							super.handleMessage(msg);
							return;
						}
						switch (msg.what) {
							case DOWNLOAD_FAILED:
								Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
								break;
							case DOWNLOAD_SUCCESS:
								Toast.makeText(activity, "下载成功", Toast.LENGTH_SHORT).show();
								Bitmap bitmap = (Bitmap) msg.obj;
								activity.imageView.setVisibility(View.VISIBLE);
								activity.imageView.setImageBitmap(bitmap);
								break;
							default:
								super.handleMessage(msg);
								break;
						}
					}
				}

				private final MyHandler mHandler = new MyHandler(this);
	
		3. 使用单利模式造成的内存泄漏
			使用单利模式的时候如果使用不当也会造成内存泄漏.因为单利模式的静态特征使得单利模式的生命周期和应用一样的长,这说明了当一个对象不需要使用了,而单利对象还存在该对象的引用,那么这个对象就不能正常的被回收,就造成了内存泄漏。
				XXUtils.getInstance(this);
			这句代码默认传入的是Activity的Context,而Activity是间接继承自Context的,当Activity退出之后,单利对象还持有他的引用,所以在为了避免传Activity的Context,在单利中通过传入的context获取到全局的上下文对象,而不使用Activity的Context就解决了这个问题。
				public class XXUtils {
					private Context mContext;
					private XXUtils(Context context) {
						mContext = context.getApplicationContext();
					}
					private static XXUtils instance;
					public static XXUtils getInstance(Context context) {
						if (instance == null) {
							synchronized (XXUtils.class) {
								if (instance == null) {
									instance = new XXUtils(context);
								}
							}
						}
						return instance;
					}
				}
			
		4. 非静态内部类创建静态实例造成的内存泄漏
			为了避免多次的初始化资源,常常会使用静态对象去保存这些对象,这种情况也很容易引发内存泄漏。
			原因：
				1) 非静态的内部类默认会持有外部类的引用
				2) 而我们又使用非静态内部类创建了一个静态的实例
				3) 该静态实例的声明周期和应用一样长,这就导致了该静态实例一直会持有该Activity的引用,导致Activity不能正常回收
				
			解决方案：
				1) 将内部类修改成静态的,这样它对外部类就没有引用
				2) 将该对象抽取出来封装成一个单利
			
			如下：
				private static TestResource mTestResource;
				protected void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					setContentView(R.layout.activity_main);
					findViewById(R.id.btn).setOnClickListener(this);
				}
				private void initData() {
					if (mTestResource == null) {
						mTestResource = new TestResource();
					}
				}
				public void onClick(View v) {
					initData();
				}
				//非静态内部类默认会持有外部类的引用
				//修改成就太之后正常被回收,是因为静态的内部类不会对Activity有引用
				private static class TestResource {
				}
		
		5. 外部类中持有非静态内部类的静态对象
			public class MainActivity extends AppCompatActivity {
				
				private static Test test;
				
				@Override
				protected void onCreate(Bundle savedInstanceState) {
					super.onCreate(savedInstanceState);
					setContentView(R.layout.activity_main);
					if (test == null) {
						test = new Test();
					}

				}
				
				private class Test {

				}
				
			}
		
		
		6. 线程造成的内存泄漏
			当我们在使用线程的时候,一般都使用匿名内部类,而匿名内部类会对外部类持有默认的引用,当Acticity关闭之后如果现成中的任务还没有执行完毕,就会导致Activity不能正常回收,造成内存泄漏。
			
			解决方案：
				创建一个静态的类,实现Runnable方法,在使用的时候实例化他。
				
			如下：	
				private void loadData() {
					new Thread(new MyThread()).start();
				}
				private static class MyThread implements Runnable {
					public void run() {
						SystemClock.sleep(20000);
					}
				}
		
		7. Timer和TimerTask导致内存泄露
			当我们Activity销毁的时，有可能Timer还在继续等待执行TimerTask，它持有Activity的引用不能被回收。
			
			解决方案：
				当我们Activity销毁的时候要立即cancel掉Timer和TimerTask，以避免发生内存泄漏。
			
		
		8. 资源未关闭造成的内存泄漏
			对于使用了BraodcastReceiver，ContentObserver，File，Cursor，Stream，Bitmap等资源的代码，应该在Activity销毁时及时关闭或者注销，否则这些资源将不会被回收，造成内存泄漏。
		
		9. 监听器没有注销造成的内存泄漏
			在Android程序里面存在很多需要register与unregister的监听器，我们需要确保及时unregister监听器。
		
		10. 集合中的内存泄漏
			我们通常把一些对象的引用加入到了集合容器（比如ArrayList）中，当我们不需要该对象时，并没有把它的引用从集合中清理掉，这样这个集合就会越来越大。如果这个集合是static的话，那情况就更严重了。所以要在退出程序之前，将集合里的东西clear，然后置为null，再退出程序。

		11. 属性动画造成内存泄露
			动画同样是一个耗时任务，比如在Activity中启动了属性动画（ObjectAnimator），但是在销毁的时候，没有调用cancle方法，虽然我们看不到动画了，但是这个动画依然会不断地播放下去，动画引用所在的控件，所在的控件引用Activity，这就造成Activity无法正常释放。
			
			解决方案：
				要在Activity销毁的时候cancel掉属性动画，避免发生内存泄漏。
			
		12. WebView造成内存泄露
			关于WebView的内存泄露，因为WebView在加载网页后会长期占用内存而不能被释放，因此我们在Activity销毁后要调用它的destory()方法来销毁它以释放内存。
			
			Webview下面的Callback持有Activity引用，造成Webview内存无法释放，即使是调用了Webview.destory()等方法都无法解决问题（Android5.1之后）
			
			解决方案：
				在销毁WebView之前需要先将WebView从父容器中移除，然后在销毁WebView。
				
			如下：
				@Override
				protected void onDestroy() {
					super.onDestroy();
					// 先从父控件中移除WebView
					mWebViewContainer.removeView(mWebView);
					mWebView.stopLoading();
					mWebView.getSettings().setJavaScriptEnabled(false);
					mWebView.clearHistory();
					mWebView.removeAllViews();
					mWebView.destroy();
				}

	
	检测工具：
		1. MAT
		
		2. LeakCanary
			会检测应用的内存回收情况，如果发现有垃圾对象没有被回收，就会去分析当前的内存快照，也就是上边MAT用到的.hprof文件，找到对象的引用链，并显示在页面上。这款插件的好处就是,可以在手机端直接查看内存泄露的地方,可以辅助我们检测内存泄露。LeakCanary是一个傻瓜化并且可视化的内存泄露分析工具。
		
			集成：
				1. 尽量在app下的build.gradle中加入以下依赖
					dependencies {
						debugCompile 'com.squareup.leakcanary:leakcanary-android:1.3.1' // or 1.4-beta1
						releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1' // or 1.4-beta1
						testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1' // or 1.4-beta1
					}
					
				2. 在Application中加入类似如下的代码
					public class ExampleApplication extends Application {
						@Override 
						public void onCreate() {
							super.onCreate();
							LeakCanary.install(this);
						}
					}
				
				到这里你就可以检测到Activity的内容泄露了。其实现原理是设置Application的ActivityLifecycleCallbacks方法监控所有Activity的生命周期回调。
				
				想要检测更多:
				首先我们需要获得一个RefWatcher，用来后续监控可能发生泄漏的对象。
					public class MyApplication extends Application {
						private static RefWatcher sRefWatcher;
						@Override
						public void onCreate() {
							super.onCreate();
							sRefWatcher = LeakCanary.install(this);
						}
						public static RefWatcher getRefWatcher() {
							return sRefWatcher;
						}
					}
				
				监控某个可能存在内存泄露的对象
					MyApplication.getRefWatcher().watch(sLeaky);
				
				默认情况下，是对Activity进行了检测。另一个需要监控的重要对象就是Fragment实例。因为它和Activity实例一样可能持有大量的视图以及视图需要的资源（比如Bitmap）即在Fragment onDestroy方法中加入如下实现
					public class MainFragment extends Fragment {
						@Override
						public void onDestroy() {
							super.onDestroy();
							MyApplication.getRefWatcher().watch(this);
						}
					}
				
				加入例外：
					比如ExampleClass.exampleField会导致内存泄漏，我们想要忽略，如下操作即可。
					// ExampleApplication is defined in "Customizing and using the no-op dependency"
					public class DebugExampleApplication extends ExampleApplication {
						protected RefWatcher installLeakCanary() {
							ExcludedRefs excludedRefs = AndroidExcludedRefs.createAppDefaults()
								.instanceField("com.example.ExampleClass", "exampleField")
								.build();
							return LeakCanary.install(this, DisplayLeakService.class, excludedRefs);
						}
					}
				
				如何不影响对外版APK：
					debugCompile 'com.squareup.leakcanary:leakcanary-android:1.3.1' // or 1.4-beta1
					releaseCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1' // or 1.4-beta1
					testCompile 'com.squareup.leakcanary:leakcanary-android-no-op:1.3.1' // or 1.4-beta1
				
				其中releaseCompile和testCompile这两个的依赖明显不同于debugCompile的依赖。它们的依赖属于NOOP操作。NOOP，即No Operation Performed，无操作指令。常用的编译器技术会检测无操作指令并出于优化的目的将无操作指令剔除。
				
				注：
					目前LeakCanary一次只能报一个泄漏问题，如果存在内存泄漏但不是你的模块，并不能说明这个模块没有问题。建议建议将非本模块的泄漏解决之后，再进行检测。


					



		