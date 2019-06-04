LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
    com_hmd_Hmd.cpp

LOCAL_SHARED_LIBRARIES := \
	libandroid_runtime \
	libnativehelper \
	libutils \
	libbinder \
    libui \
	libcutils \
	libhmdservice


LOCAL_C_INCLUDES += \
    frameworks/base/core/jni \
    $(LOCAL_PATH)/../libhmd \
    $(JNI_H_INCLUDE)


LOCAL_MODULE_TAGS := optional

LOCAL_MODULE:= libhmd_jni

LOCAL_PRELINK_MODULE:= false

ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_MODULE)

include $(BUILD_SHARED_LIBRARY)
