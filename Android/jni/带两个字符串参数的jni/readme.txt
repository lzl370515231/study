1.相应的目录加入android_os_hmdSysPropSet.cpp、hmdSysPropSet.java
base/core/jni/android_os_hmdSysPropSet.cpp
base/core/java/android/os/hmdSysPropSet.java

2.修改以下
  1.base/core/jni/AndroidRuntime.cpp

  extern int  register_android_os_hmdSysPropSet(JNIEnv *env);//add by cg (添加到namespace android 命名空间里)
 
  REG_JNI(register_android_os_hmdSysPropSet),//add by cg

   2.base/core/jni/Android.mk  //包含机关的动态库so
    LOCAL_SHARED_LIBRARIES := \
	libhmdSysPropSet \

    LOCAL_SRC_FILES:= \
     android_os_hmdSysPropSet.cpp \
3.把hmdSystemPropertiesSet放置external目录下，编译出来的是.so


4.Shutdown是一个调用jni的例子
