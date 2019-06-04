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

package com.android.bluetooth.hfp;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.IBluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import com.android.bluetooth.btservice.ProfileService;
import com.android.bluetooth.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides Bluetooth Headset and Handsfree profile, as a service in
 * the Bluetooth application.
 * @hide
 */
public class HeadsetService extends ProfileService {
    private static final boolean DBG = true;
    private static final String TAG = "LZLB_HeadsetService";
    private static final String MODIFY_PHONE_STATE = android.Manifest.permission.MODIFY_PHONE_STATE;

    private HeadsetStateMachine mStateMachine;
    private static HeadsetService sHeadsetService;

    protected String getName() {
        return TAG;
    }

    public IProfileServiceBinder initBinder() {
        return new BluetoothHeadsetBinder(this);
    }

    protected boolean start() {
    	Log.e(TAG,"start");
        mStateMachine = HeadsetStateMachine.make(this);
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        filter.addAction(AudioManager.VOLUME_CHANGED_ACTION);
        filter.addAction(BluetoothDevice.ACTION_CONNECTION_ACCESS_REPLY);
        try {
            registerReceiver(mHeadsetReceiver, filter);
        } catch (Exception e) {
            Log.w(TAG,"Unable to register headset receiver",e);
        }
        setHeadsetService(this);
        return true;
    }

    protected boolean stop() {
    	Log.e(TAG,"stop");
        try {
            unregisterReceiver(mHeadsetReceiver);
        } catch (Exception e) {
            Log.w(TAG,"Unable to unregister headset receiver",e);
        }
        if (mStateMachine != null) {
            mStateMachine.doQuit();
        }
        return true;
    }

    protected boolean cleanup() {
    	Log.e(TAG,"cleanup");
        if (mStateMachine != null) {
            mStateMachine.cleanup();
        }
        clearHeadsetService();
        return true;
    }

    private final BroadcastReceiver mHeadsetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
			Log.e("HeadSetService","onReceive action:"+action);
            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                mStateMachine.sendMessage(HeadsetStateMachine.INTENT_BATTERY_CHANGED, intent);
            } else if (action.equals(AudioManager.VOLUME_CHANGED_ACTION)) {
                int streamType = intent.getIntExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, -1);
                if (streamType == AudioManager.STREAM_BLUETOOTH_SCO) {
                    mStateMachine.sendMessage(HeadsetStateMachine.INTENT_SCO_VOLUME_CHANGED,
                                              intent);
                }
            }
            else if (action.equals(BluetoothDevice.ACTION_CONNECTION_ACCESS_REPLY)) {
                int requestType = intent.getIntExtra(BluetoothDevice.EXTRA_ACCESS_REQUEST_TYPE,
                                               BluetoothDevice.REQUEST_TYPE_PHONEBOOK_ACCESS);
                if (requestType == BluetoothDevice.REQUEST_TYPE_PHONEBOOK_ACCESS) {
                    Log.v(TAG, "Received BluetoothDevice.ACTION_CONNECTION_ACCESS_REPLY");
                    mStateMachine.handleAccessPermissionResult(intent);
                }
            }
        }
    };

    /**
     * Handlers for incoming service calls
     */
    private static class BluetoothHeadsetBinder extends IBluetoothHeadset.Stub implements IProfileServiceBinder {
        private HeadsetService mService;
		private static final String TAG="LZLB_BluetoothHeadsetBinder";

        public BluetoothHeadsetBinder(HeadsetService svc) {
            mService = svc;
        }
        public boolean cleanup() {
            mService = null;
            return true;
        }

        private HeadsetService getService() {
        	Log.e(TAG,"getService");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG,"Headset call not allowed for non-active user");
                return null;
            }

            if (mService  != null && mService.isAvailable()) {
                return mService;
            }
            return null;
        }

        public boolean connect(BluetoothDevice device) {
        	Log.e(TAG,"connect");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.connect(device);
        }

        public boolean disconnect(BluetoothDevice device) {
        	Log.e(TAG,"disconnect");
            HeadsetService service = getService();
            if (service == null) return false;
            if (DBG) Log.d(TAG, "disconnect in HeadsetService");
            return service.disconnect(device);
        }

        public List<BluetoothDevice> getConnectedDevices() {
        	Log.e(TAG,"getConnectedDevices");
            HeadsetService service = getService();
            if (service == null) return new ArrayList<BluetoothDevice>(0);
            return service.getConnectedDevices();
        }

        public List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
        	Log.e(TAG,"getDevicesMatchingConnectionStates");
            HeadsetService service = getService();
            if (service == null) return new ArrayList<BluetoothDevice>(0);
            return service.getDevicesMatchingConnectionStates(states);
        }

        public int getConnectionState(BluetoothDevice device) {
        	Log.e(TAG,"getConnectionState");
            HeadsetService service = getService();
            if (service == null) return BluetoothProfile.STATE_DISCONNECTED;
            return service.getConnectionState(device);
        }

        public boolean setPriority(BluetoothDevice device, int priority) {
        	Log.e(TAG,"setPriority");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.setPriority(device, priority);
        }

        public int getPriority(BluetoothDevice device) {
        	Log.e(TAG,"getPriority");
            HeadsetService service = getService();
            if (service == null) return BluetoothProfile.PRIORITY_UNDEFINED;
            return service.getPriority(device);
        }

        public boolean startVoiceRecognition(BluetoothDevice device) {
        	Log.e(TAG,"startVoiceRecognition");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.startVoiceRecognition(device);
        }

        public boolean stopVoiceRecognition(BluetoothDevice device) {
        Log.e(TAG,"stopVoiceRecognition");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.stopVoiceRecognition(device);
        }

        public boolean isAudioOn() {
        	Log.e(TAG,"isAudioOn");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.isAudioOn();
        }

        public boolean isAudioConnected(BluetoothDevice device) {
        	Log.e(TAG,"isAudioConnected");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.isAudioConnected(device);
        }

        public int getBatteryUsageHint(BluetoothDevice device) {
        	Log.e(TAG,"getBatteryUsageHint");
            HeadsetService service = getService();
            if (service == null) return 0;
            return service.getBatteryUsageHint(device);
        }

        public boolean acceptIncomingConnect(BluetoothDevice device) {
        Log.e(TAG,"acceptIncomingConnect");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.acceptIncomingConnect(device);
        }

        public boolean rejectIncomingConnect(BluetoothDevice device) {
        Log.e(TAG,"rejectIncomingConnect");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.rejectIncomingConnect(device);
        }

        public int getAudioState(BluetoothDevice device) {
        	Log.e(TAG,"getAudioState");
            HeadsetService service = getService();
            if (service == null) return BluetoothHeadset.STATE_AUDIO_DISCONNECTED;
            return service.getAudioState(device);
        }

        public boolean connectAudio() {
        	Log.e(TAG,"connectAudio");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.connectAudio();
        }

        public boolean disconnectAudio() {
        	Log.e(TAG,"disconnectAudio");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.disconnectAudio();
        }

        public void setAudioRouteAllowed(boolean allowed) {
        	Log.e(TAG,"setAudioRouteAllowed");
            HeadsetService service = getService();
            if (service == null) return;
            service.setAudioRouteAllowed(allowed);
        }

        public boolean getAudioRouteAllowed() {
        	Log.e(TAG,"getAudioRouteAllowed");
            HeadsetService service = getService();
            if (service != null) {
                return service.getAudioRouteAllowed();
            }

            return false;
        }

        public boolean startScoUsingVirtualVoiceCall(BluetoothDevice device) {
        	Log.e(TAG,"startScoUsingVirtualVoiceCall");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.startScoUsingVirtualVoiceCall(device);
        }

        public boolean stopScoUsingVirtualVoiceCall(BluetoothDevice device) {
        	Log.e(TAG,"stopScoUsingVirtualVoiceCall");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.stopScoUsingVirtualVoiceCall(device);
        }

        public void phoneStateChanged(int numActive, int numHeld, int callState,
                                      String number, int type) {
                                      	Log.e(TAG,"phoneStateChanged");
            HeadsetService service = getService();
            if (service == null) return;
            service.phoneStateChanged(numActive, numHeld, callState, number, type);
        }

        public void clccResponse(int index, int direction, int status, int mode, boolean mpty,
                                 String number, int type) {
                                 	Log.e(TAG,"clccResponse");
            HeadsetService service = getService();
            if (service == null) return;
            service.clccResponse(index, direction, status, mode, mpty, number, type);
        }

        public boolean sendVendorSpecificResultCode(BluetoothDevice device,
                                                    String command,
                                                    String arg) {
                                                    Log.e(TAG,"sendVendorSpecificResultCode");
            HeadsetService service = getService();
            if (service == null) {
                return false;
            }
            return service.sendVendorSpecificResultCode(device, command, arg);
        }

        public boolean enableWBS() {
        	Log.e(TAG,"enableWBS");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.enableWBS();
        }

        public boolean disableWBS() {
        	Log.e(TAG,"disableWBS");
            HeadsetService service = getService();
            if (service == null) return false;
            return service.disableWBS();
        }
    };

    //API methods
    public static synchronized HeadsetService getHeadsetService(){
    	Log.e(TAG,"getHeadsetService");
        if (sHeadsetService != null && sHeadsetService.isAvailable()) {
            if (DBG) Log.d(TAG, "getHeadsetService(): returning " + sHeadsetService);
            return sHeadsetService;
        }
        if (DBG)  {
            if (sHeadsetService == null) {
                Log.d(TAG, "getHeadsetService(): service is NULL");
            } else if (!(sHeadsetService.isAvailable())) {
                Log.d(TAG,"getHeadsetService(): service is not available");
            }
        }
        return null;
    }

    private static synchronized void setHeadsetService(HeadsetService instance) {
    	Log.e(TAG,"setHeadsetService");
        if (instance != null && instance.isAvailable()) {
            if (DBG) Log.d(TAG, "setHeadsetService(): set to: " + sHeadsetService);
            sHeadsetService = instance;
        } else {
            if (DBG)  {
                if (sHeadsetService == null) {
                    Log.d(TAG, "setHeadsetService(): service not available");
                } else if (!sHeadsetService.isAvailable()) {
                    Log.d(TAG,"setHeadsetService(): service is cleaning up");
                }
            }
        }
    }

    private static synchronized void clearHeadsetService() {
    	Log.e(TAG,"clearHeadsetService");
        sHeadsetService = null;
    }

    public boolean connect(BluetoothDevice device) {
    	Log.e(TAG,"connect");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");

        if (getPriority(device) == BluetoothProfile.PRIORITY_OFF) {
            return false;
        }

        int connectionState = mStateMachine.getConnectionState(device);
        Log.d(TAG,"connectionState = " + connectionState);
        if (connectionState == BluetoothProfile.STATE_CONNECTED ||
            connectionState == BluetoothProfile.STATE_CONNECTING) {
            return false;
        }

        mStateMachine.sendMessage(HeadsetStateMachine.CONNECT, device);
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

        mStateMachine.sendMessage(HeadsetStateMachine.DISCONNECT, device);
        return true;
    }

    public List<BluetoothDevice> getConnectedDevices() {
    	Log.e(TAG,"getConnectedDevices");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mStateMachine.getConnectedDevices();
    }

    private List<BluetoothDevice> getDevicesMatchingConnectionStates(int[] states) {
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
            Settings.Global.getBluetoothHeadsetPriorityKey(device.getAddress()),
            priority);
        if (DBG) Log.d(TAG, "Saved priority " + device + " = " + priority);
        return true;
    }

    public int getPriority(BluetoothDevice device) {
    	Log.e(TAG,"getPriority");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH_ADMIN permission");
        int priority = Settings.Global.getInt(getContentResolver(),
            Settings.Global.getBluetoothHeadsetPriorityKey(device.getAddress()),
            BluetoothProfile.PRIORITY_UNDEFINED);
        return priority;
    }

    boolean startVoiceRecognition(BluetoothDevice device) {
    	Log.e(TAG,"startVoiceRecognition");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int connectionState = mStateMachine.getConnectionState(device);
        if (connectionState != BluetoothProfile.STATE_CONNECTED &&
            connectionState != BluetoothProfile.STATE_CONNECTING) {
            return false;
        }
        mStateMachine.sendMessage(HeadsetStateMachine.VOICE_RECOGNITION_START);
        return true;
    }

    boolean stopVoiceRecognition(BluetoothDevice device) {
    	Log.e(TAG,"stopVoiceRecognition");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        // It seem that we really need to check the AudioOn state.
        // But since we allow startVoiceRecognition in STATE_CONNECTED and
        // STATE_CONNECTING state, we do these 2 in this method
        int connectionState = mStateMachine.getConnectionState(device);
        if (connectionState != BluetoothProfile.STATE_CONNECTED &&
            connectionState != BluetoothProfile.STATE_CONNECTING) {
            return false;
        }
        mStateMachine.sendMessage(HeadsetStateMachine.VOICE_RECOGNITION_STOP);
        // TODO is this return correct when the voice recognition is not on?
        return true;
    }

    boolean isAudioOn() {
    	Log.e(TAG,"isAudioOn");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mStateMachine.isAudioOn();
    }

    boolean isAudioConnected(BluetoothDevice device) {
    	Log.e(TAG,"isAudioConnected");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mStateMachine.isAudioConnected(device);
    }

    int getBatteryUsageHint(BluetoothDevice device) {
    	Log.e(TAG,"getBatteryUsageHint");
        // TODO(BT) ask for BT stack support?
        return 0;
    }

    boolean acceptIncomingConnect(BluetoothDevice device) {
        // TODO(BT) remove it if stack does access control
        return false;
    }

    boolean rejectIncomingConnect(BluetoothDevice device) {
        // TODO(BT) remove it if stack does access control
        return false;
    }

    int getAudioState(BluetoothDevice device) {
        return mStateMachine.getAudioState(device);
    }

    public void setAudioRouteAllowed(boolean allowed) {
    	Log.e(TAG,"setAudioRouteAllowed");
        mStateMachine.setAudioRouteAllowed(allowed);
    }

    public boolean getAudioRouteAllowed() {
    	Log.e(TAG,"getAudioRouteAllowed");
        return mStateMachine.getAudioRouteAllowed();
    }

    boolean connectAudio() {
    	Log.e(TAG,"connectAudio");
        // TODO(BT) BLUETOOTH or BLUETOOTH_ADMIN permission
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (!mStateMachine.isConnected()) {
            return false;
        }
        if (mStateMachine.isAudioOn()) {
            return false;
        }
        mStateMachine.sendMessage(HeadsetStateMachine.CONNECT_AUDIO);
        return true;
    }

    boolean disconnectAudio() {
    	Log.e(TAG,"disconnectAudio");
        // TODO(BT) BLUETOOTH or BLUETOOTH_ADMIN permission
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (!mStateMachine.isAudioOn()) {
            return false;
        }
        mStateMachine.sendMessage(HeadsetStateMachine.DISCONNECT_AUDIO);
        return true;
    }

    boolean startScoUsingVirtualVoiceCall(BluetoothDevice device) {
        /* Do not ignore request if HSM state is still Disconnected or
           Pending, it will be processed when transitioned to Connected */
           Log.e(TAG,"startScoUsingVirtualVoiceCall");
        mStateMachine.sendMessage(HeadsetStateMachine.VIRTUAL_CALL_START, device);
        return true;
    }

    boolean stopScoUsingVirtualVoiceCall(BluetoothDevice device) {
    Log.e(TAG,"stopScoUsingVirtualVoiceCall");
        int connectionState = mStateMachine.getConnectionState(device);
        if (connectionState != BluetoothProfile.STATE_CONNECTED &&
            connectionState != BluetoothProfile.STATE_CONNECTING) {
            return false;
        }
        mStateMachine.sendMessage(HeadsetStateMachine.VIRTUAL_CALL_STOP, device);
        return true;
    }

    private void phoneStateChanged(int numActive, int numHeld, int callState,
                                  String number, int type) {
                                  Log.e(TAG,"phoneStateChanged");
        enforceCallingOrSelfPermission(MODIFY_PHONE_STATE, null);
        Message msg = mStateMachine.obtainMessage(HeadsetStateMachine.CALL_STATE_CHANGED);
        msg.obj = new HeadsetCallState(numActive, numHeld, callState, number, type);
        msg.arg1 = 0; // false
        mStateMachine.sendMessage(msg);
    }

    private void clccResponse(int index, int direction, int status, int mode, boolean mpty,
                             String number, int type) {
                             Log.e(TAG,"clccResponse");
        enforceCallingOrSelfPermission(MODIFY_PHONE_STATE, null);
        mStateMachine.sendMessage(HeadsetStateMachine.SEND_CCLC_RESPONSE,
            new HeadsetClccResponse(index, direction, status, mode, mpty, number, type));
    }

    private boolean sendVendorSpecificResultCode(BluetoothDevice device,
                                                 String command,
                                                 String arg) {
                                                 Log.e(TAG,"sendVendorSpecificResultCode");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int connectionState = mStateMachine.getConnectionState(device);
        if (connectionState != BluetoothProfile.STATE_CONNECTED) {
            return false;
        }
        // Currently we support only "+ANDROID".
        if (!command.equals(BluetoothHeadset.VENDOR_RESULT_CODE_COMMAND_ANDROID)) {
            Log.w(TAG, "Disallowed unsolicited result code command: " + command);
            return false;
        }
        mStateMachine.sendMessage(HeadsetStateMachine.SEND_VENDOR_SPECIFIC_RESULT_CODE,
                new HeadsetVendorSpecificResultCode(device, command, arg));
        return true;
    }

    boolean enableWBS() {
    	Log.e(TAG,"enableWBS");
        // TODO(BT) BLUETOOTH or BLUETOOTH_ADMIN permission
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (!mStateMachine.isConnected()) {
            return false;
        }
        if (mStateMachine.isAudioOn()) {
            return false;
        }

        for (BluetoothDevice device: getConnectedDevices()) {
            mStateMachine.sendMessage(HeadsetStateMachine.ENABLE_WBS,device);
        }

        return true;
    }

    boolean disableWBS() {
    	Log.e(TAG,"disableWBS");
        // TODO(BT) BLUETOOTH or BLUETOOTH_ADMIN permission
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (!mStateMachine.isConnected()) {
            return false;
        }
        if (mStateMachine.isAudioOn()) {
            return false;
        }
        for (BluetoothDevice device: getConnectedDevices()) {
            mStateMachine.sendMessage(HeadsetStateMachine.DISABLE_WBS,device);
        }
        return true;
    }

    @Override
    public void dump(StringBuilder sb) {
        super.dump(sb);
        if (mStateMachine != null) {
            mStateMachine.dump(sb);
        }
    }
}
