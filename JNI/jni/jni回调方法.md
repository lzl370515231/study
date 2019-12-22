## JNI回调的三种方法



### 同一线程回调

直接用 findClass 或者 GetObjectClass，进行回调。

#### Java层代码

```java
public class Sdk {
    private Sdk() {
    }

    //单例
    private static class SdkHodler {
        static Sdk instance = new Sdk();
    }

    public static Sdk getInstance() {
        return SdkHodler.instance;
    }

    //调到C层的方法
    private native void nativeDownload();

    //c层回调上来的方法
    private int onProgressCallBack(long total, long already) {
        //自行执行回调后的操作
        System.out.println("total:"+total);
        System.out.println("already:"+already);
        return 1;
    }
}
```

#### C层代码

```c
JNIEXPORT void JNICALL Java_xxx_nativeDownload(JNIEnv *env,jobject thiz){
    
    //直接用GetObjectClass 找到 Class，也就是Sdk.class
    jclass jSdkClass = (*env)->GetObjectClass(env,thiz);
    if(jSdkClass == 0){
        ALOGE("Unable to find class");
        return;
    }
    
    //找到需要调用的方法ID
    jmethodID javaCallback = （*env）->GetMethodID(env,jSdkClass,"onProgressCallBack","(JJ)I");
    //进行回调，ret是java层的返回值
    jint ret = (*env)->CallIntMethod(env,thiz,javaCallback,1,1);
    return ;
}

//或者是

JNIEXPORT void JNICALL Java_xxx_nativeDownload(JNIEnv *env,jobject thiz){
    
    //直接用 findClass 找到 Class，也就是 Sdk.class
    jclass jSdkClass = (*env)->FindClass(env,"package/name/Sdk");
    if(jSdkClass == 0){
        LOG("Unable to find class");
        return;
    }
    //找到需要调用的方法ID
    jmethodID javaCallback = (*env)->GetMethodID(env,jSdkClass,"<init>","()V");
    jobject jSdkObject = (*env)->NewObject(env,jSdkClass,sdkInt);
    
    //进行回调，ret 是java层的返回值（这个有些场景很好用）
    jint ret =(*env)->CallIntMethod(env,jSdkObject,javaCallback,1,1);
    return ;
}
```



### 其他线程里回调

通过 NewGlobalRef，保存全局变量

#### java层代码

```java
public class Sdk {
    private Sdk() {
    }

    //单例
    private static class SdkHodler {
        static Sdk instance = new Sdk();
    }

    public static Sdk getInstance() {
        return SdkHodler.instance;
    }

    //调到C层的方法
    private native void nativeDownload();

    //c层回调上来的方法
    private int onProgressCallBack(long total, long already) {
        //自行执行回调后的操作
        System.out.println("total:"+total);
        System.out.println("already:"+already);
        return 1;
    }
}
```

#### C层代码

```c
JavaVM *g_VM;
jobject g_obj;
JNIEXPORT void JNICALL Java_xxx_nativeDownload(JNIEnv *env, jobject thiz) {
 
    //JavaVM是虚拟机在JNI中的表示，等下再其他线程回调java层需要用到
    (*env)->GetJavaVM(env, &g_VM);
  // 生成一个全局引用保留下来，以便回调
    g_obj = (*env)->NewGlobalRef(env, thiz);
    
    // 此处使用c语言开启一个线程，进行回调，这时候java层就不会阻塞，只是在等待回调
    pthread_create(xxx,xxx, download,NULL);
    return ;
}

//在此处跑在子线程中，并回调到java层
void download(void *p) {
    JNIEnv *env;
 
    //获取当前native线程是否有没有被附加到jvm环境中
   int getEnvStat = (*g_VM)->GetEnv(g_VM, (void **) &env,JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        //如果没有， 主动附加到jvm环境中，获取到env
        if ((*g_VM)->AttachCurrentThread(g_VM, &env, NULL) != 0) {
            return;
        }
        mNeedDetach = JNI_TRUE;
    }

    //通过全局变量g_obj 获取到要回调的类
    jclass javaClass = (*env)->GetObjectClass(env, g_obj);

    if (javaClass == 0) {
        LOG("Unable to find class");
        (*g_VM)->DetachCurrentThread(g_VM);
        return;
    }

   //获取要回调的方法ID
    jmethodID javaCallbackId = (*env)->GetMethodID(env, jSdkClass,
                                                 "onProgressCallBack", "(JJ)I");
    if (javaCallbackId == NULL) {
        LOGD("Unable to find method:onProgressCallBack");
        return;
    }
   //执行回调
    (*env)->CallIntMethod(env, g_obj, javaCallbackId,1,1);
    
    //释放当前线程
   if(mNeedDetach) {
        (*g_VM)->DetachCurrentThread(g_VM);
    }
    env = NULL;
}
```

#### 拓展

多线程任务下载，然后需要回调进度，那么多的线程都一并回调到onProgressCallBack这一个函数。

##### Java层代码

```java
public class Sdk {


    public Sdk() {
    }

    //单例
    private static class SdkHodler {
        static Sdk instance = new Sdk();
    }

    public static Sdk getInstance() {
        return SdkHodler.instance;
    }
    //回调分发接口
    public interface OnSubProgressListener {

        public int onProgressChange(long total, long already);
    };

    private Map<Long, OnSubProgressListener> mMap = new HashMap<>();



    //调到C层的方法
    private native int nativeDownload(long uid,String downloadPath);


    //回调的方法
    private int onProgressCallBack(long uid, long total, long already) {
        OnSubProgressListener listener =  mMap.get(uid);
        if(listener != null) {
            if(already >= total) {
                //下载完成，取消回调
                mMap.remove(uid);
            } else {
                //回调到指定任务去，通过uid辨别
                listener.onProgressChange(total,already);
            }
        }
        return 0;
    }

    public void download(long uid,String downloadPath,OnSubProgressListener l) {
        mMap.put(uid,l);
        nativeDownload(uid,downloadPath);
    }

}
```



##### C层代码

```c
带着uid 去执行任务，回调时候，把uid 回传到java层上面的
private int onProgressCallBack(long uid, long total, long already);
就可以区分是哪一个任务，并且取出Map里面存好的OnSubProgressListener接口进行回调
(这部分就不写了， 比较简单， 后面有读者要求我再补上)
```

##### 开启两个下载任务

```java
Sdk.getInstance().download(1,"xxx.jpg",new OnSubProgressListener(){

            @Override
            public int onProgressChange(long total, long already) {
            
                return 0;
            }
        });
        
Sdk.getInstance().download(2,"xxx.png",new OnSubProgressListener(){

            @Override
            public int onProgressChange(long total, long already) {
                return 0;
            }
        });
```

### 通过把接口 jobject 传递到 C层

在 c 层里面进行回调。

#### Java层

```java
public class Sdk {


    public Sdk() {
    }

    //单例
    private static class SdkHodler {
        static Sdk instance = new Sdk();
    }

    public static Sdk getInstance() {
        return SdkHodler.instance;
    }
    //回调到各个线程
    public interface OnSubProgressListener {

        public int onProgressChange(long total, long already);
    };
    
    //调到C层的方法
    private native int nativeDownload(String downloadPath,OnSubProgressListener l);

}
```



#### C 层

```c
JavaVM *g_VM;
JNIEXPORT void JNICALL Java_xxx_nativeDownload(JNIEnv *env, jobject thiz，jstring jpath，jobject jcallback) {
 
    //JavaVM是虚拟机在JNI中的表示，等下再其他线程回调java层需要用到
    (*env)->GetJavaVM(env, &g_VM);
    
    //生成一个全局引用，回调的时候findclass才不会为null
    jobject callback = (*env)->NewGlobalRef(env, jcallback)
    
    // 把接口传进去，或者保存在一个结构体里面的属性， 进行传递也可以
    pthread_create(xxx,xxx, download,callback);
    return ;
}

//在此处跑在子线程中，并回调到java层
void download(void *p) {

   if(p == NULL) return ;
  
    JNIEnv *env;
       //获取当前native线程是否有没有被附加到jvm环境中
    int getEnvStat = (*g_VM)->GetEnv(g_VM, (void **)   &env,JNI_VERSION_1_6);
    if (getEnvStat == JNI_EDETACHED) {
        //如果没有， 主动附加到jvm环境中，获取到env
        if ((*g_VM)->AttachCurrentThread(g_VM, &env, NULL) != 0) {
            return;
        }
        mNeedDetach = JNI_TRUE;
    }
    //强转回来
    jobject jcallback = (jobject)p;
    
    //通过强转后的jcallback 获取到要回调的类
    jclass javaClass = (*env)->GetObjectClass(env, jcallback);

    if (javaClass == 0) {
        LOG("Unable to find class");
        (*g_VM)->DetachCurrentThread(g_VM);
        return;
    }

   //获取要回调的方法ID
    jmethodID javaCallbackId = (*env)->GetMethodID(env, javaClass,
                                                 "onProgressChange", "(JJ)I");
    if (javaCallbackId == NULL) {
        LOGD("Unable to find method:onProgressCallBack");
        return;
    }
   //执行回调
    (*env)->CallIntMethod(env, jcallback, javaCallbackId,1,1);
    
    //释放当前线程
   if(mNeedDetach) {
        (*g_VM)->DetachCurrentThread(g_VM);
    }
    env = NULL;
    
    //释放你的全局引用的接口，生命周期自己把控
     (*env)->DeleteGlobalRef(env, jcallback);
    jcallback = NULL;
}
```



```java
Sdk.getInstance().nativeDownload("xx.jpg",new OnSubProgressListener(){
    @Override
    public int onProgressChange(long total, long already) {
        return 0;
    }
});
        
Sdk.getInstance().nativeDownload("xx.png",new OnSubProgressListener(){
    @Override
    public int onProgressChange(long total, long already) {
        return 0;
    }
});  
```

#### 备注

少了 uid 这个参数，而且少了 map 去保存接口，优化了好多内存。直接把接口传到 jni 层，对应的数据是 jobject，在c层传递的这个接口的时候需`(*env)->NewGlobalRef(env, jcallback)` 生成全局引用进行传递。



### 要点

1. Native层的回调函数对象要全局话

   ```c
   jcallback = env->NewGlobalRef(callback);
   ```

2. Native代码中自己实现的回调方法，不带JNIEnv参数，无法调用 Java类

   ```C
   /// 保存全局 JavaVM示例
   (*env)->GetJavaVM(env,&g_VM);
   
   /// 获取当前 native 线程是否附加到 jvm 环境中
   int getEnvState = (*g_VM)->GetEnv(g_VM,(void **) &env,JNI_VERSION_1_6);
   if(getEnvState == JNI_EDETACHEDD){
   	//否，主动附加到 jvm 环境中，获取到 env
   	if((*g_VM)->AttachCurrentThread(g_VM,&env,NULL) != 0){
   		return;
   	}
   	mNeedDetach = JNI_TRUE;
   }
   
   ///获取回调的方法
   ///执行回调
   
   
   //释放当前线程
   if(mNeedDetach){
   	(*g_VM)->DetachCurrentThread(g_VM);
   }
   env = NULL;
   ```

   

