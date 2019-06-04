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
		hmdSysPropSet.hmdSysPropSetnative("persist.sys.Hitvid","hhhhhhhh123456");  
	}

 	public void shutdown_API()
 	{		  
		String str1 = "system /system/bin/shutdown";//
		SocketClient socket = new SocketClient();
		socket.writeMess(str1);
 	}
}
