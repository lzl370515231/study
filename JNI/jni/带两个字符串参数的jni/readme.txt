1.��Ӧ��Ŀ¼����android_os_hmdSysPropSet.cpp��hmdSysPropSet.java
base/core/jni/android_os_hmdSysPropSet.cpp
base/core/java/android/os/hmdSysPropSet.java

2.�޸�����
  1.base/core/jni/AndroidRuntime.cpp

  extern int  register_android_os_hmdSysPropSet(JNIEnv *env);//add by cg (��ӵ�namespace android �����ռ���)
 
  REG_JNI(register_android_os_hmdSysPropSet),//add by cg

   2.base/core/jni/Android.mk  //�������صĶ�̬��so
    LOCAL_SHARED_LIBRARIES := \
	libhmdSysPropSet \

    LOCAL_SRC_FILES:= \
     android_os_hmdSysPropSet.cpp \
3.��hmdSystemPropertiesSet����externalĿ¼�£������������.so


4.Shutdown��һ������jni������
