package com.cg.deviceinfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.os.Build;
import com.himedia.Hmd; 
public class DeviceinfoActivity extends Activity {
    /** Called when the activity is first created. */
	private static final String TAG ="LZL";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
		Log.e(TAG,"DeviceinfoActivity start");   
		Hmd.DoSystemCmd();
    }
}
