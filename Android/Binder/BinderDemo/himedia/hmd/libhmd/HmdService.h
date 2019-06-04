
#ifndef ANDRIOD_HMDSERVICE_H
#define ANDRIOD_HMDSERVICE_H

#include "IHmdService.h"

#ifdef __cplusplus
extern "C" {
#endif

namespace android{

class HmdService:public BnHmdService{

public:
	static void instantiate();
	virtual int do_system_cmd_hmd(const char *cmd);
private:
	HmdService();
	virtual ~HmdService();
};
};

#ifdef __cplusplus
}
#endif
#endif