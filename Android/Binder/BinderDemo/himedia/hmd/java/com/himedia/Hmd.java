
package com.himedia;

import java.lang.String;
import android.util.Log;

/**
 * Class that provides access to some of the hmd management functions.
 *
 * {@hide}
 */
public class Hmd
{
	public static final String TAG = "HMD";

    private Hmd()
    {
    }

	static {
		Log.e(TAG,"hmd_init");
		System.loadLibrary("hmd_jni");
		nativeInit();
	}
	
	private static native void nativeInit();
	private static native int nativeDoSystemCmd();
  
	public static int DoSystemCmd(){
		Log.e(TAG,"DOSystemCmd");
		return nativeDoSystemCmd();
	}
}

