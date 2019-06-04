Amlogic T968平台下的binder 读写deviceinfo分区 
1.把himedia放到frameworks/base目录下


2.在X:\T968_0422\device\customer\p313\device.mk增加
########################
#add by cg make hmd binder
PRODUCT_PACKAGES += \
hmdservice \
libhmd_jni \
libhmdservice \
hmd_interface
################


3.在X:\T968_0422\build\core\pathmap.mk
  在FRAMEWORKS_BASE_SUBDIRS := \ 下增加
      himedia/external \   
      himedia/hmd \
	  
4.在X:\T968_0422\device\amlogic\common\products\tv\init.amlogic.rc增加
#add by cg for hmd binder
service hmd /system/bin/hmdservice
    class main
    user root
    group root 
    ioprio rt 4
    oneshot	


5.deviceinfo 应用测试demo
   libhmd 是deviceinfo底层测试	