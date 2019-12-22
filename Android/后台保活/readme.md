为什么我们的后台进程/Service会被结束掉？
	1、Android系统内存回收机制；
	2、各厂商对后台程序的一个管理制度（就是允许程序后台运行那个）；
	3、第三方软件的清理(360什么的)。

其中有的后台程序保护把程序结束的同时会把程序弄成停止状态，导致无法接收广播！

保活策略：
	1、可以通过监听系统广播来把自己拉起来
	2、可以多个app相互拉
	3、可以把自己的服务搞成前台服务
	4、在service的onstart方法里返回 STATR_STICK
	5、添加Manifest文件属性值为android:persistent=“true”
	6、覆写Service的onDestroy方法
	7、服务互相绑定
	8、设置闹钟，定时唤醒
	9、自己的app在native层fork一个子进程来与主进程互拉。



## 双进程保活

##### LocalService.java

```java
public class LocalService extends Service {
    
    private static final String TAG = LocalService.class.getName();
    private KeepAliveBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                CommonLog.d("LocalService", "connected with " + iMyAidlInterface.getServiceName());
                if (!App.mApp.isMainActivityExist()) {	//判断应用是否启动
                    Intent intent = new Intent(LocalService.this.getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplication().startActivity(intent);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            CommonLog.d("LocalService", "connected with LocalService" );
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
           // Toast.makeText(LocalService.this, "链接断开，重新启动 RemoteKeepAliveService", Toast.LENGTH_LONG).show();
            CommonLog.d(TAG, "onServiceDisconnected: 链接断开，重新启动 RemoteKeepAliveService");
            startService(new Intent(LocalService.this, RemoteKeepAliveService.class));
            bindService(new Intent(LocalService.this, RemoteKeepAliveService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    public LocalService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        CommonLog.d(TAG, "onStartCommand: LocalService 启动");
      //  Toast.makeText(this, "LocalService 启动", Toast.LENGTH_LONG).show();
        startService(new Intent(LocalService.this, RemoteKeepAliveService.class));
        bindService(new Intent(LocalService.this, RemoteKeepAliveService.class), connection, Context.BIND_IMPORTANT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new KeepAliveBinder();
        return mBinder;
    }

    private class KeepAliveBinder extends IMyAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return LocalService.class.getName();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }
}

```

##### IMyAidlInterface.aidl

```java
interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);

            String getServiceName();
}
```

##### RemoteKeepAliveService.java

声明为其他进程

```java
public class RemoteKeepAliveService extends Service {
    private static final String TAG = RemoteKeepAliveService.class.getName();
    private KeepAliveBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            CommonLog.d(TAG, "connected with RemoteKeepAliveService");
            IMyAidlInterface iMyAidlInterface = IMyAidlInterface.Stub.asInterface(service);
            try {
                CommonLog.e(TAG, "connected with " + iMyAidlInterface.getServiceName());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            CommonLog.e(TAG, "onServiceDisconnected: 链接断开，重新启动 LocalService");
           // Toast.makeText(RemoteKeepAliveService.this, "链接断开，重新启动 LocalService", Toast.LENGTH_LONG).show();
            startService(new Intent(RemoteKeepAliveService.this, LocalService.class));
            bindService(new Intent(RemoteKeepAliveService.this, LocalService.class), connection, Context.BIND_IMPORTANT);
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        CommonLog.e(TAG, "onStartCommand: RemoteKeepAliveService 启动");
        //Toast.makeText(this, "RemoteKeepAliveService 启动", Toast.LENGTH_LONG).show();
        bindService(new Intent(this, LocalService.class), connection, Context.BIND_IMPORTANT);

    }

    public RemoteKeepAliveService() {
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBinder = new KeepAliveBinder();
        return mBinder;
    }

    private class KeepAliveBinder extends IMyAidlInterface.Stub {

        @Override
        public String getServiceName() throws RemoteException {
            return RemoteKeepAliveService.class.getName();
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    }
}
```







[Android进程保活招式大全]: https://chuansongme.com/n/504099451432

