Service:
	适用场景：
		1. 并不依赖于用户可视的UI界面（当然，这一条其实也不是绝对的，如前台Service就是与Notification界面结合使用的）
		2. 具有较长时间的运行特性。
	
	Service生命周期：
		见图
	
	Service分类：
		
		Scheduled Service:
			可定时执行的Service，是Android 5.0（API LEVEL 21）版本中新添加的一个Service，名为JobService，继承Service类，使用JobScheduler类调度它并且设置JobService运行的一些配置。具体文档可以参考JobScheduler，如果你的应用最低支持版本是21，官方建议使用JobService。
		
		Start Service：
			启动 context.startService(intent);
			停止 context.stopService(intent);	或在service 自身调用 stopSelf();
			
			第一次启动，首先会执行 onCreate()回调，然后再执行onStartCommand(Intent intent, int flags, int startId)，当Client再次调用startService(Intent serviceIntent)，将只执行onStartCommand(Intent intent, int flags, int startId)，因为此时Service已经创建了，无需执行onCreate()回调。无论多少次的startService，只需要一次stopService()即可将此Service终止，执行onDestroy()函数（其实很好理解，因为onDestroy()与onCreate()回调是相对的）。当用户强制kill掉进程时，onDestroy()是不会执行的。
			
			说明：
				1) 对于同一类型的Service，Service实例一次永远只存在一个，而不管Client是否是相同的组件，也不管Client是否处于相同的进程中。
				2) Client调用stopService(..)时，如果当前Service没有启动，也不会出现任何报错或问题，也就是说，stopService(..)无需做当前Service是否有效的判断。
			
			onStartCommand(Intent intent, int flags, int startId)方法。
				参数说明：
					intent：启动组件传递过来的 Intent。
					flags：表示启动请求时是否有额外数据。可选值有：
						0						代表没有
						START_FLAG_REDELIVERY 	onStartCommand方法的返回值为START_REDELIVER_INTENT，而且在上一次服务被杀死前会去调用stopSelf方法停止服务。其中START_REDELIVER_INTENT意味着当Service因内存不足而被系统kill后，则会重建服务，并通过传递给服务的最后一个 Intent 调用 onStartCommand()，此时Intent时有值的。
						START_FLAG_RETRY 		该flag代表当onStartCommand调用后一直没有返回值时，会尝试重新去调用onStartCommand()。
						
					startId：指明当前服务的唯一ID，与stopSelfResult(int startId)配合使用，stopSelfResult()可以更安全地根据ID停止服务。
				其中参数flags默认情况下是0，对应的常量名为START_STICKY_COMPATIBILITY。
				startId是一个唯一的整型，用于表示此次Client执行startService(...)的请求请求标识，在多次startService(...)的情况下，呈现0,1,2....递增。
				另外，此函数具有一个int型的返回值，具体的可选值及含义如下：(返回值作用的条件----当Service因为内存不足而被系统kill后)
					START_NOT_STICKY：
						当Service因为内存不足而被系统kill后，接下来未来的某个时间内，即使系统内存足够可用，系统也不会尝试重新创建此Service。除非程序中Client明确再次调用startService(...)启动此Service。
					START_STICKY：
						当Service因为内存不足而被系统kill后，接下来未来的某个时间内，当系统内存足够可用的情况下，系统将会尝试重新创建此Service，一旦创建成功后将回调onStartCommand(...)方法，但其中的Intent将是null，pendingintent除外。
					START_REDELIVER_INTENT：
						与START_STICKY唯一不同的是，回调onStartCommand(...)方法时，其中的Intent将是非空，将是最后一次调用startService(...)中的intent。
					START_STICKY_COMPATIBILITY：
						START_STICKY的兼容版本，但不保证服务被kill后一定能重启
						
			
		Bound Service：
			Bound Service的主要特性在于Service的生命周期是依附于Client的生命周期的，当Client不存在时，Bound Service将执行onDestroy，同时通过Service中的Binder对象可以较为方便进行Client-Service通信。
			
			使用过程：
				1.自定义Service继承基类Service，并重写onBind(Intent intent)方法，此方法中需要返回具体的Binder对象；
				2.Client通过实现ServiceConnection接口来自定义ServiceConnection，并通过bindService (Intent service, ServiceConnection sc, int flags)方法将Service绑定到此Client上；flags 是指定绑定时是否自动创建Service。0代表不自动创建、BIND_AUTO_CREATE则代表自动创建。flags详解如下：
					1) 0，如果不想设置任何值，就设置成0
					2) Context.BIND_AUTO_CREATE，绑定服务时候，如果服务尚未创建，服务会自动创建，在API LEVEL 14以前的版本不支持这个标志，使用Context.BIND_WAIVE_PRIORITY可以达到同样效果
					3) Context.BIND_DEBUG_UNBIND，通常用于Debug，在unbindService时候，会将服务信息保存并打印出来，这个标记很容易造成内存泄漏。
					4) Context.BIND_NOT_FOREGROUND，不会将被绑定的服务提升到前台优先级，但是这个服务也至少会和客户端在内存中优先级是相同的。
					5) Context.BIND_ABOVE_CLIENT，设置服务的进程优先级高于客户端的优先级，只有当需要服务晚于客户端被销毁这种情况才这样设置。
					6) Context.BIND_ALLOW_OOM_MANAGEMENT，保持服务受默认的服务管理器管理，当内存不足时候，会销毁服务
					7) Context.BIND_WAIVE_PRIORITY，不会影响服务的进程优先级，像通用的应用进程一样将服务放在一个LRU表中
					8) Context.BIND_IMPORTANT，标识服务对客户端是非常重要的，会将服务提升至前台进程优先级，通常情况下，即时客户端是前台优先级，服务最多也只能被提升至可见进程优先级，
					9) BIND_ADJUST_WITH_ACTIVITY，如果客户端是Activity，服务优先级的提高取决于Activity的进程优先级，使用这个标识后，会无视其他标识。
				3.自定义的ServiceConnection中实现onServiceConnected(ComponentName name, IBinder binder)方法，获取Service端Binder实例；
				4.通过获取的Binder实例进行Service端其他公共方法的调用，以完成Client-Service通信；
				5.当Client在恰当的生命周期（如onDestroy等）时，此时需要解绑之前已经绑定的Service，通过调用函数unbindService(ServiceConnection sc)。
			
			
			具体使用过程中，根据 onBind(Intent intent)方法返回的 Binder对象的定义方式不同，又可以将其分为以下三种方式，且每种方式具有不同的特点和适应场景：
				1. Extending the Binder class
					是Bound Service中最常见的一种使用方式，也是Bound Service中最简单的一种。
					局限：Clinet与Service必须同属于同一个进程，不能实现进程间通信（IPC）。
					具体使用：
						
						//Serivce
						private MyBinder mBinder = new MyBinder();

						public class MyBinder extends Binder {
							MyBindService getService() {
								return MyBindService.this;
							}
						}
						
						@Override
						public IBinder onBind(Intent intent) {
							Log.w(TAG, "in onBind");
							return mBinder;
						}
						
						//Client
						private class MyServiceConnection implements ServiceConnection {

							@Override
							public void onServiceConnected(ComponentName name, IBinder binder) {
								Log.w(TAG, "in MyServiceConnection onServiceConnected");
								mBinder = (MyBinder) binder;
								mBindService = mBinder.getService();
							}

							@Override
							public void onServiceDisconnected(ComponentName name) {
								// This is called when the connection with the service has been
								// unexpectedly disconnected -- that is, its process crashed
							}

						}
					
					说明：
						在四大基本组件中，需要注意的的是BroadcastReceiver不能作为Bound Service的Client，因为BroadcastReceiver的生命周期很短，当执行完onReceive(..)回调时，BroadcastReceiver生命周期完结。而Bound Service又与Client本身的生命周期相关，因此，Android中不允许BroadcastReceiver去bindService(..)，当有此类需求时，可以考虑通过startService(..)替代。
				
				2. Using  a Messenger
					过Messenger方式返回Binder对象可以不用考虑Clinet - Service是否属于同一个进程的问题，并且，可以实现Client - Service之间的双向通信。
					局限：不支持严格意义上的多线程并发处理，实际上是以队列去处理
					具体使用：	
						//Service
						private Messenger mClientMessenger;
						private Messenger mServerMessenger = new Messenger(new ServerHandler());
						
						@Override
						public IBinder onBind(Intent intent) {
							Log.w(TAG, "in onBind");
							return mServerMessenger.getBinder();
						}
						
						class ServerHandler extends Handler {
							@Override
							public void handleMessage(Message msg) {
								Log.w(TAG, "thread name:" + Thread.currentThread().getName());
								switch (msg.what) {
								case MSG_FROM_CLIENT_TO_SERVER:
									Log.w(TAG, "receive msg from client");
									mClientMessenger = msg.replyTo;

									// service发送消息给client
									Message toClientMsg = Message.obtain(null, MSG_FROM_SERVER_TO_CLIENT);
									try {
										Log.w(TAG, "server begin send msg to client");
										mClientMessenger.send(toClientMsg);
									} catch (RemoteException e) {
										e.printStackTrace();
									}
									break;
								default:
									super.handleMessage(msg);
								}
							}
						}
						
						//Client
						private ServiceConnection sc = new MyServiceConnection();
						private Messenger mServerMessenger;	//service Messenger
						private Handler mClientHandler = new MyClientHandler();
						private Messenger mClientMessenger = new Messenger(mClientHandler);	//Client Messenger
						
						private class MyClientHandler extends Handler {
							@Override
							public void handleMessage(Message msg) {
								if (msg.what == MyMessengerService.MSG_FROM_SERVER_TO_CLIENT) {
									Log.w(TAG, "reveive msg from server");
								}
							}
						}
						
						private class MyServiceConnection implements ServiceConnection {

							@Override
							public void onServiceConnected(ComponentName name, IBinder binder) {
								Log.w(TAG, "in MyServiceConnection onServiceConnected");
								mServerMessenger = new Messenger(binder);

								mBound = true;
							}

							@Override
							public void onServiceDisconnected(ComponentName name) {
								// This is called when the connection with the service has been
								// unexpectedly disconnected -- that is, its process crashed.
								Log.w(TAG, "in MyServiceConnection onServiceDisconnected");

								mBound = false;
							}
						}
						
						public void sayHello() {
							if (!mBound)
								return;
							// Create and send a message to the service, using a supported 'what' value
							Message msg = Message.obtain(null, MyMessengerService.MSG_FROM_CLIENT_TO_SERVER, 0, 0);
							// 通过replyTo把client端的Messenger(信使)传递给service
							msg.replyTo = mClientMessenger;
							try {
								mServerMessenger.send(msg);
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						
					说明：
						想实现Service发送消息到Client，可以在客户端定义一个Handler，并得到相应的Messenger，在Clinet发送消息给Service时，通过msg.replyTo = mClientMessenger方式将Client信使传递给Service。
				
				
				3. AIDL
					详情见 Aidl 文档
			
			注：
				1. 无论哪种方式的Bound Service，在进行unbind(..)操作时，都需要注意当前Service是否处于已经绑定状态，否则可能会因为当前Service已经解绑后继续执行unbind(..)会导致崩溃。这点与Started Service区别很大（如前文所述：stopService(..)无需做当前Service是否有效的判断）。
				2. 多个客户端可同时连接到一个服务。不过，只有在第一个客户端绑定时，系统才会调用服务的 onBind() 方法来检索 IBinder。系统随后无需再次调用 onBind()，便可将同一 IBinder 传递至任何其他绑定的客户端。当最后一个客户端取消与服务的绑定时，系统会将服务销毁（除非 startService() 也启动了该服务）。
				3. 应用组件（客户端）可通过调用 bindService() 绑定到服务,Android 系统随后调用服务的 onBind() 方法，该方法返回用于与服务交互的 IBinder，而该绑定是异步执行的
				4. 我们应该始终捕获 DeadObjectException DeadObjectException 异常，该异常是在连接中断时引发的，表示调用的对象已死亡，也就是Service对象已销毁，这是远程方法引发的唯一异常，DeadObjectException继承自RemoteException，因此我们也可以捕获RemoteException异常。
	
		启动服务与绑定服务间的装换问题：
			先绑定服务后启动服务：
				如果当前Service实例先以绑定状态运行，然后再以启动状态运行，那么绑定服务将会转为启动服务运行，这时如果之前绑定的宿主（Activity）被销毁了，也不会影响服务的运行，服务还是会一直运行下去，指定收到调用停止服务或者内存不足时才会销毁该服务。
			
			先启动服务后绑定服务：
				如果当前Service实例先以启动状态运行，然后再以绑定状态运行，当前启动服务并不会转为绑定服务，但是还是会与宿主绑定，只是即使宿主解除绑定后，服务依然按启动服务的生命周期在后台运行，直到有Context调用了stopService()或是服务本身调用了stopSelf()方法抑或内存不足时才会销毁服务。
		
		
	Service 特性：
		1. Service本身都是运行在其所在进程的主线程（如果Service与Clinet同属于一个进程，则是运行于UI线程）。
		2. Service一旦创建，需要停止时都需要显示调用相应的方法（Started Service需要调用stopService(..)或Service本身调用stopSelf(..)， Bound Service需要调用unbindService(..)），否则对于Started Service将处于一直运行状态，对于Bound Service，当Client生命周期结束时也将引起问题。也就是说，Service执行完毕后，必须人为的去停止它。
					
	
	AndroidManifest.xml中注册：
		<service android:enabled=["true" | "false"]
			android:exported=["true" | "false"]
			android:icon="drawable resource"
			android:isolatedProcess=["true" | "false"]
			android:label="string resource"
			android:name="string"
			android:permission="string"
			android:process="string" >
			. . .
		</service>
		
			android:isolatedProcess ：(???????)
				设置true意味着，服务会在一个特殊的进程下运行，这个进程与系统其他进程分开且没有自己的权限。与其通信的唯一途径是通过服务的API(bind and start)。

			android:enabled：(?????)
				是否可以被系统实例化，默认为 true因为父标签 也有 enable 属性，所以必须两个都为默认值 true 的情况下服务才会被激活，否则不会激活。
	
	
	IntentService:
		IntentService是系统提供给我们的一个已经继承自Service类的特殊类，IntentService特殊性是相对于Service本身的特性而言的：
			1. 默认直接实现了onBind(..)方法，直接返回null，并定义了抽象方法onHandlerIntent(..)，用户自定义子类时，需要实现此方法。
			2. onHandlerIntent(..)主要就是用来处于相应的”长期“任务的，并且已经自动在新的线程中，用户无语自定义新线程。
			3. 当”长期“任务执行完毕后（也就是onHandlerIntent(..)执行完毕后），此IntentService将自动结束，无需人为调用方法使其结束。
			4. IntentService处于任务时，也是按照队列的方式一个个去处理，而非真正意义上的多线程并发方式。
			
		
		具体使用：
			public class MyIntentService extends IntentService {
				
				private final static String TAG = "MyIntentService";
				//构造方法 一定要实现此方法否则Service运行出错。
				public MyIntentService() {
					super("MyIntentService");
				}

				@Override
				public void onCreate() {
					super.onCreate();
				}

				@Override
				public void onStart(Intent intent, int startId) {
					super.onStart(intent, startId);
				}

				@Override
				protected void onHandleIntent(Intent intent) {
					Log.i(TAG, "onHandleIntent thread:"+Thread.currentThread());
					String action = intent.getAction();
					if(action.equals(ACTION_DOWN_IMG)){
						
					}else if(action.equals(ACTION_DOWN_VID)){
						
					}
					Log.i(TAG, "onHandleIntent end");
				}

				@Override
				public void onDestroy() {
					super.onDestroy();
					
				}
			}
		
	
	处理每个请求都需要开启一个线程，并且同一时刻一个线程只能处理一个请求:
		
	

		
	前台Service:
		前台服务被认为是用户主动意识到的一种服务，因此在内存不足时，系统也不会考虑将其终止。前台服务必须为状态栏提供通知，状态栏位于“正在进行”标题下方，这意味着除非服务停止或从前台删除，否则不能清除通知。
		Service.startForeground(int id,Notification notification)
			可以将此Service设置为前台Service。在UI显示上，notification将是一个处于onGoing状态的通知，使得前台Service拥有更高的进程优先级，并且Service可以直接notification通信。ID不得为0。
		stopForeground(boolean removeNotification) 
			该方法是用来从前台删除服务，此方法传入一个布尔值，指示是否也删除状态栏通知，true为删除。注意该方法并不会停止服务。但是，如果在服务正在前台运行时将其停止，则通知也会被删除。
		具体使用:
			public class MyService extends Service{
				
				@Override
				public IBinder onBind(Intent intent){
					return null;
				}
				
				@Override
				public void onCreate() {
					super.onCreate();
					Log.w(TAG, "in onCreate");
				}
				
				@Override
				public int onStartCommand(Intent intent, int flags, int startId) {
					Log.w(TAG, "in onStartCommand");
					Log.w(TAG, "MyService:" + this);
					String name = intent.getStringExtra("name");
					Log.w(TAG, "name:" + name);

					
					Notification notification = new Notification(R.drawable.ic_launcher, "test", System.currentTimeMillis());
					Intent notificationIntent = new Intent(this, DActivity.class);
					PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntesnt, 0);
					notification.setLatestEventInfo(this, "title", "content", pendingIntent);
					startForeground(1, notification);
					
					return START_REDELIVER_INTENT;
				}
				
				@Override
				public void onDestroy() {
					super.onDestroy();
					stopForeground(true);
					Log.w(TAG, "in onDestroy");
				}
			}

			注：
				startForeground只要传的id相同，不管是不是一个进程，不管是不是同一个notification,都会用最新的notification覆盖旧的，只显示一个。
	
	
	Service 与 Thread 的区别：
		1. 概念的区别：
			Thread是程序执行的最小单元。
			Service是Android的一种机制，服务是运行在主线程上的，由系统进程托管。
			
	如何保证服务不被杀死：
		Android将进程分为6个等级，它们按优先级顺序由高到低分别是：
			1) 前台进程			(FOREGROUND_APP)
			2) 可视进程			(VISIBLE_APP)
			3) 次要服务进程		(SECONDARY_SERVICE)
			4) 后台进程			(HIDDEN_APP)
			5) 内容供应节点		(CONTENT_PROVIDER)
			6) 空进程			(EMPTY_APP)
			
			
		1. 因内存资源不足而杀死Service
			解决方式:
				1) 可将onStartCommand() 方法的返回值设为 START_STICKY或START_REDELIVER_INTENT 
				2) 可将Service设置为前台服务，这样就有比较高的优先级，在内存资源紧张时也不会被杀掉。
				3) 通过更改优先级的方式，在清单文件中，将优先级更改为1000，在清理内存的时候，优先级越高的越不容易被杀死。
		
		2. 用户通过settings->Apps->Running->Stop 方式杀死Service
			这种情况是用户手动干预的，不过幸运的是这个过程会执行Service的生命周期，也就是onDestory方法会被调用。
				1) 可以在 onDestory() 中发送广播重新启动。
				2) 可开启两个服务，相互监听，相互启动。服务A监听B的广播来启动B，服务B监听A的广播来启动A。
			第一种方式实现如下：
				public class ServiceKilledByAppStop extends Service{

					private BroadcastReceiver mReceiver;
					private IntentFilter mIF;

					@Nullable
					@Override
					public IBinder onBind(Intent intent) {
						return null;
					}

					@Override
					public void onCreate() {
						super.onCreate();
						mReceiver = new BroadcastReceiver() {
							@Override
							public void onReceive(Context context, Intent intent) {
								Intent a = new Intent(ServiceKilledByAppStop.this, ServiceKilledByAppStop.class);
								startService(a);
							}
						};
						mIF = new IntentFilter();
						//自定义action
						mIF.addAction("com.restart.service");
						//注册广播接者
						registerReceiver(mReceiver, mIF);
					}

					@Override
					public void onDestroy() {
						super.onDestroy();

						Intent intent = new Intent();
						intent.setAction("com.restart.service");
						//发送广播
						sendBroadcast(intent);

						unregisterReceiver(mReceiver);
					}
				}
				
			//双进程方式
			
				//创建服务1，
				public class Service1 extends Service {  
				//handler用于接收消息
					private Handler handler = new Handler() {  
						public void handleMessage(android.os.Message msg) {  
							switch (msg.what) {  
							case 1:  
				//当收到发送的消息是，启动服务2.
								startService2();  
								break;  
				  
							default:  
								break;  
							}  
				  
						};  
					};  
				  
					/** 
					 * 使用aidl 启动Service2 这是aidl接口实例，接口中定义两个方法，一个是停止服务的方法，一个是开始服务的方法。
					 */  
					private StrongService startS2 = new StrongService.Stub() {  
						@Override  
						public void stopService() throws RemoteException {  
				当调用停止服务时，停止服务2，
							Intent i = new Intent(getBaseContext(), Service2.class);  
							getBaseContext().stopService(i);  
						}  
				  
						@Override  
						public void startService() throws RemoteException { 
							//当调用启动服务时，启动服务2. 
							Intent i = new Intent(getBaseContext(), Service2.class);  
							getBaseContext().startService(i);  
						}  
					};  
				  
					/** 
					 * 在内存紧张的时候，系统回收内存时，会回调OnTrimMemory， 重写onTrimMemory当系统清理内存时从新启动Service2 
					 */  
					@Override  
					public void onTrimMemory(int level) {  
						/* 
						 * 启动service2 
						 */  
						startService2();  
					}  
				  
					@Override  
					public void onCreate() {  
						Toast.makeText(Service1.this, "Service1 正在启动...", Toast.LENGTH_SHORT)  
								.show();  
				//在启动服务1的时候，将服务2也启动，并监听服务2
						startService2();  
						/* 
						 * 此线程用监听Service2的状态 
						 */  
						new Thread() {  
							public void run() {  
								while (true) {  
				//判断服务2是否已经启动，如果没有，则发送一个消息给handler.
									boolean isRun = Utils.isServiceWork(Service1.this,  
											"com.lzg.strongservice.service.Service2");  
									if (!isRun) {  
										Message msg = Message.obtain();  
										msg.what = 1;  
										handler.sendMessage(msg);  
									}  
									try {  
										Thread.sleep(1);  
									} catch (InterruptedException e) {  
										// TODO Auto-generated catch block  
										e.printStackTrace();  
									}  
								}  
							};  
						}.start();  
					}  
				  
					/** 
					 * 判断Service2是否还在运行，如果不是则启动Service2 
					 */  
					private void startService2() {  
						boolean isRun = Utils.isServiceWork(Service1.this,  
								"com.lzg.strongservice.service.Service2");  
						if (isRun == false) {  
							try {  
								startS2.startService();  
							} catch (RemoteException e) {  
								e.printStackTrace();  
							}  
						}  
					}  
				  
					@Override  
					public int onStartCommand(Intent intent, int flags, int startId) {  
				//这个方法就是使用了重器标志
						return START_STICKY;  
					}  
				  
					@Override  
					public IBinder onBind(Intent intent) {  
						return (IBinder) startS2;  
					}  
				} 
				
		
		3. 用户通过 settings->Apps->Downloaded->Force Stop方式强制性杀死Service
			创建定时器，通过定时启动任务，每隔一定时间重新启动一次服务即可。

			
	常见问题：
		高版本隐式启动：
			1)
				Intent intent = new Intent();
				intent.setAction("");
				intent.setPackage("");
				startService(serviceIntent);
				
			2) 
				Intent intent = new Intent();
				intent.setAction("");
				final Intent serviceIntent = new Intent(getExplicitIntent(this,mIntent));
				startService(serviceIntent);
				
				public static Intent getExplicitIntent(Context context, Intent implicitIntent) {
					// Retrieve all services that can match the given intent
					 PackageManager pm = context.getPackageManager();
					 List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);
					 // Make sure only one match was found
					 if (resolveInfo == null || resolveInfo.size() != 1) {
						 return null;
					 }
					 // Get component info and create ComponentName
					 ResolveInfo serviceInfo = resolveInfo.get(0);
					 String packageName = serviceInfo.serviceInfo.packageName;
					 String className = serviceInfo.serviceInfo.name;
					 ComponentName component = new ComponentName(packageName, className);
					 // Create a new intent. Use the old one for extras and such reuse
					 Intent explicitIntent = new Intent(implicitIntent);
					 // Set the component to be explicit
					 explicitIntent.setComponent(component);
					 return explicitIntent;
				}
				
	
	
	
	扩展：
		1. android:persistent 属性
			该属性为true的app具有如下特点：
				1) 在系统启动的时候会被系统启动起来
				2) 在该app被强制杀掉后系统会重新启动该app，这种情况只针对系统内置app，第三方安装的app不会被重启