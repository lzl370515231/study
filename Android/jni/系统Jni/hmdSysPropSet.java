/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 *add bby cg 2014-1-04
 */

package android.os;

import java.io.IOException;
import android.os.ServiceManager;
import java.lang.Integer;
import java.lang.String;
import android.util.Log;


public class hmdSysPropSet
{
	public static final String TAG = "hmdSysPropSet";
	
    // can't instantiate this class
    public hmdSysPropSet()
    {
    }

	public static native int hmdSysPropSetnative(String key, String value);

}

