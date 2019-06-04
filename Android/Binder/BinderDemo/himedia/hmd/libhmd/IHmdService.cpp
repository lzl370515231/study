
#undef NDEBUG
#define LOD_TAG "HMD IHmdService"

#include <binder/Parcel.h>
#include "utils/Log.h"
#include "IHmdService.h"
#define DEBUG true

namespace android{

enum {
	DOSYSTEM,
};

//¿Í»§¶Ë
class BpHmdService: public BpInterface<IHmdService>{

public:
	BpHmdService(const sp<IBinder>& impl)
		: BpInterface<IHmdService>(impl){
	}
	int do_system_cmd_hmd(const char *cmd){
		if(DEBUG){
			ALOGD("IHmdService::do_system_cmd_hmd()  cmd = %s ", cmd);
		}
		Parcel data , reply;
		data.writeInterfaceToken(IHmdService::getInterfaceDescriptor());
		data.writeCString(cmd);
		remote()->transact(DOSYSTEM, data, &reply);
		return reply.readInt32();
	}
};

IMPLEMENT_META_INTERFACE(HmdService, "com.himedia.IHmdService");

status_t BnHmdService::onTransact(
	uint32_t code, const Parcel& data, Parcel* reply, uint32_t flags){

	switch(code){
	
	case DOSYSTEM:{
 	 CHECK_INTERFACE(IHmdService, data, reply);
		const char *cmd = data.readCString();
		int ret = do_system_cmd_hmd(cmd);
		if(DEBUG){
			ALOGD("DOSYSTEM BpHmdService cmd=%s", cmd);
		}
		reply->writeInt32(ret);
		return NO_ERROR;
	}
	break;
	default:
		return BBinder::onTransact(code, data, reply, flags);
	}
}

};
