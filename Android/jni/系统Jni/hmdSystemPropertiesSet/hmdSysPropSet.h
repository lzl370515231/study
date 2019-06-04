#ifndef _KKE_MOD_H_
#define _KKE_MOD_H_


/*
// set parameter
typedef enum tagKKE_SET_CMD
{
	KKE_SETDEUBG,	// 0�رմ�ӡ��Ĭ�ϣ���1������ӡ
	KKE_SETMICVOL,		// mic vol 0~100
	KKE_SETMICECHO		// mic echo 0~100
}KKE_SET_CMD;





// get parameter
typedef enum tagKKE_GET_CMD
{
	KKE_GETDEUBG,		// 0�رմ�ӡ��Ĭ�ϣ���1������ӡ
	KKE_GETMICVOL,		// mic vol 0~100
	KKE_GETMICECHO,		// mic echo 0~100
	KKE_GETSOVER		// get so version
}KKE_GET_CMD;



*/

/*********************************************************************
name :	kke_SetParameter
func :	set kke cmd
para :	cmd[in]		KKE_SET_CMD
		para[in]	�ο�KKE_SET_CMDע��
return :		0 	success
			-1	have not inited
			-2	not support cmd
*********************************************************************/
//extern int kke_SetParameter(KKE_SET_CMD cmd, int para);


/*********************************************************************
name :	kke_GetParameter
func :	get kke info
para :	cmd[in]		KKE_GET_CMD
		para[out]	�ο�KKE_GET_CMDע��
return :		0 	success
			-1	have not inited
			-2	not support cmd
*********************************************************************/
//extern int kke_GetParameter(KKE_GET_CMD cmd, int para);


/*********************************************************************
name :	kke_Init
func :	��ʼ��
para :	NULL
return :		0 	success
			-1	open device failed
			-2	open audio failed
*********************************************************************/
//extern int kke_Init(void);


/*********************************************************************
name :	kke_Deinit
func :	����ʼ��
para :	NULL
return :		0 	success
			-1	have not inited
*********************************************************************/
//extern int kke_Deinit(void);


#endif
