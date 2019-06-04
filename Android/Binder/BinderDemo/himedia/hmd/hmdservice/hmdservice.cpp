
/*
#include <sys/types.h>
#include <unistd.h>
#include <grp.h>
#include <utils/Log.h>
#include <private/android_filesystem_config.h>
*/

#include <binder/IPCThreadState.h>
#include <binder/ProcessState.h>
#include <binder/IServiceManager.h>
#include "HmdService.h"
using namespace android;

int main(int argc, char** argv){
	sp<ProcessState> proc(ProcessState::self());
	sp<IServiceManager> sm = defaultServiceManager();
	HmdService::instantiate();
	ProcessState::self()->startThreadPool();
	IPCThreadState::self()->joinThreadPool();

}