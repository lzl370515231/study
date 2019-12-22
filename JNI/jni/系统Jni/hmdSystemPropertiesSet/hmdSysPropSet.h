#ifndef _KKE_MOD_H_
#define _KKE_MOD_H_


/*
// set parameter
typedef enum tagKKE_SET_CMD
{
	KKE_SETDEUBG,	// 0关闭打印（默认）；1开启打印
	KKE_SETMICVOL,		// mic vol 0~100
	KKE_SETMICECHO		// mic echo 0~100
}KKE_SET_CMD;





// get parameter
typedef enum tagKKE_GET_CMD
{
	KKE_GETDEUBG,		// 0关闭打印（默认）；1开启打印
	KKE_GETMICVOL,		// mic vol 0~100
	KKE_GETMICECHO,		// mic echo 0~100
	KKE_GETSOVER		// get so version
}KKE_GET_CMD;



*/

/*********************************************************************
name :	kke_SetParameter
func :	set kke cmd
para :	cmd[in]		KKE_SET_CMD
		para[in]	参考KKE_SET_CMD注释
return :		0 	success
			-1	have not inited
			-2	not support cmd
*********************************************************************/
//extern int kke_SetParameter(KKE_SET_CMD cmd, int para);


/*********************************************************************
name :	kke_GetParameter
func :	get kke info
para :	cmd[in]		KKE_GET_CMD
		para[out]	参考KKE_GET_CMD注释
return :		0 	success
			-1	have not inited
			-2	not support cmd
*********************************************************************/
//extern int kke_GetParameter(KKE_GET_CMD cmd, int para);


/*********************************************************************
name :	kke_Init
func :	初始化
para :	NULL
return :		0 	success
			-1	open device failed
			-2	open audio failed
*********************************************************************/
//extern int kke_Init(void);


/*********************************************************************
name :	kke_Deinit
func :	反初始化
para :	NULL
return :		0 	success
			-1	have not inited
*********************************************************************/
//extern int kke_Deinit(void);


#endif
