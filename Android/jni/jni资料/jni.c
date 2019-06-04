
#include <jni.h>

static JNINativeMethod gMethods[] = {
    {"libVersion",               "()I",          (void *)ass_library_version},
    {"libInit",              "()Ljava/lang/Object;",                              (void *)ass_library_init},
    {"libDone",              "(Ljava/lang/Object;)V",                              (void *)ass_library_done},
    {"setFontsDir",               "(Ljava/lang/Object;Ljava/lang/String;)V",                              (void *)ass_set_fonts_dir},
    {"setExtractFonts",              "(Ljava/lang/Object;I)V",                              (void *)ass_set_extract_fonts},
    {"setStyleOverrides",                 "(Ljava/lang/Object;Ljava/lang/String;)V",                              (void *)ass_set_style_overrides},
    {"processForceStyle",             "(Ljava/lang/Object;)V",                             (void *)ass_process_force_style},
    {"setMessageCb",           "(Ljava/lang/Object;)V",                              (void *)ass_set_message_cb},
};

int register(JNIEnv *env) {
	//把jni中的方法和java文件中的方法关联起来
	return jniRegisterNativeMethods(env, "AssJni", gMethods, sizeof(gMethods) / sizeof(gMethods[0]));
}

//检查运行环境
jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jint result = JNI_ERR;
	
	gVM = vm;

	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("GetEnv failed! \n");
		return JNI_ERR;
	}
	
	if (register(env) != JNI_OK) {
		LOGE("can't register\n");
		return JNI_ERR;
	}
	return JNI_VERSION_1_4;
}