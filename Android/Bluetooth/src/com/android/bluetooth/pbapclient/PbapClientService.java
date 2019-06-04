/*
 * Copyright (c) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.bluetooth.pbapclient;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothPbapClient;
import android.bluetooth.IBluetoothHeadsetClient;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.OperationApplicationException;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.provider.ContactsContract;

import com.android.bluetooth.btservice.ProfileService;
import com.android.bluetooth.Utils;
import com.android.vcard.VCardEntry;


import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

/**
 * Provides Bluetooth Phone Book Access Profile Client profile.
 *
 * @hide
 */
public class PbapClientService extends ProfileService {
    private static final boolean DBG = true;
    private static final String TAG = "LZLB_PbapClientService";
    private PbapPCEClient mClient;
    private HandlerThread mHandlerThread;
    private AccountManager mAccountManager;
    private static PbapClientService sPbapClientService;
    private PbapBroadcastReceiver mPbapBroadcastReceiver = new PbapBroadcastReceiver();

    @Override
    protected String getName() {
        return TAG;
    }

    @Override
    public IProfileServiceBinder initBinder() {
        return new BluetoothPbapClientBinder(this);
    }

    @Override
    protected boolean start() {
    	Log.e(TAG,"start");
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        try {
            registerReceiver(mPbapBroadcastReceiver, filter);
        } catch (Exception e) {
            Log.w(TAG,"Unable to register pbapclient receiver",e);
        }
        mClient = new PbapPCEClient(this);
        mAccountManager = AccountManager.get(this);
        setPbapClientService(this);
        mClient.start();
        return true;
    }

    @Override
    protected boolean stop() {
    	Log.e(TAG,"stop");
        try {
            unregisterReceiver(mPbapBroadcastReceiver);
        } catch (Exception e) {
            Log.w(TAG,"Unable to unregister sap receiver",e);
        }
        mClient.disconnect(null);
        return true;
    }

    @Override
    protected boolean cleanup() {
    	Log.e(TAG,"cleanup");
        clearPbapClientService();
        return true;
    }

    private class PbapBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "onReceive");
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
                  BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                  if(getPriority(device) >= BluetoothProfile.PRIORITY_ON) {
                      connect(device);
                  }
            } else if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                disconnect(device);
            }
        }
    }

    /**
     * Handler for incoming service calls
     */
    private static class BluetoothPbapClientBinder extends IBluetoothPbapClient.Stub
            implements IProfileServiceBinder {
        private PbapClientService mService;
		private static final String TAG ="LZLB_BluetoothPbapClientBinder";

        public BluetoothPbapClientBinder(PbapClientService svc) {
            mService = svc;
        }

        @Override
        public boolean cleanup() {
            mService = null;
            return true;
        }

        private PbapClientService getService() {
        	Log.e(TAG,"getService");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "PbapClient call not allowed for non-active user");
                return null;
            }

            if (mService != null && mService.isAvailable()) {
                return mService;
            }
            return null;
        }

        @Override
        public boolean connect(BluetoothDevice device) {
        	Log.e(TAG,"connect");
            PbapClientService service = getService();
            if (service == null) {
                return false;
            }
            return service.connect(device);
        }

        @Override
        public boolean disconnect(BluetoothDevice device) {
        	Log.e(TAG,"disconnect");
            PbapClientService service = getService();
            if (service == null) {
                return false;
            }
            return service.disconnect(device);
        }

        @Override
        public List<BluetoothDevice> getConnectedDevices() {
        	Log.e(TAG,"getConnectedDevices");
            PbapClientService service = getService();
            if (service == null) {
                return new ArrayList<BluetoothDevice>(0);
            }
            return service.getConnectedDevices();
        }
        @Override
        public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        	Log.e(TAG,"getDevicesMatchingConnectionStates");
            PbapClientService service = getService();
            if (service == null) {
                return new ArrayList<BluetoothDevice>(0);
            }
            return service.getDevicesMatchingConnectionStates(states);
        }

        @Override
        public int getConnectionState(BluetoothDevice device) {
        	Log.e(TAG,"getConnectionState");
            PbapClientService service = getService();
            if (service == null) {
                return BluetoothProfile.STATE_DISCONNECTED;
            }
            return service.getConnectionState(device);
        }

        public boolean setPriority(BluetoothDevice device, int priority) {
        	Log.e(TAG,"setPriority");
            PbapClientService service = getService();
            if (service == null) {
                return false;
            }
            return service.setPriority(device, priority);
        }

        public int getPriority(BluetoothDevice device) {
        	Log.e(TAG,"getPriority");
            PbapClientService service = getService();
            if (service == null) {
                return BluetoothProfile.PRIORITY_UNDEFINED;
            }
            return service.getPriority(device);
        }


    }


    // API methods
    public static synchronized PbapClientService getPbapClientService() {
    	Log.e(TAG,"getPbapClientService");
        if (sPbapClientService != null && sPbapClientService.isAvailable()) {
            if (DBG) {
                Log.d(TAG, "getPbapClientService(): returning " + sPbapClientService);
            }
            return sPbapClientService;
        }
        if (DBG) {
            if (sPbapClientService == null) {
                Log.d(TAG, "getPbapClientService(): service is NULL");
            } else if (!(sPbapClientService.isAvailable())) {
                Log.d(TAG, "getPbapClientService(): service is not available");
            }
        }
        return null;
    }

    private static synchronized void setPbapClientService(PbapClientService instance) {
    	Log.e(TAG,"setPbapClientService");
        if (instance != null && instance.isAvailable()) {
            if (DBG) {
                Log.d(TAG, "setPbapClientService(): set to: " + sPbapClientService);
            }
            sPbapClientService = instance;
        } else {
            if (DBG) {
                if (sPbapClientService == null) {
                    Log.d(TAG, "setPbapClientService(): service not available");
                } else if (!sPbapClientService.isAvailable()) {
                    Log.d(TAG, "setPbapClientService(): service is cleaning up");
                }
            }
        }
    }

    private static synchronized void clearPbapClientService() {
        sPbapClientService = null;
    }

    public boolean connect(BluetoothDevice device) {
    	Log.e(TAG,"connect");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                "Need BLUETOOTH ADMIN permission");
        Log.d(TAG,"Received request to ConnectPBAPPhonebook " + device.getAddress());
        int connectionState = mClient.getConnectionState();
        if (connectionState == BluetoothProfile.STATE_CONNECTED ||
                connectionState == BluetoothProfile.STATE_CONNECTING) {
            return false;
        }
        if (getPriority(device)>BluetoothProfile.PRIORITY_OFF) {
            mClient.connect(device);
            return true;
        }
        return false;
    }

    boolean disconnect(BluetoothDevice device) {
    	Log.e(TAG,"disconnect");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                "Need BLUETOOTH ADMIN permission");
        mClient.disconnect(device);
        return true;
    }
    public List<BluetoothDevice> getConnectedDevices() {
    	Log.e(TAG,"getConnectedDevices");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int[] desiredStates = {BluetoothProfile.STATE_CONNECTED};
        return getDevicesMatchingConnectionStates(desiredStates);
    }

    private List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
    	Log.e(TAG,"getDevicesMatchingConnectionStates");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int clientState = mClient.getConnectionState();
        Log.d(TAG,"getDevicesMatchingConnectionStates " + Arrays.toString(states) + " == " + clientState);
        List<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();
        for (int state : states) {
            if (clientState == state) {
                BluetoothDevice currentDevice = mClient.getDevice();
                if (currentDevice != null) {
                    deviceList.add(currentDevice);
                }
            }
        }
        return deviceList;
    }

    int getConnectionState(BluetoothDevice device) {
    	Log.e(TAG,"getConnectionState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (device == mClient.getDevice()) {
            return mClient.getConnectionState();
        }
        return BluetoothProfile.STATE_DISCONNECTED;
    }

    public boolean setPriority(BluetoothDevice device, int priority) {
    	Log.e(TAG,"setPriority");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                "Need BLUETOOTH_ADMIN permission");
        Settings.Global.putInt(getContentResolver(),
                Settings.Global.getBluetoothPbapClientPriorityKey(device.getAddress()),
                priority);
        if (DBG) {
            Log.d(TAG,"Saved priority " + device + " = " + priority);
        }
        return true;
    }

    public int getPriority(BluetoothDevice device) {
    	Log.e(TAG,"getPriority");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                "Need BLUETOOTH_ADMIN permission");
        int priority = Settings.Global.getInt(getContentResolver(),
                Settings.Global.getBluetoothPbapClientPriorityKey(device.getAddress()),
                BluetoothProfile.PRIORITY_UNDEFINED);
        return priority;
    }

}
