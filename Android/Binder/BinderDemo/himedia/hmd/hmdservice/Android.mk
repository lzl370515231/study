LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:= \
	hmdservice.cpp 

LOCAL_SHARED_LIBRARIES := \
	libhmdservice \
	libutils \
	libbinder

LOCAL_C_INCLUDES := \
    $(LOCAL_PATH)/../libhmd

LOCAL_MODULE_TAGS := optional

LOCAL_MODULE:= hmdservice

ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_MODULE)

include $(BUILD_EXECUTABLE)