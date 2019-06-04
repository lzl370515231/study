
#ifndef ANDRIOD_IHMDSERVICE_H
#define ANDRIOD_IHMDSERVICE_H

#include <binder/IInterface.h>
#include <binder/Parcel.h>

namespace android {

class IHmdService: public IInterface{

public:
	DECLARE_META_INTERFACE(HmdService);
	virtual int do_system_cmd_hmd(const char *cmd)=0;
};

class BnHmdService: public BnInterface<IHmdService>
{
public:
    virtual status_t    onTransact( uint32_t code,
                                    const Parcel& data,
                                    Parcel* reply,
                                    uint32_t flags = 0);
};
};

#endif