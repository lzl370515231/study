
#define LOG_TAG "HMD Jni"
#define LOG_NDEBUG 0
#define DEBUG false

#include "jni.h"
#include "JNIHelp.h"
#include <binder/IServiceManager.h>
#include "IHmdService.h"
#include "android_runtime/AndroidRuntime.h"

using namespace android;

//客户端代理接口
static sp<IHmdService> ihmdService;

static void init_native(JNIEnv *env){
	ALOGD("init");
	sp<IServiceManager> sm = defaultServiceManager();
	sp<IBinder> binder;
	do{
		binder = sm->getService(String16("himedia.hmd"));
		if(binder != 0){
			break;
		}
		if(DEBUG){
			ALOGE("himedia hmd service not published, waiting...");
		}
		usleep(500000);
	}while(true);

	ihmdService = interface_cast<IHmdService>(binder);
}

static void throw_NullPointerException(JNIEnv *env, const char* msg){
	jclass clazz;
	clazz = env->FindClass("java/lang/NullPointerException");
	env->ThrowNew(clazz, msg);
}

static int do_system_cmd_native(JNIEnv *env ,jobject obj){
		if(ihmdService == NULL){
		throw_NullPointerException(env,"hmd service has not start!");
		return -1;
	}
		const char *ckey = "mkdir -p /sdcard/lzlBuiler/test";
		int ret = ihmdService->do_system_cmd_hmd(ckey);
		return ret;
	
}

static JNINativeMethod method_table[] = {
	{ "nativeInit", "()V", (void*)init_native},
  { "nativeDoSystemCmd", "()I", (void*)do_system_cmd_native },
};

static int register_android_os_Hmd(JNIEnv *env){
	return AndroidRuntime::registerNativeMethods(
		env, "com/himedia/Hmd",method_table, NELEM(method_table));
}

jint JNI_OnLoad(JavaVM* vm, void* reserved){
	JNIEnv* env = NULL;
    jint result = -1;
	ALOGD("Hmd JNI_OnLoad()");
	
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        ALOGE("ERROR: GetEnv failed\n");
        goto bail;
    }
    assert(env != NULL);
	
    if (register_android_os_Hmd(env) < 0) {
        ALOGE("ERROR: Hmd native registration failed\n");
        goto bail;
    }
    /* success -- return valid version number */
    result = JNI_VERSION_1_4;

bail:
    return result;
}

