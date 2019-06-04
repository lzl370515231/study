
/********************************************************************************************
*	Copyright(c)2014-05-07 …Ó€⁄∫£√¿µœ
* 		All rights reserved.
* 
*	File Name:	hmdSysPropSet.c
* 	Function:	hmdSysPropSet function for application to SystemProperties.set
*	Writer:		nation(cg)
*	version: 	2014-05-07
*				
*********************************************************************************************/


//#include <android/log.h>

#include "hmdSysPropSet.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

#include <cutils/properties.h>

//#define DebugPrintf(...)	__android_log_print(ANDROID_LOG_INFO, "kkemain", __VA_ARGS__);

int hmdSysPropSet(const char *key,const char *value)
{
	int ret;
	printf("\n hmd cg property_set key start \n");
	ret=property_set(key, value);
	if( ret != 0 )
	{
		printf("\n hmd cg property_set key = %s,value= %s failed \n",key,value);
		return -1;
	}
	printf("\n hmd cg property_set key = %s,value= %s OK \n",key,value);
	return 0;
}


/*
int main(void)
{
	int ret;
	ret = hmdSysPropSet("persist.sys.Hitvid","chegnuo123test");
	if( ret != 0 )
	{
		printf("\n hmd cg property_set failed \n");
		return -1;
	}
	printf("\n hmd cg property_set ok \n");
	return 0;
}
int main(int argc, char *argv[])
{
    if(argc == 3) {
		property_set(argv[1], argv[2]);
	  	printf("set himeida info prop %s values of %s success\n",argv[1],argv[2]);
		return 0;
	}
   return -1;
 } 
*/