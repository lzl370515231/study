AIDL:(仅仅有当你同意来自不同的client訪问你的服务而且须要处理多线程问题时你才必须使用AIDL)

	AIDL支持的数据类型
　　	1、基本数据类型
　　	2、String和Charsequence
　　	3、List，只支持ArrayList，其中的对象必须序列化
　　	4、Map、只支持HashMap，其中对象必须序列化
　　	5、Parcelable对象
　　	6、AIDL接口

	Messenger是以串行的方式处理客户端发来的信息，如果大量的信息同时发送至服务端，服务端仍然只能逐个处理，所以如果有大量的并发请求，再用Messenger实现就不大合适了。并且使用Messenger无法调用服务端的方法。此时，AIDL是更好的实现IPC的方法。
	
	都在 com.lypeer.aidldemo 包下 ，现在我们需要在 .aidl 文件里使用 Book 对象，那么我们就必须在 .aidl 文件里面写上 import com.lypeer.aidldemo.Book; 
	哪怕 .java 文件和 .aidl 文件就在一个包下。
	
	
	定向tag:
		AIDL中的定向 tag 表示了在跨进程通信中数据的流向，其中 in 表示数据只能由客户端流向服务端， out 表示数据只能由服务端流向客户端，而 inout 则表示数据可在服务端与客户端之间双向流通。
		其中，数据流向是针对在客户端中的那个传入方法的对象而言的。in 为定向 tag 的话表现为服务端将会接收到一个那个对象的完整数据，但是客户端的那个对象不会因为服务端对传参的修改而发生变动；
		out 的话表现为服务端将会接收到那个对象的的空对象，但是在服务端对接收到的空对象有任何修改之后客户端将会同步变动；inout 为定向 tag 的情况下，服务端将会接收到客户端传来对象的完整信息，
		并且客户端将会同步服务端对该对象的任何变动。
		Java 中的基本类型和 String ，CharSequence 的定向 tag 默认且只能是 in 。
	
	
	使数据实现Parcelable 接口：
		由于不同的进程有着不同的内存区域，并且它们只能访问自己的那一块内存区域，所以我们不能像平时那样，传一个句柄过去就完事了——句柄指向的是一个内存区域，
		现在目标进程根本不能访问源进程的内存，那把它传过去又有什么用呢？所以我们必须将要传输的数据转化为能够在内存之间流通的形式。这个转化的过程就叫做序列
		化与反序列化。
	
	使用AIDL的流程
		1. 服务端
			服务端首先创建一个Service用于监听客户端的连接请求，然后创建一个AIDL文件，将暴露给客户端的接口在AIDL文件中声明，最后在Service中实现这个AIDL接口。 
		2. 客户端(只能使用 bindService())
			绑定服务端的Service，绑定成功后，将返回的Binder对象转换程AIDL接口所属的类型。
		
	AIDL难点：
		1. Binder是有可能意外死亡的，所以当服务意外停止时，应重新连接服务。有两种方法：
			1) 给Binder设置DeathRecipient监听，当Binder死亡时会收到binderDied方法回调，在该方法中重新连接服务。
			2) 在onServiceDisconnection中重新连接
			两种方法的区别主要在，一个运行在Binder线程池中，另一个运行在UI线程中。
		
		2. 为AIDL的访问设置权限限制，同样有两种方法：
			1) 在AndroidManifest中声明自定义权限，在onBind方法增加权限判断（checkCallingOrSelfPermission方法）,不通过则返回nulls
			2) 在服务端的onTransact方法中进行权限验证，失败则返回false。
			除了上述使用权限判断的方法之外，还可以采用Uid和Pid来做验证，getCallingUid和getCallingPid可以拿的客户端所属应用的Uid和Pid。并以此进一步获得包名等信息。
		
		3. RemoteCallbackList
			主要作用是可以把多个callback保存到列表里，在合适的时机同时回调，也可以防止重复的调用相同的任务，只保证你需要的一个结果回调。
			擅长简单的持续性的一系列的远程接口的使用，尤其是Service对他的客户端的回调。
			需要注意的是：
				1) 使用的时候，请确保每一个注册的callback唯一性，这样可以在进程停止的时候，清空这些callback。
				2) 多线程请注意锁的问题
			
			使用这个类只要在Service使用单例模式就可以了，使用register和unregister方法来添加客户端的回调，使用时，先beginBroadcast，在getBroadcastItem，最后finishBroadcast。
			// 添加RemoteCallbackList
			private final RemoteCallbackList<IOnCallbackListener> mCallbacks = new RemoteCallbackList<>();

			// 修改后的getCalculateResultByThread
			@Override
			public void getCalculateResultByThread(final TestBean bean, final IOnCallbackListener callback) throws RemoteException {
				new Thread() {
					@Override
					public void run() {
					//注册callback
						mCallbacks.register(callback);
						callback(bean.getX() + bean.getY(), callback);
					}
				}.start();
			}

			/**
			* 处理callback
			*/
			void callback(int result, IOnCallbackListener callback) {
				final int N = mCallbacks.beginBroadcast();
				try {
					for (int i = 0; i < N; i++) {
						IOnCallbackListener ibc = mCallbacks.getBroadcastItem(i);
						ibc.callback(result);
						// 处理结束，解绑callback
						mCallbacks.unregister(ibc);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				mCallbacks.finishBroadcast();
			}
			
			1. 保持跟踪注册的IInterface 回调，通过这些回调的 unique IBindler(通过调用IInterface.asBindler())。
			2. 将每一个注册的接口通过IBindler.DeathRecipient绑定，这样若进程被清楚就可以清除list。
			3. 锁定interfaces列表处理多线程的调用，然后不需要持有lock的方式，用线程安全的方式遍历列表
		
		
	IBinder.DeathRecipient:
		/**
		* Interface for receiving a callback when the process hosting an IBinder has gone away.
		*
		* @see #linkToDeath
		*/
		public interface DeathRecipient {
			public void binderDied();
		}
		注释的意思大概是这是一个接受Binder所在的宿主进程消失时的回调，并且建议我们去查看linkToDeath
		
		public void linkToDeath(DeathRecipient recipient, int flags) throws RemoteException;
		public boolean unlinkToDeath(DeathRecipient recipient, int flags);
		通过一个IBinder.linkToDeath()可以监听这个Binder本身的消失，并调用回调DeathRecipient.binderDied().IBinder.unlinkToDeath()可以取消监听
		
		示例：
			一个作为服务端，一个作为客户端。客户端通过IBinder.DeathRecipient来监听服务端的异常终止情况:
			
			/////客户端/////
			final DeathRecipient deathHandle = new DeathRecipient() {
				@Override
				public void binderDied() {
					// TODO Auto-generated method stub
					Log.i(TAG, "binder is died");
				}
			};
			
			mCon = new ServiceConnection() {
				@Override
				public void onServiceDisconnected(ComponentName name) {
					// TODO Auto-generated method stub
					Log.i(TAG, "onServiceDisconnected "+name.toShortString());
				}

				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					// TODO Auto-generated method stub
					try {
						Log.i(TAG, "onServiceConnected "+name.toShortString()+"  "+service.getInterfaceDescriptor());
						//添加监听
						service.linkToDeath(deathHandle, 0);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			
			bindService(intent,mCon,Context.BIND_AUTO_CREATE);
	
	
	AIDL权限配置：
		1. AndroidManifest方式:
			基本配置：
				Service：
					<!-- 声明权限，一般来说，要声明权限的级别，否则在aidl调用时会出现一个bug：先安装client端，后安装service端会导致client端无法打开 -->
					<permission android:name="com.aa"
						android:protectionLevel="signature" />
					<!-- 声明使用指定的权限 -->
					<uses-permission android:name="com.aa" />
			
				Service配置：
					<service
						android:name="com.pax.pay.service.PaymentService"
						android:permission="com.aa">
						<intent-filter>
							<action android:name="com.pax.pay.service.PaymentService" />
						</intent-filter>
					</service>
				
				client端清单文件要使用相关权限：
					<uses-permission android:name="com.aa" />
			
			实现方法：
				1)
				public IBinder onBind(Intent t) {
					Log("service on bind");
					//第一种远程调用的验证方式，验证客户端的清单文件是否包含相关权限声明
					int check = checkCallingPermission("com.txy.umpay.aidl.permission.OPERATION_HARDWARE");
					if (check == PackageManager.PERMISSION_DENIED) {
						Log("客户端没有权限调用");
						return null;
					}
					return mBinder;
				}
				
				2)
				第二种方法就是在ITaskBinder.Stub mBinder = new ITaskBinder.Stub() 的onTransact(int code, Parcel data, Parcel reply, int flags)方法中，我感觉这个方法就相当于一个拦截器，只要客户端调用ITaskBinder的方法就会先触发onTransact这个方法，这个方法返回boolean值 返回false则其他方法就调用不成功了。
				//在这做权限验证,类似拦截器
				public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
						throws RemoteException {
					Log("这是拦截器！code:" + code + "---flags:" + flags);
					String packageName = null;
					String[] packages = getPackageManager().
							getPackagesForUid(getCallingUid());
					if (packages != null && packages.length > 0) {
						packageName = packages[0];
					}
					if (packageName == null) {
						return false;
					}
					boolean checkPermission = checkPermission(MAIDLService.this, "com.txy.umpay.aidl.permission.OPERATION_HARDWARE", packageName);
					if (!checkPermission) {
						return false;
					}
					return super.onTransact(code, data, reply, flags);
				}
				
				private boolean checkPermission(Context context, String permName, String pkgName) {
					PackageManager pm = context.getPackageManager();
					if (PackageManager.PERMISSION_GRANTED == pm.checkPermission(permName, pkgName)) {
						return true;
					} else {
						return false;
					}
				}

				
	
	Android 中aidl调用执行线程和同步异步问题
		1. bind服务回调执行所在线程
			客户端调用bindService(intent, mConn, Context.BIND_AUTO_CREATE);ServiceConnection回调中获取服务器端的接口（实现了Binder的类）onServiceConnected回调实在主线程中执行
		2. 调用服务端binder类（只实现binder和对应接口的stub类）
			1) 调用没有用 oneway修饰的方法，客户端调用会直接阻塞，服务端方法在binder线程池中执行
			2) 调用使用oneway调用的方法（不能有返回值），客户端不会阻塞，服务端方法在binder线程池中执行
		3. 有回调的binder方法（实现一个对应的服务器端binder回调）
			1) 回调方法直接在服务器端方法中调用：客户端阻塞，阻塞时间为两个之和，客服端回调在调用方法对应线程（如在主线程也是一样，但是show toast 出不来），服务端线程任然在binder线程中执行
			2) 回调方法在服务器端方法中开线程调用。客户端阻塞，阻塞时间为服务器端时间，客服端回调在客服端binder线程中执行，服务端线程任然在binder线程中执行
		4. 客户端回调中调用服务器端方法
			在3基础上，再在回调中调用服务器端方法是在子线程中执行不是在binder线程
		总结：调用binder中非oneway方法是阻塞的并且方法是在子线程中执行，调用oneway方法不阻塞，仍然在子线程中执行
		

		
	示例：(双向通信)
	
		//这个就是aidl文件
		interface RestaurantAidlInterface {

			//新来了一个顾客
			void join(IBinder token,String name);
			//走了一个顾客
			void leave();
			//注册回调接口
			void registerCallBack(NotifyCallBack cb);
			void unregisterCallBack(NotifyCallBack cb);
		}
		
		//Callback
		interface NotifyCallBack {
			void notifyMainUiThread(String name,boolean joinOrLeave);
		}
		
		
		public class RestaurantService extends Service {

			//这个list 就是用来存储当前餐厅有多少顾客 注意我们为什么没有用顾客的名字来存储？
			//而是用了这个CustomerClient的类 看这个类的注释即可明白
			private List<CustomerClient> mClientsList = new ArrayList<>();

			//上面用CustomerClient 的原因是因为害怕客户端异常销毁时，服务器收不到消息 造成资源浪费等异常
			//同样的 我们在服务端通知客户端消息的时候 也害怕 服务端 会异常销毁 导致客户端收不到消息
			//好在谷歌早就为我们考虑到这种情况  提供了RemoteCallbackList 来完成对应的功能
			//避免我们再重复一遍上述的过程
			private RemoteCallbackList<NotifyCallBack> mCallBacks = new RemoteCallbackList<>();


			private final RestaurantAidlInterface.Stub mBinder = new RestaurantAidlInterface.Stub() {


				@Override
				public void join(IBinder token, String name) throws RemoteException {
					CustomerClient cl = new CustomerClient(token, name);
					mClientsList.add(cl);
					notifyCallBack(name, true);
				}

				@Override
				public void leave() throws RemoteException {
					//顾客离开的时候 我们随机让他离开一个就行了
					int length = mClientsList.size();
					int randomIndex = new Random().nextInt(length-1);
					mClientsList.remove(randomIndex);
					notifyCallBack(mClientsList.get(randomIndex).mCustomerName, false);
				}

				@Override
				public void registerCallBack(NotifyCallBack cb) throws RemoteException {
					mCallBacks.register(cb);
				}

				@Override
				public void unregisterCallBack(NotifyCallBack cb) throws RemoteException {
					mCallBacks.unregister(cb);
				}
			};

			private void notifyCallBack(String customerName, boolean joinOrLeave) {
				final int len = mCallBacks.beginBroadcast();
				for (int i = 0; i < len; i++) {
					try {
						// 通知回调
						mCallBacks.getBroadcastItem(i).notifyMainUiThread(customerName, joinOrLeave);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				mCallBacks.finishBroadcast();
			}


			@Override
			public void onDestroy() {
				//销毁回调资源 否则要内存泄露
				mCallBacks.kill();
				super.onDestroy();
			}

			@Nullable
			@Override
			public IBinder onBind(Intent intent) {
				return mBinder;
			}

			//http://developer.android.com/intl/zh-cn/reference/android/os/Binder.html#linkToDeath(android.os.IBinder.DeathRecipient, int)
			//实际上 这个接口 就是用来 当客户端自己发生崩溃时， 我们的服务端也能收到这个崩溃的消息
			//并且会调用binderDied 这个回调方法，所以你看这个内部类的代码 就明白了 无非就是保证当客户端异常销毁的时候
			//我们服务端也要保证收到这个消息 然后做出相应的应对
			final class CustomerClient implements DeathRecipient {

				public final IBinder mToken;

				public CustomerClient(IBinder mToken, String mCustomerName) {
					this.mToken = mToken;
					this.mCustomerName = mCustomerName;
				}

				public final String mCustomerName;

				@Override
				public void binderDied() {
					//我们的应对方法就是当客户端 也就是顾客异常消失的时候 我们要把这个list里面 的对象也移出掉
					if (mClientsList.indexOf(this) >= 0) {
						mClientsList.remove(this);
					}

				}
			}
		}
		
		
		public class MainActivity extends AppCompatActivity implements View.OnClickListener {

			private Button bt, bt2, bt3, bt4;

			private RestaurantAidlInterface mService;

			private TextView tv;


			private ServiceConnection mServiceConnection = new ServiceConnection() {
				@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					mService = RestaurantAidlInterface.Stub.asInterface(service);
					try {
						//我们这个demo里面 只注册了一个回调 实际上可以注册很多个回调 因为service里面 我们存的是list callback
						mService.registerCallBack(mNotifyCallBack);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onServiceDisconnected(ComponentName name) {
					try {
						mService.unregisterCallBack(mNotifyCallBack);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					mService = null;
				}
			};


			private NotifyCallBack mNotifyCallBack = new NotifyCallBack.Stub() {

				@Override
				public void notifyMainUiThread(String name, boolean joinOrLeave) throws RemoteException {
					String toastStr = "";
					if (joinOrLeave) {
						toastStr = name + "进入了餐厅";
					} else {
						toastStr = name + "离开了餐厅";
					}
					tv.setText(toastStr);
				}
			};


			@Override
			protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				bt = (Button) this.findViewById(R.id.bt);
				bt2 = (Button) this.findViewById(R.id.bt2);
				bt3 = (Button) this.findViewById(R.id.bt3);
				bt4 = (Button) this.findViewById(R.id.bt4);
				tv = (TextView) this.findViewById(R.id.tv);
				bt.setOnClickListener(this);
				bt2.setOnClickListener(this);
				bt3.setOnClickListener(this);
				bt4.setOnClickListener(this);

				Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
				setSupportActionBar(toolbar);

				FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
				fab.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
								.setAction("Action", null).show();
					}
				});
			}

			@Override
			public boolean onCreateOptionsMenu(Menu menu) {
				// Inflate the menu; this adds items to the action bar if it is present.
				getMenuInflater().inflate(R.menu.menu_main, menu);
				return true;
			}

			@Override
			public boolean onOptionsItemSelected(MenuItem item) {
				// Handle action bar item clicks here. The action bar will
				// automatically handle clicks on the Home/Up button, so long
				// as you specify a parent activity in AndroidManifest.xml.
				int id = item.getItemId();

				//noinspection SimplifiableIfStatement
				if (id == R.id.action_settings) {
					return true;
				}

				return super.onOptionsItemSelected(item);
			}

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
					case R.id.bt:
						bindService();
						break;
					case R.id.bt2:
						unbindService();
						break;
					case R.id.bt3:
						addCustomer();
						break;
					case R.id.bt4:
						leaveCustomer();
						break;
				}

			}

			private void bindService() {
				Intent intent = new Intent(RestaurantAidlInterface.class.getName());
				bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
			}

			private void unbindService() {
				unbindService(mServiceConnection);
			}

			private void leaveCustomer() {
				try {
					// mService.registerCallBack(mNotifyCallBack);
					mService.leave();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			private void addCustomer() {
				try {
					mService.join(new Binder(), getRandomString(6));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			public static String getRandomString(int length) { //length表示生成字符串的长度
				String base = "abcdefghijklmnopqrstuvwxyz0123456789";
				Random random = new Random();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < length; i++) {
					int number = random.nextInt(base.length());
					sb.append(base.charAt(number));
				}
				return sb.toString();
			}
		}
		
		
		
			
				