LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libauio_resample
LOCAL_MULTILIB := 32
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := speex_resampler.c \
					linear_resampler.c	\
					resample.c

LOCAL_LDLIBS := -llog

LOCAL_C_INCLUDES := \
	$(LOCAL_PATH)
		
#include $(BUILD_SHARED_LIBRARY)
include $(BUILD_EXECUTABLE)
