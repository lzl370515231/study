Amlogic T968ƽ̨�µ�binder ��дdeviceinfo���� 
1.��himedia�ŵ�frameworks/baseĿ¼��


2.��X:\T968_0422\device\customer\p313\device.mk����
########################
#add by cg make hmd binder
PRODUCT_PACKAGES += \
hmdservice \
libhmd_jni \
libhmdservice \
hmd_interface
################


3.��X:\T968_0422\build\core\pathmap.mk
  ��FRAMEWORKS_BASE_SUBDIRS := \ ������
      himedia/external \   
      himedia/hmd \
	  
4.��X:\T968_0422\device\amlogic\common\products\tv\init.amlogic.rc����
#add by cg for hmd binder
service hmd /system/bin/hmdservice
    class main
    user root
    group root 
    ioprio rt 4
    oneshot	


5.deviceinfo Ӧ�ò���demo
   libhmd ��deviceinfo�ײ����	