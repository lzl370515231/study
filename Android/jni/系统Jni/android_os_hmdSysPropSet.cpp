/********************************************************************************************
*	Copyright(c)2014-05-07 …Ó€⁄∫£√¿µœ
* 		All rights reserved.
* 
*	File Name:	android_os_hmdSysPropSet.cpp
* 	Function:	hmdSysPropSet function for application to SystemProperties.set JNI
*	Writer:		nation(cg)
*	version: 	2014-05-07
*				
*********************************************************************************************/

#define LOG_TAG "android_os_hmdSysPropSet"
#define LOG_NDEBUG 0

#include "JNIHelp.h"
#include "jni.h"
#include "android_runtime/AndroidRuntime.h"
#include <utils/misc.h>
#include <utils/Log.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>





#ifdef __cplusplus
extern "C"
{
#endif

int hmdSysPropSet(const char *key,const char *value);

#ifdef __cplusplus
}
#endif


namespace android
{
static void throw_NullPointerException(JNIEnv *env, const char* msg)
{
    jclass clazz;
    clazz = env->FindClass("java/lang/NullPointerException");
    env->ThrowNew(clazz, msg);
}


static int hmdSysPropSet_native(JNIEnv *env,jobject obj,jstring key,jstring value)
{
	const char* keys;
	const char* values;
	int ret;
	jboolean isCopy;
	keys = env->GetStringUTFChars(key, &isCopy);
	values = env->GetStringUTFChars(value, &isCopy);
	ret = hmdSysPropSet(keys,values);
	printf("hmdSysPropSet_native: keys= %s , values = %s \n",(const char*)keys,(const char*)values);
	env->ReleaseStringUTFChars(key, keys);
	env->ReleaseStringUTFChars(value, values);
    return ret;
}


static JNINativeMethod method_table[] = {
    { "hmdSysPropSetnative", "(Ljava/lang/String;Ljava/lang/String;)I", (void*)hmdSysPropSet_native },
};

int register_android_os_hmdSysPropSet(JNIEnv *env)
{
    return AndroidRuntime::registerNativeMethods(
        env, "android/os/hmdSysPropSet",
        method_table, NELEM(method_table));
}

};

