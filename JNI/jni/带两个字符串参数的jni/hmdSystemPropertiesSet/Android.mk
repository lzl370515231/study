LOCAL_PATH:= $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES:= hmdSysPropSet.c 

 LOCAL_C_INCLUDES := \
	$(LOCAL_PATH) \
	     
LOCAL_MODULE_TAGS := eng

LOCAL_SHARED_LIBRARIES := libcutils libc libusbhost

LOCAL_MODULE:= libhmdSysPropSet
#LOCAL_MODULE:= hmdSysPropSet
include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_EXECUTABLE)
#include $(BUILD_STATIC_LIBRARY)

