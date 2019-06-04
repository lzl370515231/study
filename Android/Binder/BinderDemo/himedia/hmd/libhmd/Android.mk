LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES:=               \
    IHmdService.cpp      \
    HmdService.cpp

LOCAL_SHARED_LIBRARIES :=     		\
	libcutils             			\
	libutils              			\
	libbinder             			\
	libandroid_runtime         \
	libhmd

LOCAL_C_INCLUDES += \
	libcore/include \
	system/core/include/cutils 
LOCAL_MODULE_TAGS := optional

LOCAL_MODULE:= libhmdservice

ALL_DEFAULT_INSTALLED_MODULES += $(LOCAL_MODULE)

LOCAL_PRELINK_MODULE:= false

include $(BUILD_SHARED_LIBRARY)
