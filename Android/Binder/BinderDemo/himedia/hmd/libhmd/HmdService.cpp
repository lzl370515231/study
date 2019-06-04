
#undef NDEBUG
/*
#include <utils/misc.h>
#include <stdint.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <fcntl.h>

#ifdef HAVE_ANDROID_OS    
#else
#include <sys/user.h>
#endif

#include <cutils/properties.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/mman.h>
#include <stdlib.h>
*/
#define LOG_TAG "HMD HmdService"
#include "HmdService.h"
#include <binder/IServiceManager.h>
#include <utils/Log.h>
#define DEBUG true

#ifdef __cplusplus
extern "C"
{
#endif
int do_system_cmd(const char* cmd);
#ifdef __cplusplus
}
#endif

namespace android{

		void HmdService::instantiate(){
				defaultServiceManager()->addService(
				String16("himedia.hmd"), new HmdService());
		}
		
		HmdService::HmdService(){
				if(DEBUG){
						ALOGD("HmdService created");
				}
		}
		
		HmdService::~HmdService(){
				if(DEBUG){
						ALOGD("HmdService destroyed");
				}
		}
		int HmdService::do_system_cmd_hmd(const char *cmd){
				if(DEBUG){
						ALOGD("HmdService:do_system_cmd_hmd cmd=%s",cmd);
				}
				int ret =-1;
				///Ö´ÐÐÃüÁî½Ó¿Ú
				ret = do_system_cmd(cmd);
				return ret;
		}
}
