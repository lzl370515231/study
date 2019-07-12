#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_lzl_builder_jni_natives_StringFromJni_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
