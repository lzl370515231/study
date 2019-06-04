
#define LOG_TAG "HMD"
#include <utils/Log.h>

#include <sys/wait.h>
#define LOGE printf

int isSystemSuccessful(pid_t status)
{
    if(-1 == status){
        ALOGE("system error!!");
        return -1;
    }else{
        if (WIFEXITED(status)){
            if (0 == WEXITSTATUS(status)){
                return 0;

            }else{
                ALOGE("run shell script fail, script exit code: %d",WEXITSTATUS(status));
                return -1;
            }
        }else{
            ALOGE("exit status = [%d]",WEXITSTATUS(status));
            return -1;
        }
    }

}

//执行系统命令
int do_system_cmd(const char* cmd)
{
    pid_t status ;
    if(cmd!= NULL){
        status = system(cmd);
        if (isSystemSuccessful(status) != 0) {
            printf("hmdflashInfo run system cmd error!");
            return -1;
        }
        return 1;
    }
    return -1;
}

/*
//生成可执行文件bin的main函数
int main( int argc, char *argv[])
{
	 char *hitvid;
	 int ret=-1;
	if(argc==1)
	{
		hitvid = (char *)malloc(sizeof(char)*33);
		memset(hitvid,0,sizeof(hitvid));
		get_deviceinfo_HiTvID(hitvid,"emmc");
		printf("please input hitvid=%s\n",hitvid);
		free(hitvid);
		return -1;
	}
	ret=set_deviceinfo_HiTvID(argv[1],"emmc");
	printf("ret=%d \n",ret);
	return ret;
}*/
