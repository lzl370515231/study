Messager:
	Messager实现IPC通信，底层也是使用了AIDL方式。和AIDL方式不同的是，Messager方式是利用Handler形式处理，因此，它是线程安全的，这也表示它不支持并发处理；而AIDL方式是非线程安全的，支持并发处理，因此，我们使用AIDL方式时需要保证代码的线程安全。
	
	在进程A中创建一个Message，将这个Message对象通过IMessenger.send(message)方法传递到进程B（当然，Message对象本身是无法被传递到进程B的，send(message)方法会使用一个Parcel对象对Message对象编集，再将Parcel对象传递到进程B中，然后解编集，得到一个和进程A中Message对象内容一样的对象），再把Message对象加入到进程B的消息队列里，Handler会去处理它。
	
	示例：
		//B 进程代码
		public class RemoteService extends Service {
			public static final int GET_RESULT = 1;
			private final Messenger mMessenger = new Messenger(new Handler() {
				private int remoteInt = 1;//返回到进程A的值
				@Override
				public void handleMessage(Message msg) {
					if (msg.what == GET_RESULT) {
						try {
							msg.replyTo.send(Message.obtain(null, GET_RESULT, remoteInt++, 0));
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					} else {
						super.handleMessage(msg);
					}
				}
			});
			
			@Override
			public IBinder onBind(Intent intent) {
				return mMessenger.getBinder();
			}
		}
		
		//A 进程代码
		private Messenger mService;
		private boolean isBinding = false;
		private ServiceConnection mConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mService = new Messenger(service);
				isBinding = true;
			}
		 
			@Override public void onServiceDisconnected(ComponentName name) {
				mService = null;
				isBinding = false;
			} 
		};
		
		//绑定进程B的服务
		bindService(new Intent(this, RemoteService.class), mConnection, BIND_AUTO_CREATE);
		
		//处理来自进程B回复的消息
		private Messenger mMessenger = new Messenger(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == RemoteServiceProxy.GET_RESULT) {
					 Log.i("TAG", "Int form process B is "+msg.arg1);//msg.arg1就是remoteInt
				} else {
					 super.handleMessage(msg);
				}
			}
		});
		
		//向进程B发一条消息，并接收来自进程B回复过来的消息
		Message message = Message.obtain(null, RemoteServiceProxy.GET_RESULT);
		message.replyTo = mMessenger;
		try {
			mService.send(message);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
加图片

	Messager的实现：
		public Messenger(Handler target) {
			mTarget = target.getIMessenger();
		}

		public IBinder getBinder() {
			return mTarget.asBinder();
		}

		public void send(Message message) throws RemoteException {
			mTarget.send(message);
		}

		Handler类的方法：

		final IMessenger getIMessenger() {
			synchronized (mQueue) {
				if (mMessenger != null) {
					return mMessenger;
				}
				mMessenger = new MessengerImpl();
				return mMessenger;
			}
		}

		private final class MessengerImpl extends IMessenger.Stub {
			public void send(Message msg) {
				Handler.this.sendMessage(msg);
			}
		}
		
		从上面代码可知，当进程A的Activity绑定进程B的RemoteService时，onBind方法返回一个MessengerImpl对象，它是IBinder类的子类。这个对象会被传递到进程A中，在进程A与进程B建立链接时，传到ServiceConnection.onServiceConnected(ComponentName name, IBinder service)方法中，IBinder service就是进程B返回的MessengerImpl对象。然后用service构建一个Messager对象
			mService = new Messenger(service);
			
			public Messenger(IBinder target) {
				mTarget = IMessenger.Stub.asInterface(target);
			}

		IMessenger.Stub.asInterface(target)方法会在进程A中创建一个IMessenger的代理类Proxy，看过AIDL的朋友会对这个很熟悉。
		当使用mService发送一条消息时，实际调用的是代理类Proxy的send(Message msg)方法。
		
			msg.writeToParcel(_data, 0);
		上面方法是将Message msg的内容写到Parcel _data对象中，然后调用进程B返回的MessengerImpl对象的transact方法。这个方法把_data对象传递到进程B中。
		在进程B中调用onTransact方法。
			this.send(_arg0);
		这个方法在进程B中恢复进程A传递过来的消息，然后调用this.send(_arg0);。再来看看send(_arg0)这个方法

			private final class MessengerImpl extends IMessenger.Stub {
				public void send(Message msg) {
					Handler.this.sendMessage(msg);
				}
			}