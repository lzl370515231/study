Android 创建一个实时监听的服务
	
	1. 在frameworks/base/core/java/android/com/下添加aidl文件
		frameworks\base\core\java\com\xxx\xxx\aidl\IGestureHoverServiceManager.aidl
		frameworks\base\core\java\com\xxx\xxx\aidl\IGestureHoverListener.aidl
		
		interface IGestureHoverServiceManager {  
			void registerListener(IGestureHoverListener listener);  
			void unregisterListener(IGestureHoverListener listener);   
			void test();  
			void setRange(float startX,float startY,float endX,float endY);  
		}
		
		interface IGestureHoverListener {
			void onHoverDown();    
			void onHoverStay();
			void onHoverUp();
			void getRange(float startX,float startY,float endX,float endY);
		}
	
	
	2. 修改frameworks/base/Android.mk文件,将aidl加到该文件中	
		LOCAL_SRC_FILES += \
			core/java/com/xxx/xxx/aidl/IGestureHoverServiceManager.aidl \
			core/java/com/xxx/xxx/aidl/IGestureHoverListener.aidl \
		
	3. 在frameworks/base/services/core/java/com/android/server/下添加service文件
		
		public class GestureHoverService extends IGestureHoverServiceManager.Stub{
    
			private static final String TAG = "GestureHoverService";
			
			private Context mContext; 
			private final CallbacksHandler mCallbacksHandler;    
			
			public static class Lifecycle extends SystemService {
				protected GestureHoverService mGestureHoverService;
		 
				public Lifecycle(Context context) {
					super(context);
				}
		 
				@Override
				public void onStart() {
					Log.i(TAG,"onStart");
					GestureHoverService = new GestureHoverService(getContext());
					publishBinderService(Context.HOVER_SERVICE, mGestureHoverService);
					mGestureHoverService.start();
				}
				
				@Override
				public void onBootPhase(int phase) {
					Log.i(TAG,"onBootPhase");
					if (phase == SystemService.PHASE_ACTIVITY_MANAGER_READY) {
						mGestureHoverService.systemReady();
					} else if (phase == SystemService.PHASE_BOOT_COMPLETED) {
						mGestureHoverService.bootCompleted();
					}
				}
			}
			
			public GestureHoverService(Context context){
				Log.i(TAG,"GestureHoverService");
				mContext = context;
				mCallbacksHandler = new CallbacksHandler(FgThread.get().getLooper());
			}
			
			@Override
			public void test() {
				mCallbacksHandler.testH();
				Log.i(TAG,"test ok");
			}
			
			@Override
			public void setRange(float startX,float startY,float endX,float endY) {
				mCallbacksHandler.getRange(startX,startY,endX,endY);
				Log.i(TAG,"setRange ok");
			}
			
			@Override
			public void registerListener(IGestureHoverListener listener) {
				mCallbacksHandler.register(listener);
			}
		 
			@Override
			public void unregisterListener(IGestureHoverListener listener) {
				mCallbacksHandler.unregister(listener);
			}
			
			private static class CallbacksHandler extends Handler {
				private static final int MSG_HOVER_DOWN = 1;
				private static final int MSG_HOVER_STAY = 2;
				private static final int MSG_HOVER_UP = 3;
				private static final int MSG_RANGE = 4;
				
				private final RemoteCallbackList<IGestureHoverListener>
						mCallbacks = new RemoteCallbackList<>();
		 
				public CallbacksHandler(Looper looper) {
					super(looper);
				}
		 
				public void register(IGestureHoverListener callback) {
					mCallbacks.register(callback);
				}
		 
				public void unregister(IGestureHoverListener callback) {
					mCallbacks.unregister(callback);
				}
				@Override
				public void handleMessage(Message msg) {
					final SomeArgs args = (SomeArgs) msg.obj;
					final int n = mCallbacks.beginBroadcast();
					for (int i = 0; i < n; i++) {
						final IGestureHoverListener callback = mCallbacks.getBroadcastItem(i);
						try {
							invokeCallback(callback, msg.what, args);
						} catch (RemoteException ignored) {
						}
					}
					mCallbacks.finishBroadcast();
					args.recycle();
				}
				
				private void invokeCallback(IGestureHoverListener callback, int what, SomeArgs args) throws RemoteException {
					switch (what) {
						case MSG_HOVER_DOWN: {
							Log.i("zhi","se-down");
							callback.onHoverDown();
							break;
						}
						case MSG_HOVER_STAY: {
							Log.i("zhi","se-stay");
							callback.onHoverStay();
							break;
						}
						case MSG_HOVER_UP: {
							Log.i("zhi","se-up");
							callback.onHoverUp();
							break;
						}
						case MSG_RANGE:{
							Log.i("zhi","se-MSG_RANGE");
							callback.getRange((float)args.arg1,(float)args.arg2,(float)args.arg3,(float)args.arg4);
							break;
						}    
					}
				}
				
				public void testH() {
					final SomeArgs args = SomeArgs.obtain();
					args.argi1 = 1;
					obtainMessage(MSG_HOVER_STAY, args).sendToTarget();
					Log.i(TAG,"testH ok");
				}
				
				public void getRange(float startX,float startY,float endX,float endY){
					final SomeArgs args = SomeArgs.obtain();
					args.arg1 = startX;
					args.arg2 = startY;
					args.arg3 = endX;
					args.arg4 = endY;
					obtainMessage(MSG_RANGE, args).sendToTarget();
					Log.i("zhi","se-getRange ok");
				}    
			}
		}
		
	4. 在frameworks/base/core/java/android/app/下添加manager
		@SystemService(Context.HOVER_SERVICE)
		public class GestureHoverManager{
			private static final String TAG = "GestureHoverManager";
			private final Context mContext;
			private final IGestureHoverServiceManager mGestureHoverServiceManager;
			private final ArrayList<GestureHoverListenerDelegate> mDelegates = new ArrayList<>();
			private final Looper mLooper;    
			/**
			 *
			 * @hide
			 */    
			public GestureHoverManager(Context context,IGestureHoverServiceManager service, Looper looper) throws ServiceNotFoundException{
				mContext = context;
				//mGestureHoverServiceManager = IGestureHoverServiceManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.HOVER_SERVICE));
				mGestureHoverServiceManager = service;
				mLooper = looper;
				Log.i("lzq","mGestureHoverServiceManager:"+mGestureHoverServiceManager);
			} 
			
			/**
			 *
			 * @hide
			 */ 
			public void test(){
				try {
					mGestureHoverServiceManager.test();
				} catch (RemoteException e) {
					throw e.rethrowFromSystemServer();
				}
		 
			}
			
			/**
			 *
			 * @hide
			 */ 
			public void setRange(float startX,float startY,float endX,float endY){
				try {
					mGestureHoverServiceManager.setRange(startX,startY,endX,endY);
				} catch (RemoteException e) {
					throw e.rethrowFromSystemServer();
				}
		 
			}
			
			/**
			 *
			 * @hide
			 */
			public void registerListener(GestureHoverListener listener) {
				Log.i("lzq","registerListener:"+listener);
				synchronized (mDelegates) {
					final GestureHoverListenerDelegate delegate = new GestureHoverListenerDelegate(listener,mLooper);        
					try {
						mGestureHoverServiceManager.registerListener(delegate);
					} catch (RemoteException e) {
						throw e.rethrowFromSystemServer();
					}
					mDelegates.add(delegate);
				}    
			}
			
			/**
			 *
			 * @hide
			 */
			public void unregisterListener(GestureHoverListener listener) {
				Log.i("lzq","unregisterListener:"+listener);
				synchronized (mDelegates) {
					for (Iterator<GestureHoverListenerDelegate> i = mDelegates.iterator(); i.hasNext();) {
						final GestureHoverListenerDelegate delegate = i.next();
						if (delegate.mCallback == listener) {
							try {
								mGestureHoverServiceManager.unregisterListener(delegate);
							} catch (RemoteException e) {
								throw e.rethrowFromSystemServer();
							}
							i.remove();
						}
					}
				}
			}
			
			private static class GestureHoverListenerDelegate extends IGestureHoverListener.Stub implements Handler.Callback{

				final GestureHoverListener mCallback;
				final Handler mHandler;
				private static final int MSG_HOVER_DOWN = 1;
				private static final int MSG_HOVER_STAY = 2;
				private static final int MSG_HOVER_UP = 3;
				private static final int MSG_RANGE = 4;
				public GestureHoverListenerDelegate(GestureHoverListener callback,Looper looper){
					mCallback = callback;
					mHandler = new Handler(looper, this);            
				}
		 
		 
				@Override
				public boolean handleMessage(Message msg) {
					final SomeArgs args = (SomeArgs) msg.obj;
					switch (msg.what) {
						case MSG_HOVER_DOWN:
							Log.i("zhi","ma-down");
							mCallback.onHoverDown();
							args.recycle();
							return true;
						case MSG_HOVER_STAY:
							Log.i("zhi","ma-stay");
							mCallback.onHoverStay();
							args.recycle();
							return true;
						case MSG_HOVER_UP:
							Log.i("zhi","ma-up");
							mCallback.onHoverUp();
							args.recycle();
							return true;
						case MSG_RANGE:
							mCallback.getRange((float)args.arg1,(float)args.arg2,(float)args.arg3,(float)args.arg4);
							args.recycle();
							return true;
					}
					args.recycle();
					return false;
				}
				
				@Override
				public void onHoverDown(){
					final SomeArgs args = SomeArgs.obtain();
					mHandler.obtainMessage(MSG_HOVER_DOWN, args).sendToTarget();
				}
				
				@Override
				public void onHoverStay(){
					final SomeArgs args = SomeArgs.obtain();
					mHandler.obtainMessage(MSG_HOVER_STAY, args).sendToTarget();            
				}
				
				@Override
				public void onHoverUp(){
					final SomeArgs args = SomeArgs.obtain();
					mHandler.obtainMessage(MSG_HOVER_UP, args).sendToTarget();            
				}
				
				@Override
				public void getRange(float startX,float startY,float endX,float endY){
					final SomeArgs args = SomeArgs.obtain();
					args.arg1 = startX;
					args.arg2 = startY;
					args.arg3 = endX;
					args.arg4 = endY;
					mHandler.obtainMessage(MSG_RANGE, args).sendToTarget();            
				}        
			}        
		}
	
	
	5. 在frameworks/base/core/java/android/content/Context.java中添加service名称定义
		/**
		 *
		 * @see #getSystemService
		 * @hide
		 */
		public static final String HOVER_SERVICE = "gesture";
		
	6. 在frameworks/base/services/java/com/android/server/SystemServer.java中添加服务
		private static final String HOVER_SERVICE_CLASS = "com.android.server.GestureHoverService$Lifecycle";
		
		try {
            mSystemServiceManager.startService(HOVER_SERVICE_CLASS);
        } catch (Throwable e) {
            reportWtf("starting HoverService", e);
        }
	
	7. 在frameworks/base/core/java/android/app/SystemServiceRegistry.java中注册服务
		registerService(Context.HOVER_SERVICE, GestureHoverManager.class,new CachedServiceFetcher<GestureHoverManager>() {
            @Override
            public GestureHoverManager createService(ContextImpl ctx) throws ServiceNotFoundException {
                IGestureHoverServiceManager service = 
                        IGestureHoverServiceManager.Stub.asInterface(ServiceManager.getServiceOrThrow(Context.HOVER_SERVICE));
                return new GestureHoverManager(ctx, service ,ctx.mMainThread.getHandler().getLooper());
            }
		});
	
	8. 配置系统服务权限
		device\mediatek\sepolicy\full\plat_private\service_contexts.te
		device\mediatek\sepolicy\full\prebuilts\api\26.0\plat_public\service_contexts.te
			gesture                        u:object_r:tra_ges_service:s0
		
		device\mediatek\sepolicy\full\plat_private\service.te
		device\mediatek\sepolicy\full\prebuilts\api\26.0\plat_public\service.te
			type tra_ges_service, app_api_service, system_server_service, service_manager_type;

	9. app中使用服务
		private GestureHoverManager mGestureHoverManager;
		mGestureHoverManager = (GestureHoverManager)getApplicationContext().getSystemService(Context.HOVER_SERVICE); 
		
		@Override
		protected void onResume() {
			 mGestureHoverManager.registerListener(mGestureHoverListener); 
		}
		
		@Override
		protected void onPause() {  
			super.onPause();
			mGestureHoverManager.unregisterListener(mGestureHoverListener); 
		}
		
		private final GestureHoverListener mGestureHoverListener = new GestureHoverListener() {
			@Override
			public void onHoverDown() {
				
			}
	 
			@Override
			public void onHoverStay() {
				Log.i("zhi","settings-stay");
			}
			@Override
			public void onHoverUp() {
	 
			}
			@Override
			public void getRange(float startX,float startY,float endX,float endY) {
				
			}
		};
		
		

			