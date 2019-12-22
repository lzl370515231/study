/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.bluetooth.a2dp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.IBluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.os.ParcelUuid;
import android.provider.Settings;
import android.util.Log;
import com.android.bluetooth.avrcp.Avrcp;
import com.android.bluetooth.btservice.ProfileService;
import com.android.bluetooth.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Provides Bluetooth A2DP profile, as a service in the Bluetooth application.
 * @hide
 */
public class A2dpService extends ProfileService {
    private static final boolean DBG = true;
    private static final String TAG="LZLB_A2dpService";

    private A2dpStateMachine mStateMachine;
    private Avrcp mAvrcp;
    private static A2dpService sAd2dpService;
    static final ParcelUuid[] A2DP_SOURCE_UUID = {
        BluetoothUuid.AudioSource
    };
    static final ParcelUuid[] A2DP_SOURCE_SINK_UUIDS = {
        BluetoothUuid.AudioSource,
        BluetoothUuid.AudioSink
    };

    protected String getName() {
		Log.e(TAG,"getName");
        return TAG;
    }

    protected IProfileServiceBinder initBinder() {
    Log.e(TAG,"initBinder");
        return new BluetoothA2dpBinder(this);
    }

    protected boolean start() {
    Log.e(TAG,"start");
        mAvrcp = Avrcp.make(this);
        mStateMachine = A2dpStateMachine.make(this, this);
        setA2dpService(this);
        return true;
    }

    protected boolean stop() {
    Log.e(TAG,"stop");
        if (mStateMachine != null) {
            mStateMachine.doQuit();
        }
        if (mAvrcp != null) {
            mAvrcp.doQuit();
        }
        return true;
    }

    protected boolean cleanup() {
    Log.e(TAG,"cleanup");
        if (mStateMachine!= null) {
            mStateMachine.cleanup();
        }
        if (mAvrcp != null) {
            mAvrcp.cleanup();
            mAvrcp = null;
        }
        clearA2dpService();
        return true;
    }

    //API Methods

    public static synchronized A2dpService getA2dpService(){
    Log.e(TAG,"getA2dpService");
        if (sAd2dpService != null && sAd2dpService.isAvailable()) {
            if (DBG) Log.d(TAG, "getA2DPService(): returning " + sAd2dpService);
            return sAd2dpService;
        }
        if (DBG)  {
            if (sAd2dpService == null) {
                Log.d(TAG, "getA2dpService(): service is NULL");
            } else if (!(sAd2dpService.isAvailable())) {
                Log.d(TAG,"getA2dpService(): service is not available");
            }
        }
        return null;
    }

    private static synchronized void setA2dpService(A2dpService instance) {
    Log.e(TAG,"setA2dpService");
        if (instance != null && instance.isAvailable()) {
            if (DBG) Log.d(TAG, "setA2dpService(): set to: " + sAd2dpService);
            sAd2dpService = instance;
        } else {
            if (DBG)  {
                if (sAd2dpService == null) {
                    Log.d(TAG, "setA2dpService(): service not available");
                } else if (!sAd2dpService.isAvailable()) {
                    Log.d(TAG,"setA2dpService(): service is cleaning up");
                }
            }
        }
    }

    private static synchronized void clearA2dpService() {
     Log.e(TAG,"clearA2dpService");
        sAd2dpService = null;
    }

    public boolean connect(BluetoothDevice device) {
    Log.e(TAG,"connect");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");

        if (getPriority(device) == BluetoothProfile.PRIORITY_OFF) {
            return false;
        }
        ParcelUuid[] featureUuids = device.getUuids();
        if ((BluetoothUuid.containsAnyUuid(featureUuids, A2DP_SOURCE_UUID)) &&
            !(BluetoothUuid.containsAllUuids(featureUuids ,A2DP_SOURCE_SINK_UUIDS))) {
            Log.e(TAG,"Remote does not have A2dp Sink UUID");
            return false;
        }

        int connectionState = mStateMachine.getConnectionState(device);
        if (connectionState == BluetoothProfile.STATE_CONNECTED ||
            connectionState == BluetoothProfile.STATE_CONNECTING) {
            return false;
        }

        mStateMachine.sendMessage(A2dpStateMachine.CONNECT, device);
        return true;
    }

    boolean disconnect(BluetoothDevice device) {
    Log.e(TAG,"disconnect");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");
        int connectionState = mStateMachine.getConnectionState(device);
        if (connectionState != BluetoothProfile.STATE_CONNECTED &&
            connectionState != BluetoothProfile.STATE_CONNECTING) {
            return false;
        }

        mStateMachine.sendMessage(A2dpStateMachine.DISCONNECT, device);
        return true;
    }

    public List<BluetoothDevice> getConnectedDevices() {
    Log.e(TAG,"getConnectedDevices");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mStateMachine.getConnectedDevices();
    }

    List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
    Log.e(TAG,"getDevicesMatchingConnectionStates");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mStateMachine.getDevicesMatchingConnectionStates(states);
    }

    int getConnectionState(BluetoothDevice device) {
    Log.e(TAG,"getConnectionState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mStateMachine.getConnectionState(device);
    }

    public boolean setPriority(BluetoothDevice device, int priority) {
    Log.e(TAG,"setPriority");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH_ADMIN permission");
        Settings.Global.putInt(getContentResolver(),
            Settings.Global.getBluetoothA2dpSinkPriorityKey(device.getAddress()),
            priority);
        if (DBG) Log.d(TAG,"Saved priority " + device + " = " + priority);
        return true;
    }

    public int getPriority(BluetoothDevice device) {
    Log.e(TAG,"getPriority");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH_ADMIN permission");
        int priority = Settings.Global.getInt(getContentResolver(),
            Settings.Global.getBluetoothA2dpSinkPriorityKey(device.getAddress()),
            BluetoothProfile.PRIORITY_UNDEFINED);
        return priority;
    }

    /* Absolute volume implementation */
    public boolean isAvrcpAbsoluteVolumeSupported() {
    Log.e(TAG,"isAvrcpAbsoluteVolumeSupported");
        return mAvrcp.isAbsoluteVolumeSupported();
    }

    public void adjustAvrcpAbsoluteVolume(int direction) {
    Log.e(TAG,"adjustAvrcpAbsoluteVolume");
        mAvrcp.adjustVolume(direction);
    }

    public void setAvrcpAbsoluteVolume(int volume) {
    Log.e(TAG,"setAvrcpAbsoluteVolume");
        mAvrcp.setAbsoluteVolume(volume);
    }

    public void setAvrcpAudioState(int state) {
    Log.e(TAG,"setAvrcpAudioState");
        mAvrcp.setA2dpAudioState(state);
    }

    public void resetAvrcpBlacklist(BluetoothDevice device) {
    Log.e(TAG,"resetAvrcpBlacklist");
        if (mAvrcp != null) {
            mAvrcp.resetBlackList(device.getAddress());
        }
    }

    synchronized boolean isA2dpPlaying(BluetoothDevice device) {
    Log.e(TAG,"isA2dpPlaying");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM,
                                       "Need BLUETOOTH permission");
        if (DBG) Log.d(TAG, "isA2dpPlaying(" + device + ")");
        return mStateMachine.isPlaying(device);
    }

    //Binder object: Must be static class or memory leak may occur 
    private static class BluetoothA2dpBinder extends IBluetoothA2dp.Stub 
        implements IProfileServiceBinder {

		private static final String TAG ="LZLB_BluetoothA2dpBinder";
        private A2dpService mService;

        private A2dpService getService() {
			Log.e(TAG,"getService");
            if (!Utils.checkCaller()) {
                Log.w(TAG,"A2dp call not allowed for non-active user");
                return null;
            }

            if (mService != null && mService.isAvailable()) {
                return mService;
            }
            return null;
        }

        BluetoothA2dpBinder(A2dpService svc) {
        Log.e(TAG,"BluetoothA2dpBinder");
            mService = svc;
        }

        public boolean cleanup()  {
        Log.e(TAG,"cleanup");
            mService = null;
            return true;
        }

        public boolean connect(BluetoothDevice device) {
        Log.e(TAG,"connect");
            A2dpService service = getService();
            if (service == null) return false;
            return service.connect(device);
        }

        public boolean disconnect(BluetoothDevice device) {
        Log.e(TAG,"disconnect");
            A2dpService service = getService();
            if (service == null) return false;
            return service.disconnect(device);
        }

        public List<BluetoothDevice> getConnectedDevices() {
        Log.e(TAG,"getConnectedDevices");
            A2dpService service = getService();
            if (service == null) return new ArrayList<BluetoothDevice>(0);
            return service.getConnectedDevices();
        }

        public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        Log.e(TAG,"getDevicesMatchingConnectionStates");
            A2dpService service = getService();
            if (service == null) return new ArrayList<BluetoothDevice>(0);
            return service.getDevicesMatchingConnectionStates(states);
        }

        public int getConnectionState(BluetoothDevice device) {
        Log.e(TAG,"getConnectionState");
            A2dpService service = getService();
            if (service == null) return BluetoothProfile.STATE_DISCONNECTED;
            return service.getConnectionState(device);
        }

        public boolean setPriority(BluetoothDevice device, int priority) {
        Log.e(TAG,"setPriority");
            A2dpService service = getService();
            if (service == null) return false;
            return service.setPriority(device, priority);
        }

        public int getPriority(BluetoothDevice device) {
        Log.e(TAG,"getPriority");
            A2dpService service = getService();
            if (service == null) return BluetoothProfile.PRIORITY_UNDEFINED;
            return service.getPriority(device);
        }

        public boolean isAvrcpAbsoluteVolumeSupported() {
        Log.e(TAG,"isAvrcpAbsoluteVolumeSupported");
            A2dpService service = getService();
            if (service == null) return false;
            return service.isAvrcpAbsoluteVolumeSupported();
        }

        public void adjustAvrcpAbsoluteVolume(int direction) {
        Log.e(TAG,"adjustAvrcpAbsoluteVolume");
            A2dpService service = getService();
            if (service == null) return;
            service.adjustAvrcpAbsoluteVolume(direction);
        }

        public void setAvrcpAbsoluteVolume(int volume) {
        Log.e(TAG,"setAvrcpAbsoluteVolume");
            A2dpService service = getService();
            if (service == null) return;
            service.setAvrcpAbsoluteVolume(volume);
        }

        public boolean isA2dpPlaying(BluetoothDevice device) {
        Log.e(TAG,"isA2dpPlaying");
            A2dpService service = getService();
            if (service == null) return false;
            return service.isA2dpPlaying(device);
        }
    };

    @Override
    public void dump(StringBuilder sb) {
    Log.e(TAG,"dump");
        super.dump(sb);
        if (mStateMachine != null) {
            mStateMachine.dump(sb);
        }
        if (mAvrcp != null) {
            mAvrcp.dump(sb);
        }
    }
}
