package com.himedia.shutdown;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.os.hmdSysPropSet;
import android.util.Log;
public class ShutdownActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
/*        Log.v("add by cg test","\n cg  Build.getHMDHM1= "+ Build.getHM1() + " Build.getHMDHM2= "+Build.getHM2()+ " Build.getHMDHM3= "+ Build.getHM3()+ " Build.getHMDHM4= "+Build.getHM4() + " Build.getHMDHM5= "+Build.getHM5()+" Build.getHMDSn= "+Build.getSn()+"\n");
    
    Log.v("add by cg test","\n cg  Build.getHMDMac()= "+Build.getMac() + " Build.getHMDVersionNumber= "+Build.getVersionNumber()+ " Build.getHMDOSType= "+Build.getOSType()+ " Build.getHMDChipsetType= "+Build.getChipsetType() +"\n"); 
    Log.v("add by cg test","\n cg  Build.getHMDPid()= "+Build.getPid() + " Build.getHMDUUid= "+Build.getHitvid()+ " Build.getHMDManufacturer= "+Build.getManufacturer()+ " Build.getHMDBrand= "+Build.getBrand() + " Build.getHMDFirmwareModel= "+Build.getFirmwareModel()+" getALLVersionNumber="+Build.getALLVersionNumber()+"\n");
    Build.setHitvid("hmd123456789");
    Build.setWhiteFlag("yes","time");
    Log.v("add by cg test","\n cg  Build.getHitvid()= "+Build.getHitvid() + " Build.getAllWhiteflag= "+Build.getAllWhiteflag()+ " Build.getWhiteflag= "+Build.getWhiteflag()+"\n"); 
*/
 //Build.setHitvid("hmd123456789");
 hmdSysPropSet.hmdSysPropSetnative("persist.sys.Hitvid","hhhhhhhh123456");  
  }
    
    
    
 	public void shutdown_API()
 	{		  
       String str1 = "system /system/bin/shutdown";//

       SocketClient socket = new SocketClient();
       socket.writeMess(str1);
 	}
/*private void setBuildMethod(String function,String value1,String value2)throws Exception{
		int ret = -1;
		Class SystemProperties = Class.forName("android.os.Build");
		Constructor constructor = SystemProperties.getConstructor();
		Object buildMethod = constructor.newInstance();
    	Method method = SystemProperties.getMethod(function,new Class[] { String.class, String.class });
    	method.invoke(buildMethod,new Object[]{value1,value2});
	}

	
	private void setBuildMethod_extern(String function,String value1)throws Exception{
		int ret = -1;
		Class SystemProperties = Class.forName("android.os.Build");
		Constructor constructor = SystemProperties.getConstructor();
		Object buildMethod = constructor.newInstance();
    	Method method = SystemProperties.getMethod(function,new Class[] { String.class});
    	method.invoke(buildMethod,new Object[]{value1});
	}*/
}
