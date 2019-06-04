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

/**
 * @hide
 */

package com.android.bluetooth.btservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothUuid;
import android.bluetooth.IBluetooth;
import android.bluetooth.IBluetoothCallback;
import android.bluetooth.BluetoothActivityEnergyInfo;
import android.bluetooth.OobData;
import android.bluetooth.UidTraffic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryStats;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.ParcelUuid;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.EventLog;
import android.util.Log;

import android.util.Slog;
import android.util.SparseArray;
import com.android.bluetooth.a2dp.A2dpService;
import com.android.bluetooth.a2dpsink.A2dpSinkService;
import com.android.bluetooth.hid.HidService;
import com.android.bluetooth.hfp.HeadsetService;
import com.android.bluetooth.hfpclient.HeadsetClientService;
import com.android.bluetooth.pbapclient.PbapClientService;
import com.android.bluetooth.sdp.SdpManager;
import com.android.internal.R;
import com.android.bluetooth.Utils;
import com.android.bluetooth.btservice.RemoteDevices.DeviceProperties;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;

import android.os.ServiceManager;
import com.android.internal.app.IBatteryStats;

public class AdapterService extends Service {
    private static final String TAG = "LZLB_AdapterService";
    private static final boolean DBG = true;
    private static final boolean TRACE_REF = true;
    private static final int MIN_ADVT_INSTANCES_FOR_MA = 5;
    private static final int MIN_OFFLOADED_FILTERS = 10;
    private static final int MIN_OFFLOADED_SCAN_STORAGE_BYTES = 1024;
    //For Debugging only
    private static int sRefCount = 0;
    private long mBluetoothStartTime = 0;

    private final Object mEnergyInfoLock = new Object();
    private int mStackReportedState;
    private long mTxTimeTotalMs;
    private long mRxTimeTotalMs;
    private long mIdleTimeTotalMs;
    private long mEnergyUsedTotalVoltAmpSecMicro;
    private SparseArray<UidTraffic> mUidTraffic = new SparseArray<>();

    private final ArrayList<ProfileService> mProfiles = new ArrayList<ProfileService>();

    public static final String ACTION_LOAD_ADAPTER_PROPERTIES =
        "com.android.bluetooth.btservice.action.LOAD_ADAPTER_PROPERTIES";
    public static final String ACTION_SERVICE_STATE_CHANGED =
        "com.android.bluetooth.btservice.action.STATE_CHANGED";
    public static final String EXTRA_ACTION="action";
    public static final int PROFILE_CONN_CONNECTED  = 1;
    public static final int PROFILE_CONN_REJECTED  = 2;

    private static final String ACTION_ALARM_WAKEUP =
        "com.android.bluetooth.btservice.action.ALARM_WAKEUP";

    public static final String BLUETOOTH_ADMIN_PERM =
        android.Manifest.permission.BLUETOOTH_ADMIN;
    public static final String BLUETOOTH_PRIVILEGED =
                android.Manifest.permission.BLUETOOTH_PRIVILEGED;
    static final String BLUETOOTH_PERM = android.Manifest.permission.BLUETOOTH;
    static final String RECEIVE_MAP_PERM = android.Manifest.permission.RECEIVE_BLUETOOTH_MAP;

    private static final String PHONEBOOK_ACCESS_PERMISSION_PREFERENCE_FILE =
            "phonebook_access_permission";
    private static final String MESSAGE_ACCESS_PERMISSION_PREFERENCE_FILE =
            "message_access_permission";
    private static final String SIM_ACCESS_PERMISSION_PREFERENCE_FILE =
            "sim_access_permission";

    private static final int ADAPTER_SERVICE_TYPE=Service.START_STICKY;

    private static final String[] DEVICE_TYPE_NAMES = new String[] {
      "???",
      "BR/EDR",
      "LE",
      "DUAL"
    };

    private static final int CONTROLLER_ENERGY_UPDATE_TIMEOUT_MILLIS = 30;

    static {
        classInitNative();
    }

    private static AdapterService sAdapterService;
    public static synchronized AdapterService getAdapterService(){
			Log.e(TAG,"getAdapterService");
        if (sAdapterService != null && !sAdapterService.mCleaningUp) {
            Log.d(TAG, "getAdapterService() - returning " + sAdapterService);
            return sAdapterService;
        }
        if (DBG)  {
            if (sAdapterService == null) {
                Log.d(TAG, "getAdapterService() - Service not available");
            } else if (sAdapterService.mCleaningUp) {
                Log.d(TAG,"getAdapterService() - Service is cleaning up");
            }
        }
        return null;
    }

    private static synchronized void setAdapterService(AdapterService instance) {
    Log.e(TAG,"setAdapterService");
        if (instance != null && !instance.mCleaningUp) {
            if (DBG) Log.d(TAG, "setAdapterService() - set to: " + sAdapterService);
            sAdapterService = instance;
        } else {
            if (DBG)  {
                if (sAdapterService == null) {
                    Log.d(TAG, "setAdapterService() - Service not available");
                } else if (sAdapterService.mCleaningUp) {
                    Log.d(TAG,"setAdapterService() - Service is cleaning up");
                }
            }
        }
    }

    private static synchronized void clearAdapterService() {
        sAdapterService = null;
    }

    private AdapterProperties mAdapterProperties;
    private AdapterState mAdapterStateMachine;
    private BondStateMachine mBondStateMachine;
    private JniCallbacks mJniCallbacks;
    private RemoteDevices mRemoteDevices;

    /* TODO: Consider to remove the search API from this class, if changed to use call-back */
    private SdpManager mSdpManager = null;

    private boolean mProfilesStarted;
    private boolean mNativeAvailable;
    private boolean mCleaningUp;
    private HashMap<String,Integer> mProfileServicesState = new HashMap<String,Integer>();
    //Only BluetoothManagerService should be registered
    private RemoteCallbackList<IBluetoothCallback> mCallbacks;
    private int mCurrentRequestId;
    private boolean mQuietmode = false;

    private AlarmManager mAlarmManager;
    private PendingIntent mPendingAlarm;
    private IBatteryStats mBatteryStats;
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private String mWakeLockName;

    private ProfileObserver mProfileObserver;

    public AdapterService() {
        super();
		Log.e(TAG,"AdapterService");
        if (TRACE_REF) {
            synchronized (AdapterService.class) {
                sRefCount++;
                debugLog("AdapterService() - REFCOUNT: CREATED. INSTANCE_COUNT" + sRefCount);
            }
        }

        // This is initialized at the beginning in order to prevent
        // NullPointerException from happening if AdapterService
        // functions are called before BLE is turned on due to
        // |mRemoteDevices| being null.
        mRemoteDevices = new RemoteDevices(this);
    }

    public void onProfileConnectionStateChanged(BluetoothDevice device, int profileId, int newState, int prevState) {
    Log.e(TAG,"onProfileConnectionStateChanged");
        Message m = mHandler.obtainMessage(MESSAGE_PROFILE_CONNECTION_STATE_CHANGED);
        m.obj = device;
        m.arg1 = profileId;
        m.arg2 = newState;
        Bundle b = new Bundle(1);
        b.putInt("prevState", prevState);
        m.setData(b);
        mHandler.sendMessage(m);
    }

    public void initProfilePriorities(BluetoothDevice device, ParcelUuid[] mUuids) {
    Log.e(TAG,"initProfilePriorities");
        if(mUuids == null) return;
        Message m = mHandler.obtainMessage(MESSAGE_PROFILE_INIT_PRIORITIES);
        m.obj = device;
        m.arg1 = mUuids.length;
        Bundle b = new Bundle(1);
        for(int i=0; i<mUuids.length; i++) {
            b.putParcelable("uuids" + i, mUuids[i]);
        }
        m.setData(b);
        mHandler.sendMessage(m);
    }

    private void processInitProfilePriorities (BluetoothDevice device, ParcelUuid[] uuids){
    Log.e(TAG,"processInitProfilePriorities");
        HidService hidService = HidService.getHidService();
        A2dpService a2dpService = A2dpService.getA2dpService();
        A2dpSinkService a2dpSinkService = A2dpSinkService.getA2dpSinkService();
        HeadsetService headsetService = HeadsetService.getHeadsetService();
        HeadsetClientService headsetClientService = HeadsetClientService.getHeadsetClientService();
        PbapClientService pbapClientService = PbapClientService.getPbapClientService();

        // Set profile priorities only for the profiles discovered on the remote device.
        // This avoids needless auto-connect attempts to profiles non-existent on the remote device
        if ((hidService != null) &&
            (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Hid) ||
             BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Hogp)) &&
            (hidService.getPriority(device) == BluetoothProfile.PRIORITY_UNDEFINED)){
            hidService.setPriority(device,BluetoothProfile.PRIORITY_ON);
        }

        // If we do not have a stored priority for HFP/A2DP (all roles) then default to on.
        if ((headsetService != null) &&
            ((BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.HSP) ||
                    BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Handsfree)) &&
            (headsetService.getPriority(device) == BluetoothProfile.PRIORITY_UNDEFINED))) {
            headsetService.setPriority(device,BluetoothProfile.PRIORITY_ON);
        }

        if ((a2dpService != null) &&
            (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.AudioSink) ||
            BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.AdvAudioDist)) &&
            (a2dpService.getPriority(device) == BluetoothProfile.PRIORITY_UNDEFINED)){
            a2dpService.setPriority(device,BluetoothProfile.PRIORITY_ON);
        }

        if ((headsetClientService != null) &&
            ((BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.Handsfree_AG) ||
              BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.HSP_AG)) &&
             (headsetClientService.getPriority(device) == BluetoothProfile.PRIORITY_UNDEFINED))) {
            headsetClientService.setPriority(device, BluetoothProfile.PRIORITY_ON);
        }

        if ((a2dpSinkService != null) &&
            (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.AudioSource) &&
             (a2dpSinkService.getPriority(device) == BluetoothProfile.PRIORITY_UNDEFINED))) {
            a2dpSinkService.setPriority(device, BluetoothProfile.PRIORITY_ON);
        }

        if ((pbapClientService != null) &&
            (BluetoothUuid.isUuidPresent(uuids, BluetoothUuid.PBAP_PSE) &&
             (pbapClientService.getPriority(device) == BluetoothProfile.PRIORITY_UNDEFINED))) {
            pbapClientService.setPriority(device, BluetoothProfile.PRIORITY_ON);
        }
    }

    private void processProfileStateChanged(BluetoothDevice device, int profileId, int newState, int prevState) {
    Log.e(TAG,"processProfileStateChanged");
        // Profiles relevant to phones.
        if (((profileId == BluetoothProfile.A2DP) || (profileId == BluetoothProfile.HEADSET)) &&
             (newState == BluetoothProfile.STATE_CONNECTED)){
            debugLog( "Profile connected. Schedule missing profile connection if any");
            connectOtherProfile(device, PROFILE_CONN_CONNECTED);
            setProfileAutoConnectionPriority(device, profileId);
        }

        // Profiles relevant to Car Kitts.
        if (((profileId == BluetoothProfile.A2DP_SINK) ||
             (profileId == BluetoothProfile.HEADSET_CLIENT)) &&
            (newState == BluetoothProfile.STATE_CONNECTED)) {
            debugLog( "Profile connected. Schedule missing profile connection if any");
            connectOtherProfile(device, PROFILE_CONN_CONNECTED);
            setProfileAutoConnectionPriority(device, profileId);
        }

        IBluetooth.Stub binder = mBinder;
        if (binder != null) {
            try {
                binder.sendConnectionStateChange(device, profileId, newState,prevState);
            } catch (RemoteException re) {
                errorLog("" + re);
            }
        }
    }

    public void addProfile(ProfileService profile) {
    	Log.e(TAG,"addProfile");
        synchronized (mProfiles) {
            if (!mProfiles.contains(profile)) {
                mProfiles.add(profile);
            }
        }
    }

    public void removeProfile(ProfileService profile) {
    	Log.e(TAG,"removeProfile");
        synchronized (mProfiles) {
            mProfiles.remove(profile);
        }
    }

    public void onProfileServiceStateChanged(String serviceName, int state) {
    Log.e(TAG,"onProfileServiceStateChanged");
        Message m = mHandler.obtainMessage(MESSAGE_PROFILE_SERVICE_STATE_CHANGED);
        m.obj=serviceName;
        m.arg1 = state;
        mHandler.sendMessage(m);
    }

    private void processProfileServiceStateChanged(String serviceName, int state) {
    Log.e(TAG,"processProfileServiceStateChanged");
        boolean doUpdate=false;
        boolean isBleTurningOn;
        boolean isBleTurningOff;
        boolean isTurningOn;
        boolean isTurningOff;

        synchronized (mProfileServicesState) {
            Integer prevState = mProfileServicesState.get(serviceName);
            if (prevState != null && prevState != state) {
                mProfileServicesState.put(serviceName,state);
                doUpdate=true;
            }
        }
        debugLog("processProfileServiceStateChanged() serviceName=" + serviceName
            + ", state=" + state +", doUpdate=" + doUpdate);

        if (!doUpdate) {
            return;
        }

        synchronized (mAdapterStateMachine) {
            isTurningOff = mAdapterStateMachine.isTurningOff();
            isTurningOn = mAdapterStateMachine.isTurningOn();
            isBleTurningOn = mAdapterStateMachine.isBleTurningOn();
            isBleTurningOff = mAdapterStateMachine.isBleTurningOff();
        }

        debugLog("processProfileServiceStateChanged() - serviceName=" + serviceName +
                 " isTurningOn=" + isTurningOn + " isTurningOff=" + isTurningOff +
                 " isBleTurningOn=" + isBleTurningOn + " isBleTurningOff=" + isBleTurningOff);

        if (isBleTurningOn) {
            if (serviceName.equals("com.android.bluetooth.gatt.GattService")) {
                debugLog("GattService is started");
                mAdapterStateMachine.sendMessage(mAdapterStateMachine.obtainMessage(AdapterState.BLE_STARTED));
                return;
            }

        } else if(isBleTurningOff) {
            if (serviceName.equals("com.android.bluetooth.gatt.GattService")) {
                debugLog("GattService stopped");
                mAdapterStateMachine.sendMessage(mAdapterStateMachine.obtainMessage(AdapterState.BLE_STOPPED));
                return;
            }

        } else if (isTurningOff) {
            //On to BLE_ON
            //Process stop or disable pending
            //Check if all services are stopped if so, do cleanup
            synchronized (mProfileServicesState) {
                Iterator<Map.Entry<String,Integer>> i = mProfileServicesState.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<String,Integer> entry = i.next();
                    debugLog("Service: " + entry.getKey());
                    if (entry.getKey().equals("com.android.bluetooth.gatt.GattService")) {
                        debugLog("Skip GATT service - already started before");
                        continue;
                    }
                    if (BluetoothAdapter.STATE_OFF != entry.getValue()) {
                        debugLog("onProfileServiceStateChange() - Profile still running: "
                            + entry.getKey());
                        return;
                    }
                }
            }
            debugLog("onProfileServiceStateChange() - All profile services stopped...");
            //Send message to state machine
            mProfilesStarted=false;
            mAdapterStateMachine.sendMessage(mAdapterStateMachine.obtainMessage(AdapterState.BREDR_STOPPED));

        } else if (isTurningOn) {
            updateInteropDatabase();

            //Process start pending
            //Check if all services are started if so, update state
            synchronized (mProfileServicesState) {
                Iterator<Map.Entry<String,Integer>> i = mProfileServicesState.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry<String,Integer> entry = i.next();
                    debugLog("Service: " + entry.getKey());
                    if (entry.getKey().equals("com.android.bluetooth.gatt.GattService")) {
                        debugLog("Skip GATT service - already started before");
                        continue;
                    }
                    if (BluetoothAdapter.STATE_ON != entry.getValue()) {
                        debugLog("onProfileServiceStateChange() - Profile still not running:"
                            + entry.getKey());
                        return;
                    }
                }
            }
            debugLog("onProfileServiceStateChange() - All profile services started.");
            mProfilesStarted=true;
            //Send message to state machine
            mAdapterStateMachine.sendMessage(mAdapterStateMachine.obtainMessage(AdapterState.BREDR_STARTED));
        }
    }

    private void updateInteropDatabase() {
    	Log.e(TAG,"updateInteropDatabase");
        interopDatabaseClearNative();

        String interop_string = Settings.Global.getString(getContentResolver(),
                                            Settings.Global.BLUETOOTH_INTEROPERABILITY_LIST);
        if (interop_string == null) return;
        Log.d(TAG, "updateInteropDatabase: [" + interop_string + "]");

        String[] entries = interop_string.split(";");
        for (String entry : entries) {
            String[] tokens = entry.split(",");
            if (tokens.length != 2) continue;

            // Get feature
            int feature = 0;
            try {
                feature = Integer.parseInt(tokens[1]);
            } catch (NumberFormatException e) {
                Log.e(TAG, "updateInteropDatabase: Invalid feature '" + tokens[1] + "'");
                continue;
            }

            // Get address bytes and length
            int length = (tokens[0].length() + 1) / 3;
            if (length < 1 || length > 6) {
                Log.e(TAG, "updateInteropDatabase: Malformed address string '" + tokens[0] + "'");
                continue;
            }

            byte[] addr = new byte[6];
            int offset = 0;
            for (int i = 0; i < tokens[0].length(); ) {
                if (tokens[0].charAt(i) == ':') {
                    i += 1;
                } else {
                    try {
                        addr[offset++] = (byte) Integer.parseInt(tokens[0].substring(i, i + 2), 16);
                    } catch (NumberFormatException e) {
                        offset = 0;
                        break;
                    }
                    i += 2;
                }
            }

            // Check if address was parsed ok, otherwise, move on...
            if (offset == 0) continue;

            // Add entry
            interopDatabaseAddNative(feature, addr, length);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
		Log.e(TAG,"onCreate");
        debugLog("onCreate()");
        mBinder = new AdapterServiceBinder(this);
        mAdapterProperties = new AdapterProperties(this);
        mAdapterStateMachine =  AdapterState.make(this, mAdapterProperties);
        mJniCallbacks =  new JniCallbacks(mAdapterStateMachine, mAdapterProperties);
        initNative();
        mNativeAvailable=true;
        mCallbacks = new RemoteCallbackList<IBluetoothCallback>();
        //Load the name and address
        getAdapterPropertyNative(AbstractionLayer.BT_PROPERTY_BDADDR);
        getAdapterPropertyNative(AbstractionLayer.BT_PROPERTY_BDNAME);
        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService(
                BatteryStats.SERVICE_NAME));

        mSdpManager = SdpManager.init(this);
        registerReceiver(mAlarmBroadcastReceiver, new IntentFilter(ACTION_ALARM_WAKEUP));
        mProfileObserver = new ProfileObserver(getApplicationContext(), this, new Handler());
        mProfileObserver.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        debugLog("onBind()");
		Log.e(TAG,"onBind");
        return mBinder;
    }
    public boolean onUnbind(Intent intent) {
        debugLog("onUnbind() - calling cleanup");
		Log.e(TAG,"onUnbind");
        cleanup();
        return super.onUnbind(intent);
    }

    public void onDestroy() {
        debugLog("onDestroy()");
		Log.e(TAG,"onDestroy");
        mProfileObserver.stop();
    }

    void BleOnProcessStart() {
        debugLog("BleOnProcessStart()");
		Log.e(TAG,"BleOnProcessStart");

        if (getApplicationContext().getResources().getBoolean(
                R.bool.config_bluetooth_reload_supported_profiles_when_enabled)) {
            Config.init(getApplicationContext());
        }

        Class[] supportedProfileServices = Config.getSupportedProfiles();
        //Initialize data objects
        for (int i=0; i < supportedProfileServices.length;i++) {
            mProfileServicesState.put(supportedProfileServices[i].getName(),BluetoothAdapter.STATE_OFF);
        }

        // Reset |mRemoteDevices| whenever BLE is turned off then on
        // This is to replace the fact that |mRemoteDevices| was
        // reinitialized in previous code.
        //
        // TODO(apanicke): The reason is unclear but
        // I believe it is to clear the variable every time BLE was
        // turned off then on. The same effect can be achieved by
        // calling cleanup but this may not be necessary at all
        // We should figure out why this is needed later
        mRemoteDevices.cleanup();
        mAdapterProperties.init(mRemoteDevices);

        debugLog("BleOnProcessStart() - Make Bond State Machine");
        mBondStateMachine = BondStateMachine.make(this, mAdapterProperties, mRemoteDevices);

        mJniCallbacks.init(mBondStateMachine,mRemoteDevices);

        try {
            mBatteryStats.noteResetBleScan();
        } catch (RemoteException e) {
            // Ignore.
        }

        //FIXME: Set static instance here???
        setAdapterService(this);

        //Start Gatt service
        setGattProfileServiceState(supportedProfileServices,BluetoothAdapter.STATE_ON);
    }

    void startCoreServices()
    {
    	Log.e(TAG,"startCoreServices");
        debugLog("startCoreServices()");
        Class[] supportedProfileServices = Config.getSupportedProfiles();

        //Start profile services
        if (!mProfilesStarted && supportedProfileServices.length >0) {
            //Startup all profile services
            setProfileServiceState(supportedProfileServices,BluetoothAdapter.STATE_ON);
        }else {
            debugLog("startCoreProfiles(): Profile Services alreay started");
            mAdapterStateMachine.sendMessage(mAdapterStateMachine.obtainMessage(AdapterState.BREDR_STARTED));
        }
    }

    void startBluetoothDisable() {
    	Log.e(TAG,"startBluetoothDisable");
        mAdapterStateMachine.sendMessage(mAdapterStateMachine.obtainMessage(AdapterState.BEGIN_DISABLE));
    }

    boolean stopProfileServices() {
    	Log.e(TAG,"stopProfileServices");
        Class[] supportedProfileServices = Config.getSupportedProfiles();
        if (mProfilesStarted && supportedProfileServices.length>0) {
            setProfileServiceState(supportedProfileServices,BluetoothAdapter.STATE_OFF);
            return true;
        }
        debugLog("stopProfileServices() - No profiles services to stop or already stopped.");
        return false;
    }

    boolean stopGattProfileService() {
    	Log.e(TAG,"stopGattProfileService");
        //TODO: can optimize this instead of looping around all supported profiles
        debugLog("stopGattProfileService()");
        Class[] supportedProfileServices = Config.getSupportedProfiles();

        setGattProfileServiceState(supportedProfileServices,BluetoothAdapter.STATE_OFF);
        return true;
    }


     void updateAdapterState(int prevState, int newState){
     	Log.e(TAG,"updateAdapterState");
        if (mCallbacks !=null) {
            int n=mCallbacks.beginBroadcast();
            debugLog("updateAdapterState() - Broadcasting state to " + n + " receivers.");
            for (int i=0; i <n;i++) {
                try {
                    mCallbacks.getBroadcastItem(i).onBluetoothStateChange(prevState,newState);
                }  catch (RemoteException e) {
                    debugLog("updateAdapterState() - Callback #" + i + " failed ("  + e + ")");
                }
            }
            mCallbacks.finishBroadcast();
        }
    }

    void cleanup () {
    	Log.e(TAG,"cleanup");
        debugLog("cleanup()");
        if (mCleaningUp) {
            errorLog("cleanup() - Service already starting to cleanup, ignoring request...");
            return;
        }

        mCleaningUp = true;

        unregisterReceiver(mAlarmBroadcastReceiver);

        if (mPendingAlarm != null) {
            mAlarmManager.cancel(mPendingAlarm);
            mPendingAlarm = null;
        }

        // This wake lock release may also be called concurrently by
        // {@link #releaseWakeLock(String lockName)}, so a synchronization is needed here.
        synchronized (this) {
            if (mWakeLock != null) {
                if (mWakeLock.isHeld())
                    mWakeLock.release();
                mWakeLock = null;
            }
        }

        if (mAdapterStateMachine != null) {
            mAdapterStateMachine.doQuit();
            mAdapterStateMachine.cleanup();
        }

        if (mBondStateMachine != null) {
            mBondStateMachine.doQuit();
            mBondStateMachine.cleanup();
        }

        if (mRemoteDevices != null) {
            mRemoteDevices.cleanup();
        }

        if(mSdpManager != null) {
            mSdpManager.cleanup();
            mSdpManager = null;
        }

        if (mNativeAvailable) {
            debugLog("cleanup() - Cleaning up adapter native");
            cleanupNative();
            mNativeAvailable=false;
        }

        if (mAdapterProperties != null) {
            mAdapterProperties.cleanup();
        }

        if (mJniCallbacks != null) {
            mJniCallbacks.cleanup();
        }

        if (mProfileServicesState != null) {
            mProfileServicesState.clear();
        }

        clearAdapterService();

        if (mBinder != null) {
            mBinder.cleanup();
            mBinder = null;  //Do not remove. Otherwise Binder leak!
        }

        if (mCallbacks !=null) {
            mCallbacks.kill();
        }

        System.exit(0);
    }

    private static final int MESSAGE_PROFILE_SERVICE_STATE_CHANGED =1;
    private static final int MESSAGE_PROFILE_CONNECTION_STATE_CHANGED=20;
    private static final int MESSAGE_CONNECT_OTHER_PROFILES = 30;
    private static final int MESSAGE_PROFILE_INIT_PRIORITIES=40;
    private static final int CONNECT_OTHER_PROFILES_TIMEOUT= 6000;

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.e(TAG,"mHandler handleMessage msg.what:"+msg.what);
            debugLog("handleMessage() - Message: " + msg.what);

            switch (msg.what) {
                case MESSAGE_PROFILE_SERVICE_STATE_CHANGED: {
                    debugLog("handleMessage() - MESSAGE_PROFILE_SERVICE_STATE_CHANGED");
                    processProfileServiceStateChanged((String) msg.obj, msg.arg1);
                }
                    break;
                case MESSAGE_PROFILE_CONNECTION_STATE_CHANGED: {
                    debugLog( "handleMessage() - MESSAGE_PROFILE_CONNECTION_STATE_CHANGED");
                    processProfileStateChanged((BluetoothDevice) msg.obj, msg.arg1,msg.arg2, msg.getData().getInt("prevState",BluetoothAdapter.ERROR));
                }
                    break;
                case MESSAGE_PROFILE_INIT_PRIORITIES: {
                    debugLog( "handleMessage() - MESSAGE_PROFILE_INIT_PRIORITIES");
                    ParcelUuid[] mUuids = new ParcelUuid[msg.arg1];
                    for(int i=0; i<mUuids.length; i++) {
                        mUuids[i] = msg.getData().getParcelable("uuids" + i);
                    }
                    processInitProfilePriorities((BluetoothDevice) msg.obj, mUuids);
                }
                    break;
                case MESSAGE_CONNECT_OTHER_PROFILES: {
                    debugLog( "handleMessage() - MESSAGE_CONNECT_OTHER_PROFILES");
                    processConnectOtherProfiles((BluetoothDevice) msg.obj,msg.arg1);
                }
                    break;
            }
        }
    };

    @SuppressWarnings("rawtypes")
    private void setGattProfileServiceState(Class[] services, int state) {
    Log.e(TAG,"setGattProfileServiceState");
        if (state != BluetoothAdapter.STATE_ON && state != BluetoothAdapter.STATE_OFF) {
            Log.w(TAG,"setGattProfileServiceState(): invalid state...Leaving...");
            return;
        }

        int expectedCurrentState= BluetoothAdapter.STATE_OFF;
        int pendingState = BluetoothAdapter.STATE_TURNING_ON;

        if (state == BluetoothAdapter.STATE_OFF) {
            expectedCurrentState= BluetoothAdapter.STATE_ON;
            pendingState = BluetoothAdapter.STATE_TURNING_OFF;
        }

        for (int i=0; i <services.length;i++) {
            String serviceName = services[i].getName();
            String simpleName = services[i].getSimpleName();

            if (simpleName.equals("GattService")) {
                Integer serviceState = mProfileServicesState.get(serviceName);

                if(serviceState != null && serviceState != expectedCurrentState) {
                    debugLog("setProfileServiceState() - Unable to "
                        + (state == BluetoothAdapter.STATE_OFF ? "start" : "stop" )
                        + " service " + serviceName
                        + ". Invalid state: " + serviceState);
                        continue;
                }
                debugLog("setProfileServiceState() - "
                    + (state == BluetoothAdapter.STATE_OFF ? "Stopping" : "Starting")
                    + " service " + serviceName);

                mProfileServicesState.put(serviceName,pendingState);
                Intent intent = new Intent(this,services[i]);
                intent.putExtra(EXTRA_ACTION,ACTION_SERVICE_STATE_CHANGED);
                intent.putExtra(BluetoothAdapter.EXTRA_STATE,state);
                startService(intent);
                return;
            }
        }
    }


    @SuppressWarnings("rawtypes")
    private void setProfileServiceState(Class[] services, int state) {
    Log.e(TAG,"setProfileServiceState state:"+state);
        if (state != BluetoothAdapter.STATE_ON && state != BluetoothAdapter.STATE_OFF) {
            debugLog("setProfileServiceState() - Invalid state, leaving...");
            return;
        }

        int expectedCurrentState= BluetoothAdapter.STATE_OFF;
        int pendingState = BluetoothAdapter.STATE_TURNING_ON;
        if (state == BluetoothAdapter.STATE_OFF) {
            expectedCurrentState= BluetoothAdapter.STATE_ON;
            pendingState = BluetoothAdapter.STATE_TURNING_OFF;
        }

        for (int i=0; i <services.length;i++) {
            String serviceName = services[i].getName();
            String simpleName = services[i].getSimpleName();

            if (simpleName.equals("GattService")) continue;

            Integer serviceState = mProfileServicesState.get(serviceName);
            if(serviceState != null && serviceState != expectedCurrentState) {
                debugLog("setProfileServiceState() - Unable to "
                    + (state == BluetoothAdapter.STATE_OFF ? "start" : "stop" )
                    + " service " + serviceName
                    + ". Invalid state: " + serviceState);
                continue;
            }

            debugLog("setProfileServiceState() - "
                + (state == BluetoothAdapter.STATE_OFF ? "Stopping" : "Starting")
                + " service " + serviceName);

            mProfileServicesState.put(serviceName,pendingState);
            Intent intent = new Intent(this,services[i]);
            intent.putExtra(EXTRA_ACTION,ACTION_SERVICE_STATE_CHANGED);
            intent.putExtra(BluetoothAdapter.EXTRA_STATE,state);
            startService(intent);
        }
    }

    private boolean isAvailable() {
        return !mCleaningUp;
    }

    /**
     * Handlers for incoming service calls
     */
    private AdapterServiceBinder mBinder;

    /**
     * The Binder implementation must be declared to be a static class, with
     * the AdapterService instance passed in the constructor. Furthermore,
     * when the AdapterService shuts down, the reference to the AdapterService
     * must be explicitly removed.
     *
     * Otherwise, a memory leak can occur from repeated starting/stopping the
     * service...Please refer to android.os.Binder for further details on
     * why an inner instance class should be avoided.
     *
     */
    private static class AdapterServiceBinder extends IBluetooth.Stub {
        private AdapterService mService;
		private static final String TAG ="LZLB_AdapterServiceBinder";

        public AdapterServiceBinder(AdapterService svc) {
            mService = svc;
        }
        public boolean cleanup() {
        	Log.e(TAG,"cleanup");
            mService = null;
            return true;
        }

        public AdapterService getService() {
        	Log.e(TAG,"getService");
            if (mService  != null && mService.isAvailable()) {
                return mService;
            }
            return null;
        }
        public boolean isEnabled() {
        	Log.e(TAG,"isEnabled");
            // don't check caller, may be called from system UI
            AdapterService service = getService();
            if (service == null) return false;
            return service.isEnabled();
        }

        public int getState() {
        	Log.e(TAG,"getState");
            // don't check caller, may be called from system UI
            AdapterService service = getService();
            if (service == null) return  BluetoothAdapter.STATE_OFF;
            return service.getState();
        }

        public boolean enable() {
        	Log.e(TAG,"enable");
            if ((Binder.getCallingUid() != Process.SYSTEM_UID) &&
                (!Utils.checkCaller())) {
                Log.w(TAG, "enable() - Not allowed for non-active user and non system user");
                return false;
            }
            AdapterService service = getService();
            if (service == null) return false;
            return service.enable();
        }

        public boolean enableNoAutoConnect() {
        	Log.e(TAG,"enableNoAutoConnect");
            if ((Binder.getCallingUid() != Process.SYSTEM_UID) &&
                (!Utils.checkCaller())) {
                Log.w(TAG, "enableNoAuto() - Not allowed for non-active user and non system user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.enableNoAutoConnect();
        }

        public boolean disable() {
        	Log.e(TAG,"disable");
            if ((Binder.getCallingUid() != Process.SYSTEM_UID) &&
                (!Utils.checkCaller())) {
                Log.w(TAG, "disable() - Not allowed for non-active user and non system user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.disable();
        }

        public String getAddress() {
        	Log.e(TAG,"getAddress");
            if ((Binder.getCallingUid() != Process.SYSTEM_UID) &&
                (!Utils.checkCallerAllowManagedProfiles(mService))) {
                Log.w(TAG, "getAddress() - Not allowed for non-active user and non system user");
                return null;
            }

            AdapterService service = getService();
            if (service == null) return null;
            return service.getAddress();
        }

        public ParcelUuid[] getUuids() {
        	Log.e(TAG,"getUuids");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "getUuids() - Not allowed for non-active user");
                return new ParcelUuid[0];
            }

            AdapterService service = getService();
            if (service == null) return new ParcelUuid[0];
            return service.getUuids();
        }

        public String getName() {
        	Log.e(TAG,"getName");
            if ((Binder.getCallingUid() != Process.SYSTEM_UID) &&
                (!Utils.checkCaller())) {
                Log.w(TAG, "getName() - Not allowed for non-active user and non system user");
                return null;
            }

            AdapterService service = getService();
            if (service == null) return null;
            return service.getName();
        }

        public boolean setName(String name) {
        	Log.e(TAG,"setName");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setName() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setName(name);
        }

        public int getScanMode() {
        	Log.e(TAG,"getScanMode");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getScanMode() - Not allowed for non-active user");
                return BluetoothAdapter.SCAN_MODE_NONE;
            }

            AdapterService service = getService();
            if (service == null) return BluetoothAdapter.SCAN_MODE_NONE;
            return service.getScanMode();
        }

        public boolean setScanMode(int mode, int duration) {
        	Log.e(TAG,"setScanMode");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setScanMode() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setScanMode(mode,duration);
        }

        public int getDiscoverableTimeout() {
        	Log.e(TAG,"getDiscoverableTimeout");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "getDiscoverableTimeout() - Not allowed for non-active user");
                return 0;
            }

            AdapterService service = getService();
            if (service == null) return 0;
            return service.getDiscoverableTimeout();
        }

        public boolean setDiscoverableTimeout(int timeout) {
        	Log.e(TAG,"setDiscoverableTimeout");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setDiscoverableTimeout() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setDiscoverableTimeout(timeout);
        }

        public boolean startDiscovery() {
        	Log.e(TAG,"startDiscovery");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "startDiscovery() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.startDiscovery();
        }

        public boolean cancelDiscovery() {
        	Log.e(TAG,"cancelDiscovery");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "cancelDiscovery() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.cancelDiscovery();
        }
        public boolean isDiscovering() {
        	Log.e(TAG,"isDiscovering");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "isDiscovering() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.isDiscovering();
        }

        public BluetoothDevice[] getBondedDevices() {
        	Log.e(TAG,"getBondedDevices");
            // don't check caller, may be called from system UI
            AdapterService service = getService();
            if (service == null) return new BluetoothDevice[0];
            return service.getBondedDevices();
        }

        public int getAdapterConnectionState() {
        	Log.e(TAG,"getAdapterConnectionState");
            // don't check caller, may be called from system UI
            AdapterService service = getService();
            if (service == null) return BluetoothAdapter.STATE_DISCONNECTED;
            return service.getAdapterConnectionState();
        }

        public int getProfileConnectionState(int profile) {
        	Log.e(TAG,"getProfileConnectionState");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getProfileConnectionState- Not allowed for non-active user");
                return BluetoothProfile.STATE_DISCONNECTED;
            }

            AdapterService service = getService();
            if (service == null) return BluetoothProfile.STATE_DISCONNECTED;
            return service.getProfileConnectionState(profile);
        }

        public boolean createBond(BluetoothDevice device, int transport) {
        	Log.e(TAG,"createBond");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "createBond() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.createBond(device, transport, null);
        }

        public boolean createBondOutOfBand(BluetoothDevice device, int transport, OobData oobData) {
        Log.e(TAG,"createBondOutOfBand");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "createBondOutOfBand() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.createBond(device, transport, oobData);
        }

        public boolean cancelBondProcess(BluetoothDevice device) {
        	Log.e(TAG,"cancelBondProcess");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "cancelBondProcess() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.cancelBondProcess(device);
        }

        public boolean removeBond(BluetoothDevice device) {
        	Log.e(TAG,"removeBond");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "removeBond() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.removeBond(device);
        }

        public int getBondState(BluetoothDevice device) {
        	Log.e(TAG,"getBondState");
            // don't check caller, may be called from system UI
            AdapterService service = getService();
            if (service == null) return BluetoothDevice.BOND_NONE;
            return service.getBondState(device);
        }

        public int getConnectionState(BluetoothDevice device) {
        	Log.e(TAG,"getConnectionState");
            AdapterService service = getService();
            if (service == null) return 0;
            return service.getConnectionState(device);
        }

        public String getRemoteName(BluetoothDevice device) {
        	Log.e(TAG,"getRemoteName");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getRemoteName() - Not allowed for non-active user");
                return null;
            }

            AdapterService service = getService();
            if (service == null) return null;
            return service.getRemoteName(device);
        }

        public int getRemoteType(BluetoothDevice device) {
        	Log.e(TAG,"getRemoteType");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getRemoteType() - Not allowed for non-active user");
                return BluetoothDevice.DEVICE_TYPE_UNKNOWN;
            }

            AdapterService service = getService();
            if (service == null) return BluetoothDevice.DEVICE_TYPE_UNKNOWN;
            return service.getRemoteType(device);
        }

        public String getRemoteAlias(BluetoothDevice device) {
        	Log.e(TAG,"getRemoteAlias");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getRemoteAlias() - Not allowed for non-active user");
                return null;
            }

            AdapterService service = getService();
            if (service == null) return null;
            return service.getRemoteAlias(device);
        }

        public boolean setRemoteAlias(BluetoothDevice device, String name) {
        	Log.e(TAG,"setRemoteAlias");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setRemoteAlias() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setRemoteAlias(device, name);
        }

        public int getRemoteClass(BluetoothDevice device) {
        	Log.e(TAG,"getRemoteClass");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getRemoteClass() - Not allowed for non-active user");
                return 0;
            }

            AdapterService service = getService();
            if (service == null) return 0;
            return service.getRemoteClass(device);
        }

        public ParcelUuid[] getRemoteUuids(BluetoothDevice device) {
        	Log.e(TAG,"getRemoteUuids");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "getRemoteUuids() - Not allowed for non-active user");
                return new ParcelUuid[0];
            }

            AdapterService service = getService();
            if (service == null) return new ParcelUuid[0];
            return service.getRemoteUuids(device);
        }

        public boolean fetchRemoteUuids(BluetoothDevice device) {
        Log.e(TAG,"fetchRemoteUuids");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "fetchRemoteUuids() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.fetchRemoteUuids(device);
        }



        public boolean setPin(BluetoothDevice device, boolean accept, int len, byte[] pinCode) {
        Log.e(TAG,"setPin");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setPin() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setPin(device, accept, len, pinCode);
        }

        public boolean setPasskey(BluetoothDevice device, boolean accept, int len, byte[] passkey) {
        Log.e(TAG,"setPasskey");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setPasskey() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setPasskey(device, accept, len, passkey);
        }

        public boolean setPairingConfirmation(BluetoothDevice device, boolean accept) {
        Log.e(TAG,"setPairingConfirmation");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setPairingConfirmation() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setPairingConfirmation(device, accept);
        }

        public int getPhonebookAccessPermission(BluetoothDevice device) {
        Log.e(TAG,"getPhonebookAccessPermission");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "getPhonebookAccessPermission() - Not allowed for non-active user");
                return BluetoothDevice.ACCESS_UNKNOWN;
            }

            AdapterService service = getService();
            if (service == null) return BluetoothDevice.ACCESS_UNKNOWN;
            return service.getPhonebookAccessPermission(device);
        }

        public boolean setPhonebookAccessPermission(BluetoothDevice device, int value) {
        Log.e(TAG,"setPhonebookAccessPermission");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setPhonebookAccessPermission() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setPhonebookAccessPermission(device, value);
        }

        public int getMessageAccessPermission(BluetoothDevice device) {
        Log.e(TAG,"getMessageAccessPermission");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "getMessageAccessPermission() - Not allowed for non-active user");
                return BluetoothDevice.ACCESS_UNKNOWN;
            }

            AdapterService service = getService();
            if (service == null) return BluetoothDevice.ACCESS_UNKNOWN;
            return service.getMessageAccessPermission(device);
        }

        public boolean setMessageAccessPermission(BluetoothDevice device, int value) {
        Log.e(TAG,"setMessageAccessPermission");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setMessageAccessPermission() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setMessageAccessPermission(device, value);
        }

        public int getSimAccessPermission(BluetoothDevice device) {
        Log.e(TAG,"getSimAccessPermission");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "getSimAccessPermission() - Not allowed for non-active user");
                return BluetoothDevice.ACCESS_UNKNOWN;
            }

            AdapterService service = getService();
            if (service == null) return BluetoothDevice.ACCESS_UNKNOWN;
            return service.getSimAccessPermission(device);
        }

        public boolean setSimAccessPermission(BluetoothDevice device, int value) {
        Log.e(TAG,"setSimAccessPermission");
            if (!Utils.checkCaller()) {
                Log.w(TAG, "setSimAccessPermission() - Not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.setSimAccessPermission(device, value);
        }

        public void sendConnectionStateChange(BluetoothDevice
                device, int profile, int state, int prevState) {
                
        Log.e(TAG,"sendConnectionStateChange");
            AdapterService service = getService();
            if (service == null) return;
            service.sendConnectionStateChange(device, profile, state, prevState);
        }

        public ParcelFileDescriptor connectSocket(BluetoothDevice device, int type,
                                                  ParcelUuid uuid, int port, int flag) {
                                                  Log.e(TAG,"connectSocket");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "connectSocket() - Not allowed for non-active user");
                return null;
            }

            AdapterService service = getService();
            if (service == null) return null;
            return service.connectSocket(device, type, uuid, port, flag);
        }

        public ParcelFileDescriptor createSocketChannel(int type, String serviceName,
                                                        ParcelUuid uuid, int port, int flag) {
                                                        Log.e(TAG,"createSocketChannel");
            if (!Utils.checkCallerAllowManagedProfiles(mService)) {
                Log.w(TAG, "createSocketChannel() - Not allowed for non-active user");
                return null;
            }

            AdapterService service = getService();
            if (service == null) return null;
            return service.createSocketChannel(type, serviceName, uuid, port, flag);
        }
        public boolean sdpSearch(BluetoothDevice device, ParcelUuid uuid) {
        Log.e(TAG,"sdpSearch");
            if (!Utils.checkCaller()) {
                Log.w(TAG,"sdpSea(): not allowed for non-active user");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.sdpSearch(device,uuid);
        }

        public boolean configHciSnoopLog(boolean enable) {
        Log.e(TAG,"configHciSnoopLog");
            if (Binder.getCallingUid() != Process.SYSTEM_UID) {
                EventLog.writeEvent(0x534e4554 /* SNET */, "Bluetooth", Binder.getCallingUid(),
                        "configHciSnoopLog() - Not allowed for non-active user b/18643224");
                return false;
            }

            AdapterService service = getService();
            if (service == null) return false;
            return service.configHciSnoopLog(enable);
        }

        public boolean factoryReset() {
        Log.e(TAG,"factoryReset");
            AdapterService service = getService();
            if (service == null) return false;
            service.disable();
            return service.factoryReset();

        }

        public void registerCallback(IBluetoothCallback cb) {
        Log.e(TAG,"registerCallback");
            AdapterService service = getService();
            if (service == null) return ;
            service.registerCallback(cb);
         }

         public void unregisterCallback(IBluetoothCallback cb) {
         Log.e(TAG,"unregisterCallback");
             AdapterService service = getService();
             if (service == null) return ;
             service.unregisterCallback(cb);
         }

         public boolean isMultiAdvertisementSupported() {
         Log.e(TAG,"isMultiAdvertisementSupported");
             AdapterService service = getService();
             if (service == null) return false;
             return service.isMultiAdvertisementSupported();
         }

         public boolean isPeripheralModeSupported() {
         Log.e(TAG,"isPeripheralModeSupported");
             AdapterService service = getService();
             if (service == null) return false;
             return service.isPeripheralModeSupported();
         }

         public boolean isOffloadedFilteringSupported() {
         Log.e(TAG,"isOffloadedFilteringSupported");
             AdapterService service = getService();
             if (service == null) return false;
             int val = service.getNumOfOffloadedScanFilterSupported();
             return (val >= MIN_OFFLOADED_FILTERS);
         }

         public boolean isOffloadedScanBatchingSupported() {
         Log.e(TAG,"isOffloadedScanBatchingSupported");
             AdapterService service = getService();
             if (service == null) return false;
             int val = service.getOffloadedScanResultStorage();
             return (val >= MIN_OFFLOADED_SCAN_STORAGE_BYTES);
         }

         public boolean isActivityAndEnergyReportingSupported() {
         Log.e(TAG,"isActivityAndEnergyReportingSupported");
             AdapterService service = getService();
             if (service == null) return false;
             return service.isActivityAndEnergyReportingSupported();
         }

         public BluetoothActivityEnergyInfo reportActivityInfo() {
         Log.e(TAG,"reportActivityInfo");
             AdapterService service = getService();
             if (service == null) return null;
             return service.reportActivityInfo();
         }

         public void requestActivityInfo(ResultReceiver result) {
         Log.e(TAG,"requestActivityInfo");
             Bundle bundle = new Bundle();
             bundle.putParcelable(BatteryStats.RESULT_RECEIVER_CONTROLLER_KEY,
                     reportActivityInfo());
             result.send(0, bundle);
         }

        public void onLeServiceUp(){
        Log.e(TAG,"onLeServiceUp");
             AdapterService service = getService();
             if (service == null) return;
             service.onLeServiceUp();
         }

         public void onBrEdrDown(){
         Log.e(TAG,"onBrEdrDown");
             AdapterService service = getService();
             if (service == null) return;
             service.onBrEdrDown();
         }

         public void dump(FileDescriptor fd, String[] args) {
         Log.e(TAG,"dump");
            PrintWriter writer = new PrintWriter(new FileOutputStream(fd));
            AdapterService service = getService();
            if (service == null) return;
            service.dump(fd, writer, args);
         }
    };

    // ----API Methods--------

     boolean isEnabled() {
     	Log.e(TAG,"isEnabled");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getState() == BluetoothAdapter.STATE_ON;
     }

     int getState() {
     Log.e(TAG,"getState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (mAdapterProperties != null) return mAdapterProperties.getState();
        return  BluetoothAdapter.STATE_OFF;
     }

     boolean enable() {
     
        return enable (false);
     }

      public boolean enableNoAutoConnect() {
         return enable (true);
     }

     public synchronized boolean enable(boolean quietMode) {
     	Log.e(TAG,"enable");
         enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");

         debugLog("enable() - Enable called with quiet mode status =  " + mQuietmode);
         mQuietmode = quietMode;
         Message m = mAdapterStateMachine.obtainMessage(AdapterState.BLE_TURN_ON);
         mAdapterStateMachine.sendMessage(m);
         mBluetoothStartTime = System.currentTimeMillis();
         return true;
     }

     boolean disable() {
     Log.e(TAG,"disable");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");

        debugLog("disable() called...");
        Message m = mAdapterStateMachine.obtainMessage(AdapterState.BLE_TURN_OFF);
        mAdapterStateMachine.sendMessage(m);
        return true;
    }

     String getAddress() {
     	Log.e(TAG,"getAddress");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        String addrString = null;
        byte[] address = mAdapterProperties.getAddress();
        return Utils.getAddressStringFromByte(address);
    }

     ParcelUuid[] getUuids() {
     	Log.e(TAG,"getUuids");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        return mAdapterProperties.getUuids();
    }

     String getName() {
     	Log.e(TAG,"getName");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM,
                                       "Need BLUETOOTH permission");

        try {
            return mAdapterProperties.getName();
        } catch (Throwable t) {
            debugLog("getName() - Unexpected exception (" + t + ")");
        }
        return null;
    }

     boolean setName(String name) {
     Log.e(TAG,"setName");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");

        return mAdapterProperties.setName(name);
    }

     int getScanMode() {
     	Log.e(TAG,"getScanMode");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        return mAdapterProperties.getScanMode();
    }

     boolean setScanMode(int mode, int duration) {
     Log.e(TAG,"setScanMode");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        setDiscoverableTimeout(duration);

        int newMode = convertScanModeToHal(mode);
        return mAdapterProperties.setScanMode(newMode);
    }

     int getDiscoverableTimeout() {
     	Log.e(TAG,"getDiscoverableTimeout");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        return mAdapterProperties.getDiscoverableTimeout();
    }

     boolean setDiscoverableTimeout(int timeout) {
     	Log.e(TAG,"setDiscoverableTimeout");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        return mAdapterProperties.setDiscoverableTimeout(timeout);
    }

     boolean startDiscovery() {
     	Log.e(TAG,"startDiscovery");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");

        return startDiscoveryNative();
    }

     boolean cancelDiscovery() {
     	Log.e(TAG,"cancelDiscovery");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");

        return cancelDiscoveryNative();
    }

     boolean isDiscovering() {
     	Log.e(TAG,"isDiscovering");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        return mAdapterProperties.isDiscovering();
    }

     BluetoothDevice[] getBondedDevices() {
     	Log.e(TAG,"getBondedDevices");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getBondedDevices();
    }

     int getAdapterConnectionState() {
     	Log.e(TAG,"getAdapterConnectionState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getConnectionState();
    }

     int getProfileConnectionState(int profile) {
     	Log.e(TAG,"getProfileConnectionState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");

        return mAdapterProperties.getProfileConnectionState(profile);
    }
     boolean sdpSearch(BluetoothDevice device,ParcelUuid uuid) {
     	Log.e(TAG,"sdpSearch");
         enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
         if(mSdpManager != null) {
             mSdpManager.sdpSearch(device,uuid);
             return true;
         } else {
             return false;
         }
     }

     boolean createBond(BluetoothDevice device, int transport, OobData oobData) {
     	Log.e(TAG,"createBond");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
            "Need BLUETOOTH ADMIN permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp != null && deviceProp.getBondState() != BluetoothDevice.BOND_NONE) {
            return false;
        }

        // Pairing is unreliable while scanning, so cancel discovery
        // Note, remove this when native stack improves
        cancelDiscoveryNative();

        Message msg = mBondStateMachine.obtainMessage(BondStateMachine.CREATE_BOND);
        msg.obj = device;
        msg.arg1 = transport;

        if (oobData != null) {
            Bundle oobDataBundle = new Bundle();
            oobDataBundle.putParcelable(BondStateMachine.OOBDATA, oobData);
            msg.setData(oobDataBundle);
        }
        mBondStateMachine.sendMessage(msg);
        return true;
    }

      public boolean isQuietModeEnabled() {
      	Log.e(TAG,"isQuietModeEnabled");
          debugLog("isQuetModeEnabled() - Enabled = " + mQuietmode);
          return mQuietmode;
     }

     public void autoConnect(){
     	Log.e(TAG,"autoConnect");
        if (getState() != BluetoothAdapter.STATE_ON){
             errorLog("autoConnect() - BT is not ON. Exiting autoConnect");
             return;
         }
         if (isQuietModeEnabled() == false) {
             debugLog( "autoConnect() - Initiate auto connection on BT on...");
             // Phone profiles.
             autoConnectHeadset();
             autoConnectA2dp();

             // Car Kitt profiles.
             autoConnectHeadsetClient();
             autoConnectA2dpSink();
             autoConnectPbapClient();
         }
         else {
             debugLog( "autoConnect() - BT is in quiet mode. Not initiating auto connections");
         }
    }

    public void updateUuids() {
    	Log.e(TAG,"updateUuids");
        debugLog( "updateUuids() - Updating UUIDs for bonded devices");
        BluetoothDevice[] bondedDevices = getBondedDevices();
        if (bondedDevices == null) return;

        for (BluetoothDevice device : bondedDevices) {
            mRemoteDevices.updateUuids(device);
        }
    }

     private void autoConnectHeadset(){
     	Log.e(TAG,"autoConnectHeadset");
        HeadsetService  hsService = HeadsetService.getHeadsetService();

        BluetoothDevice bondedDevices[] = getBondedDevices();
        if ((bondedDevices == null) ||(hsService == null)) {
            return;
        }
        for (BluetoothDevice device : bondedDevices) {
            if (hsService.getPriority(device) == BluetoothProfile.PRIORITY_AUTO_CONNECT ){
                debugLog("autoConnectHeadset() - Connecting HFP with " + device.toString());
                hsService.connect(device);
            }
        }
    }

     private void autoConnectA2dp(){
     	Log.e(TAG,"autoConnectA2dp");
        A2dpService a2dpSservice = A2dpService.getA2dpService();
        BluetoothDevice bondedDevices[] = getBondedDevices();
        if ((bondedDevices == null) ||(a2dpSservice == null)) {
            return;
        }
        for (BluetoothDevice device : bondedDevices) {
            if (a2dpSservice.getPriority(device) == BluetoothProfile.PRIORITY_AUTO_CONNECT ){
                debugLog("autoConnectA2dp() - Connecting A2DP with " + device.toString());
                a2dpSservice.connect(device);
            }
        }
    }

    private void autoConnectHeadsetClient() {
    	Log.e(TAG,"autoConnectHeadsetClient");
        HeadsetClientService headsetClientService = HeadsetClientService.getHeadsetClientService();
        BluetoothDevice bondedDevices[] = getBondedDevices();
        if ((bondedDevices == null) || (headsetClientService == null)) {
            return;
        }

        for (BluetoothDevice device : bondedDevices) {
            if (headsetClientService.getPriority(device) == BluetoothProfile.PRIORITY_AUTO_CONNECT){
            debugLog("autoConnectHeadsetClient() - Connecting Headset Client with " +
                device.toString());
            headsetClientService.connect(device);
            }
        }
    }

    private void autoConnectA2dpSink() {
    	Log.e(TAG,"autoConnectA2dpSink");
         A2dpSinkService a2dpSinkService = A2dpSinkService.getA2dpSinkService();
         BluetoothDevice bondedDevices[] = getBondedDevices();
         if ((bondedDevices == null) || (a2dpSinkService == null)) {
             return;
         }

         for (BluetoothDevice device : bondedDevices) {
             if (a2dpSinkService.getPriority(device) == BluetoothProfile.PRIORITY_AUTO_CONNECT) {
                 debugLog("autoConnectA2dpSink() - Connecting A2DP Sink with " + device.toString());
                 a2dpSinkService.connect(device);
             }
         }
     }

     private void autoConnectPbapClient(){
     	Log.e(TAG,"autoConnectPbapClient");
         PbapClientService pbapClientService = PbapClientService.getPbapClientService();
         BluetoothDevice bondedDevices[] = getBondedDevices();
         if ((bondedDevices == null) || (pbapClientService == null)) {
             return;
         }
         for (BluetoothDevice device : bondedDevices) {
             if (pbapClientService.getPriority(device) == BluetoothProfile.PRIORITY_AUTO_CONNECT) {
                 debugLog("autoConnectPbapClient() - Connecting PBAP Client with " +
                         device.toString());
                 pbapClientService.connect(device);
             }
         }
    }


     public void connectOtherProfile(BluetoothDevice device, int firstProfileStatus){
     Log.e(TAG,"connectOtherProfile");
        if ((mHandler.hasMessages(MESSAGE_CONNECT_OTHER_PROFILES) == false) &&
            (isQuietModeEnabled()== false)){
            Message m = mHandler.obtainMessage(MESSAGE_CONNECT_OTHER_PROFILES);
            m.obj = device;
            m.arg1 = (int)firstProfileStatus;
            mHandler.sendMessageDelayed(m,CONNECT_OTHER_PROFILES_TIMEOUT);
        }
    }

     private void processConnectOtherProfiles (BluetoothDevice device, int firstProfileStatus){
     Log.e(TAG,"processConnectOtherProfiles");
        if (getState()!= BluetoothAdapter.STATE_ON){
            return;
        }
        HeadsetService  hsService = HeadsetService.getHeadsetService();
        A2dpService a2dpService = A2dpService.getA2dpService();

        // if any of the profile service is  null, second profile connection not required
        if ((hsService == null) ||(a2dpService == null )){
            return;
        }
        List<BluetoothDevice> a2dpConnDevList= a2dpService.getConnectedDevices();
        List<BluetoothDevice> hfConnDevList= hsService.getConnectedDevices();
        // Check if the device is in disconnected state and if so return
        // We ned to connect other profile only if one of the profile is still in connected state
        // This is required to avoide a race condition in which profiles would
        // automaticlly connect if the disconnection is initiated within 6 seconds of connection
        //First profile connection being rejected is an exception
        if((hfConnDevList.isEmpty() && a2dpConnDevList.isEmpty())&&
            (PROFILE_CONN_CONNECTED  == firstProfileStatus)){
            return;
        }
        if((hfConnDevList.isEmpty()) &&
            (hsService.getPriority(device) >= BluetoothProfile.PRIORITY_ON)){
            hsService.connect(device);
        }
        else if((a2dpConnDevList.isEmpty()) &&
            (a2dpService.getPriority(device) >= BluetoothProfile.PRIORITY_ON)){
            a2dpService.connect(device);
        }
    }

     private void adjustOtherHeadsetPriorities(HeadsetService  hsService,
             List<BluetoothDevice> connectedDeviceList) {
             Log.e(TAG,"adjustOtherHeadsetPriorities");
        for (BluetoothDevice device : getBondedDevices()) {
           if (hsService.getPriority(device) >= BluetoothProfile.PRIORITY_AUTO_CONNECT &&
               !connectedDeviceList.contains(device)) {
               hsService.setPriority(device, BluetoothProfile.PRIORITY_ON);
           }
        }
     }

    private void adjustOtherSinkPriorities(A2dpService a2dpService,
             BluetoothDevice connectedDevice) {
             Log.e(TAG,"adjustOtherSinkPriorities");
         for (BluetoothDevice device : getBondedDevices()) {
             if (a2dpService.getPriority(device) >= BluetoothProfile.PRIORITY_AUTO_CONNECT &&
                 !device.equals(connectedDevice)) {
                 a2dpService.setPriority(device, BluetoothProfile.PRIORITY_ON);
             }
         }
     }

    private void adjustOtherHeadsetClientPriorities(HeadsetClientService hsService,
            BluetoothDevice connectedDevice) {
            Log.e(TAG,"adjustOtherHeadsetClientPriorities");
        for (BluetoothDevice device : getBondedDevices()) {
           if (hsService.getPriority(device) >= BluetoothProfile.PRIORITY_AUTO_CONNECT &&
               !device.equals(connectedDevice)) {
               hsService.setPriority(device, BluetoothProfile.PRIORITY_ON);
           }
        }
     }

    private void adjustOtherA2dpSinkPriorities(A2dpSinkService a2dpService,
            BluetoothDevice connectedDevice) {
            Log.e(TAG,"adjustOtherA2dpSinkPriorities");
        for (BluetoothDevice device : getBondedDevices()) {
            if (a2dpService.getPriority(device) >= BluetoothProfile.PRIORITY_AUTO_CONNECT &&
                !device.equals(connectedDevice)) {
                a2dpService.setPriority(device, BluetoothProfile.PRIORITY_ON);
            }
        }
    }

    private void adjustOtherPbapClientPriorities(PbapClientService pbapService,
            BluetoothDevice connectedDevice) {
            Log.e(TAG,"adjustOtherPbapClientPriorities");
        for (BluetoothDevice device : getBondedDevices()) {
           if (pbapService.getPriority(device) >= BluetoothProfile.PRIORITY_AUTO_CONNECT &&
               !device.equals(connectedDevice)) {
               pbapService.setPriority(device, BluetoothProfile.PRIORITY_ON);
           }
        }
     }

    void setProfileAutoConnectionPriority (BluetoothDevice device, int profileId){
    Log.e(TAG,"setProfileAutoConnectionPriority profileId:"+profileId);
        switch (profileId) {
            case BluetoothProfile.HEADSET:
                HeadsetService  hsService = HeadsetService.getHeadsetService();
                List<BluetoothDevice> deviceList = hsService.getConnectedDevices();
                if ((hsService != null) &&
                   (BluetoothProfile.PRIORITY_AUTO_CONNECT != hsService.getPriority(device))) {
                    adjustOtherHeadsetPriorities(hsService, deviceList);
                    hsService.setPriority(device,BluetoothProfile.PRIORITY_AUTO_CONNECT);
                }
                break;

            case BluetoothProfile.A2DP:
                A2dpService a2dpService = A2dpService.getA2dpService();
                if ((a2dpService != null) && (BluetoothProfile.PRIORITY_AUTO_CONNECT !=
                        a2dpService.getPriority(device))) {
                    adjustOtherSinkPriorities(a2dpService, device);
                    a2dpService.setPriority(device,BluetoothProfile.PRIORITY_AUTO_CONNECT);
                }
                break;

           case BluetoothProfile.A2DP_SINK:
                A2dpSinkService a2dpSinkService = A2dpSinkService.getA2dpSinkService();
                if ((a2dpSinkService != null) && (BluetoothProfile.PRIORITY_AUTO_CONNECT !=
                        a2dpSinkService.getPriority(device))) {
                    adjustOtherA2dpSinkPriorities(a2dpSinkService, device);
                    a2dpSinkService.setPriority(device,BluetoothProfile.PRIORITY_AUTO_CONNECT);
                }
                break;

            case BluetoothProfile.HEADSET_CLIENT:
                HeadsetClientService headsetClientService =
                        HeadsetClientService.getHeadsetClientService();
                if ((headsetClientService != null) && (BluetoothProfile.PRIORITY_AUTO_CONNECT !=
                        headsetClientService.getPriority(device))) {
                    adjustOtherHeadsetClientPriorities(headsetClientService, device);
                    headsetClientService.setPriority(device,BluetoothProfile.PRIORITY_AUTO_CONNECT);
                }
                break;

            case BluetoothProfile.PBAP_CLIENT:
                PbapClientService pbapClientService = PbapClientService.getPbapClientService();
                if ((pbapClientService != null) && (BluetoothProfile.PRIORITY_AUTO_CONNECT !=
                        pbapClientService.getPriority(device))) {
                    adjustOtherPbapClientPriorities(pbapClientService, device);
                    pbapClientService.setPriority(device,BluetoothProfile.PRIORITY_AUTO_CONNECT);
                }
                break;

            default:
                Log.w(TAG, "Attempting to set Auto Connect priority on invalid profile");
                break;
         }
    }

     boolean cancelBondProcess(BluetoothDevice device) {
     	Log.e(TAG,"cancelBondProcess");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");
        byte[] addr = Utils.getBytesFromAddress(device.getAddress());
        return cancelBondNative(addr);
    }

     boolean removeBond(BluetoothDevice device) {
     	Log.e(TAG,"removeBond");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM, "Need BLUETOOTH ADMIN permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null || deviceProp.getBondState() != BluetoothDevice.BOND_BONDED) {
            return false;
        }
        Message msg = mBondStateMachine.obtainMessage(BondStateMachine.REMOVE_BOND);
        msg.obj = device;
        mBondStateMachine.sendMessage(msg);
        return true;
    }

    int getBondState(BluetoothDevice device) {
    	Log.e(TAG,"getBondState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) {
            return BluetoothDevice.BOND_NONE;
        }
        return deviceProp.getBondState();
    }

    int getConnectionState(BluetoothDevice device) {
    Log.e(TAG,"getConnectionState");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        byte[] addr = Utils.getBytesFromAddress(device.getAddress());
        return getConnectionStateNative(addr);
    }

     String getRemoteName(BluetoothDevice device) {
     Log.e(TAG,"getRemoteName");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        if (mRemoteDevices == null) return null;
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) return null;
        return deviceProp.getName();
    }

     int getRemoteType(BluetoothDevice device) {
     Log.e(TAG,"getRemoteType");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) return BluetoothDevice.DEVICE_TYPE_UNKNOWN;
        return deviceProp.getDeviceType();
    }

     String getRemoteAlias(BluetoothDevice device) {
     Log.e(TAG,"getRemoteAlias");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) return null;
        return deviceProp.getAlias();
    }

     boolean setRemoteAlias(BluetoothDevice device, String name) {
     Log.e(TAG,"setRemoteAlias");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) return false;
        deviceProp.setAlias(device, name);
        return true;
    }

     int getRemoteClass(BluetoothDevice device) {
     Log.e(TAG,"getRemoteClass");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) return 0;

        return deviceProp.getBluetoothClass();
    }

     ParcelUuid[] getRemoteUuids(BluetoothDevice device) {
     Log.e(TAG,"getRemoteUuids");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null) return null;
        return deviceProp.getUuids();
    }

     boolean fetchRemoteUuids(BluetoothDevice device) {
     Log.e(TAG,"fetchRemoteUuids");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        mRemoteDevices.fetchUuids(device);
        return true;
    }


     boolean setPin(BluetoothDevice device, boolean accept, int len, byte[] pinCode) {
     Log.e(TAG,"setPin");
        enforceCallingOrSelfPermission(BLUETOOTH_ADMIN_PERM,
                                       "Need BLUETOOTH ADMIN permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        // Only allow setting a pin in bonding state, or bonded state in case of security upgrade.
        if (deviceProp == null ||
            (deviceProp.getBondState() != BluetoothDevice.BOND_BONDING &&
             deviceProp.getBondState() != BluetoothDevice.BOND_BONDED)) {
            return false;
        }

        byte[] addr = Utils.getBytesFromAddress(device.getAddress());
        return pinReplyNative(addr, accept, len, pinCode);
    }

     boolean setPasskey(BluetoothDevice device, boolean accept, int len, byte[] passkey) {
     Log.e(TAG,"setPasskey");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null || deviceProp.getBondState() != BluetoothDevice.BOND_BONDING) {
            return false;
        }

        byte[] addr = Utils.getBytesFromAddress(device.getAddress());
        return sspReplyNative(addr, AbstractionLayer.BT_SSP_VARIANT_PASSKEY_ENTRY, accept,
                Utils.byteArrayToInt(passkey));
    }

     boolean setPairingConfirmation(BluetoothDevice device, boolean accept) {
     Log.e(TAG,"setPairingConfirmation");
        enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED,
                                       "Need BLUETOOTH PRIVILEGED permission");
        DeviceProperties deviceProp = mRemoteDevices.getDeviceProperties(device);
        if (deviceProp == null || deviceProp.getBondState() != BluetoothDevice.BOND_BONDING) {
            return false;
        }

        byte[] addr = Utils.getBytesFromAddress(device.getAddress());
        return sspReplyNative(addr, AbstractionLayer.BT_SSP_VARIANT_PASSKEY_CONFIRMATION,
                accept, 0);
    }

    int getPhonebookAccessPermission(BluetoothDevice device) {
    Log.e(TAG,"getPhonebookAccessPermission");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        SharedPreferences pref = getSharedPreferences(PHONEBOOK_ACCESS_PERMISSION_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        if (!pref.contains(device.getAddress())) {
            return BluetoothDevice.ACCESS_UNKNOWN;
        }
        return pref.getBoolean(device.getAddress(), false)
                ? BluetoothDevice.ACCESS_ALLOWED : BluetoothDevice.ACCESS_REJECTED;
    }

    boolean setPhonebookAccessPermission(BluetoothDevice device, int value) {
    Log.e(TAG,"setPhonebookAccessPermission");
        enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED,
                                       "Need BLUETOOTH PRIVILEGED permission");
        SharedPreferences pref = getSharedPreferences(PHONEBOOK_ACCESS_PERMISSION_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (value == BluetoothDevice.ACCESS_UNKNOWN) {
            editor.remove(device.getAddress());
        } else {
            editor.putBoolean(device.getAddress(), value == BluetoothDevice.ACCESS_ALLOWED);
        }
        return editor.commit();
    }

    int getMessageAccessPermission(BluetoothDevice device) {
    Log.e(TAG,"getMessageAccessPermission");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        SharedPreferences pref = getSharedPreferences(MESSAGE_ACCESS_PERMISSION_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        if (!pref.contains(device.getAddress())) {
            return BluetoothDevice.ACCESS_UNKNOWN;
        }
        return pref.getBoolean(device.getAddress(), false)
                ? BluetoothDevice.ACCESS_ALLOWED : BluetoothDevice.ACCESS_REJECTED;
    }

    boolean setMessageAccessPermission(BluetoothDevice device, int value) {
    Log.e(TAG,"setMessageAccessPermission");
        enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED,
                                       "Need BLUETOOTH PRIVILEGED permission");
        SharedPreferences pref = getSharedPreferences(MESSAGE_ACCESS_PERMISSION_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (value == BluetoothDevice.ACCESS_UNKNOWN) {
            editor.remove(device.getAddress());
        } else {
            editor.putBoolean(device.getAddress(), value == BluetoothDevice.ACCESS_ALLOWED);
        }
        return editor.commit();
    }

    int getSimAccessPermission(BluetoothDevice device) {
    Log.e(TAG,"getSimAccessPermission");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        SharedPreferences pref = getSharedPreferences(SIM_ACCESS_PERMISSION_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        if (!pref.contains(device.getAddress())) {
            return BluetoothDevice.ACCESS_UNKNOWN;
        }
        return pref.getBoolean(device.getAddress(), false)
                ? BluetoothDevice.ACCESS_ALLOWED : BluetoothDevice.ACCESS_REJECTED;
    }

    boolean setSimAccessPermission(BluetoothDevice device, int value) {
    Log.e(TAG,"setSimAccessPermission");
        enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED,
                                       "Need BLUETOOTH PRIVILEGED permission");
        SharedPreferences pref = getSharedPreferences(SIM_ACCESS_PERMISSION_PREFERENCE_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        if (value == BluetoothDevice.ACCESS_UNKNOWN) {
            editor.remove(device.getAddress());
        } else {
            editor.putBoolean(device.getAddress(), value == BluetoothDevice.ACCESS_ALLOWED);
        }
        return editor.commit();
    }

     void sendConnectionStateChange(BluetoothDevice
            device, int profile, int state, int prevState) {
            Log.e(TAG,"sendConnectionStateChange");
        // TODO(BT) permission check?
        // Since this is a binder call check if Bluetooth is on still
        if (getState() == BluetoothAdapter.STATE_OFF) return;

        mAdapterProperties.sendConnectionStateChange(device, profile, state, prevState);

    }

     ParcelFileDescriptor connectSocket(BluetoothDevice device, int type,
                                              ParcelUuid uuid, int port, int flag) {
                                              Log.e(TAG,"connectSocket 5");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int fd = connectSocketNative(Utils.getBytesFromAddress(device.getAddress()),
                   type, Utils.uuidToByteArray(uuid), port, flag, Binder.getCallingUid());
        if (fd < 0) {
            errorLog("Failed to connect socket");
            return null;
        }
        return ParcelFileDescriptor.adoptFd(fd);
    }

     ParcelFileDescriptor createSocketChannel(int type, String serviceName,
                                                    ParcelUuid uuid, int port, int flag) {
                                                    Log.e(TAG,"createSocketChannel 5");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        int fd =  createSocketChannelNative(type, serviceName,
                                 Utils.uuidToByteArray(uuid), port, flag, Binder.getCallingUid());
        if (fd < 0) {
            errorLog("Failed to create socket channel");
            return null;
        }
        return ParcelFileDescriptor.adoptFd(fd);
    }

    boolean configHciSnoopLog(boolean enable) {
    	Log.e(TAG,"configHciSnoopLog");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return configHciSnoopLogNative(enable);
    }

    boolean factoryReset() {
    	Log.e(TAG,"factoryReset");
        enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED, "Need BLUETOOTH permission");
        return factoryResetNative();
    }

     void registerCallback(IBluetoothCallback cb) {
         mCallbacks.register(cb);
      }

      void unregisterCallback(IBluetoothCallback cb) {
         mCallbacks.unregister(cb);
      }

    public int getNumOfAdvertisementInstancesSupported() {
    	Log.e(TAG,"getNumOfAdvertisementInstancesSupported");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getNumOfAdvertisementInstancesSupported();
    }

    public boolean isMultiAdvertisementSupported() {
    	Log.e(TAG,"isMultiAdvertisementSupported");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return getNumOfAdvertisementInstancesSupported() >= MIN_ADVT_INSTANCES_FOR_MA;
    }

    public boolean isRpaOffloadSupported() {
    	Log.e(TAG,"isRpaOffloadSupported");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.isRpaOffloadSupported();
    }

    public int getNumOfOffloadedIrkSupported() {
    	Log.e(TAG,"getNumOfOffloadedIrkSupported");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getNumOfOffloadedIrkSupported();
    }

    public int getNumOfOffloadedScanFilterSupported() {
    	Log.e(TAG,"getNumOfOffloadedScanFilterSupported");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getNumOfOffloadedScanFilterSupported();
    }

    public boolean isPeripheralModeSupported() {
    	Log.e(TAG,"isPeripheralModeSupported");
        return getResources().getBoolean(R.bool.config_bluetooth_le_peripheral_mode_supported);
    }

    public int getOffloadedScanResultStorage() {
    	Log.e(TAG,"getOffloadedScanResultStorage");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getOffloadedScanResultStorage();
    }

    private boolean isActivityAndEnergyReportingSupported() {
    	Log.e(TAG,"isActivityAndEnergyReportingSupported");
          enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED, "Need BLUETOOTH permission");
          return mAdapterProperties.isActivityAndEnergyReportingSupported();
    }

    private BluetoothActivityEnergyInfo reportActivityInfo() {
    	Log.e(TAG,"reportActivityInfo");
        enforceCallingOrSelfPermission(BLUETOOTH_PRIVILEGED, "Need BLUETOOTH permission");
        if (mAdapterProperties.getState() != BluetoothAdapter.STATE_ON ||
                !mAdapterProperties.isActivityAndEnergyReportingSupported()) {
            return null;
        }

        // Pull the data. The callback will notify mEnergyInfoLock.
        readEnergyInfo();

        synchronized (mEnergyInfoLock) {
            try {
                mEnergyInfoLock.wait(CONTROLLER_ENERGY_UPDATE_TIMEOUT_MILLIS);
            } catch (InterruptedException e) {
                // Just continue, the energy data may be stale but we won't miss anything next time
                // we query.
            }

            final BluetoothActivityEnergyInfo info = new BluetoothActivityEnergyInfo(
                    SystemClock.elapsedRealtime(),
                    mStackReportedState,
                    mTxTimeTotalMs, mRxTimeTotalMs, mIdleTimeTotalMs,
                    mEnergyUsedTotalVoltAmpSecMicro);

            // Count the number of entries that have byte counts > 0
            int arrayLen = 0;
            for (int i = 0; i < mUidTraffic.size(); i++) {
                final UidTraffic traffic = mUidTraffic.valueAt(i);
                if (traffic.getTxBytes() != 0 || traffic.getRxBytes() != 0) {
                    arrayLen++;
                }
            }

            // Copy the traffic objects whose byte counts are > 0 and reset the originals.
            final UidTraffic[] result = arrayLen > 0 ? new UidTraffic[arrayLen] : null;
            int putIdx = 0;
            for (int i = 0; i < mUidTraffic.size(); i++) {
                final UidTraffic traffic = mUidTraffic.valueAt(i);
                if (traffic.getTxBytes() != 0 || traffic.getRxBytes() != 0) {
                    result[putIdx++] = traffic.clone();
                    traffic.setRxBytes(0);
                    traffic.setTxBytes(0);
                }
            }

            info.setUidTraffic(result);

            // Read on clear values; a record of data is created with
            // timstamp and new samples are collected until read again
            mStackReportedState = 0;
            mTxTimeTotalMs = 0;
            mRxTimeTotalMs = 0;
            mIdleTimeTotalMs = 0;
            mEnergyUsedTotalVoltAmpSecMicro = 0;
            return info;
        }
    }

    public int getTotalNumOfTrackableAdvertisements() {
    	Log.e(TAG,"getTotalNumOfTrackableAdvertisements");
        enforceCallingOrSelfPermission(BLUETOOTH_PERM, "Need BLUETOOTH permission");
        return mAdapterProperties.getTotalNumOfTrackableAdvertisements();
    }

    public void onLeServiceUp() {
    	Log.e(TAG,"onLeServiceUp");
        Message m = mAdapterStateMachine.obtainMessage(AdapterState.USER_TURN_ON);
        mAdapterStateMachine.sendMessage(m);
    }

    public void onBrEdrDown() {
    	Log.e(TAG,"onBrEdrDown");
        Message m = mAdapterStateMachine.obtainMessage(AdapterState.USER_TURN_OFF);
        mAdapterStateMachine.sendMessage(m);
    }

    private static int convertScanModeToHal(int mode) {
    	Log.e(TAG,"convertScanModeToHal mode:"+mode);
        switch (mode) {
            case BluetoothAdapter.SCAN_MODE_NONE:
                return AbstractionLayer.BT_SCAN_MODE_NONE;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                return AbstractionLayer.BT_SCAN_MODE_CONNECTABLE;
            case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                return AbstractionLayer.BT_SCAN_MODE_CONNECTABLE_DISCOVERABLE;
        }
       // errorLog("Incorrect scan mode in convertScanModeToHal");
        return -1;
    }

    static int convertScanModeFromHal(int mode) {
    	Log.e(TAG,"convertScanModeFromHal mode:"+mode);
        switch (mode) {
            case AbstractionLayer.BT_SCAN_MODE_NONE:
                return BluetoothAdapter.SCAN_MODE_NONE;
            case AbstractionLayer.BT_SCAN_MODE_CONNECTABLE:
                return BluetoothAdapter.SCAN_MODE_CONNECTABLE;
            case AbstractionLayer.BT_SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                return BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE;
        }
        //errorLog("Incorrect scan mode in convertScanModeFromHal");
        return -1;
    }

    // This function is called from JNI. It allows native code to set a single wake
    // alarm. If an alarm is already pending and a new request comes in, the alarm
    // will be rescheduled (i.e. the previously set alarm will be cancelled).
    private boolean setWakeAlarm(long delayMillis, boolean shouldWake) {
    	Log.e(TAG,"setWakeAlarm");
        synchronized (this) {
            if (mPendingAlarm != null) {
                mAlarmManager.cancel(mPendingAlarm);
            }

            long wakeupTime = SystemClock.elapsedRealtime() + delayMillis;
            int type = shouldWake
                ? AlarmManager.ELAPSED_REALTIME_WAKEUP
                : AlarmManager.ELAPSED_REALTIME;

            Intent intent = new Intent(ACTION_ALARM_WAKEUP);
            mPendingAlarm = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            mAlarmManager.setExact(type, wakeupTime, mPendingAlarm);
            return true;
        }
    }

    // This function is called from JNI. It allows native code to acquire a single wake lock.
    // If the wake lock is already held, this function returns success. Although this function
    // only supports acquiring a single wake lock at a time right now, it will eventually be
    // extended to allow acquiring an arbitrary number of wake locks. The current interface
    // takes |lockName| as a parameter in anticipation of that implementation.
    private boolean acquireWakeLock(String lockName) {
    	Log.e(TAG,"acquireWakeLock");
        synchronized (this) {
            if (mWakeLock == null) {
                mWakeLockName = lockName;
                mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, lockName);
            }

            if (!mWakeLock.isHeld())
                mWakeLock.acquire();
        }
        return true;
    }

    // This function is called from JNI. It allows native code to release a wake lock acquired
    // by |acquireWakeLock|. If the wake lock is not held, this function returns failure.
    // Note that the release() call is also invoked by {@link #cleanup()} so a synchronization is
    // needed here. See the comment for |acquireWakeLock| for an explanation of the interface.
    private boolean releaseWakeLock(String lockName) {
    	Log.e(TAG,"releaseWakeLock");
        synchronized (this) {
            if (mWakeLock == null) {
                errorLog("Repeated wake lock release; aborting release: " + lockName);
                return false;
            }

            if (mWakeLock.isHeld())
                mWakeLock.release();
        }
        return true;
    }

    private void energyInfoCallback(int status, int ctrl_state, long tx_time, long rx_time,
                                    long idle_time, long energy_used, UidTraffic[] data)
            throws RemoteException {
            Log.e(TAG,"energyInfoCallback");
        if (ctrl_state >= BluetoothActivityEnergyInfo.BT_STACK_STATE_INVALID &&
                ctrl_state <= BluetoothActivityEnergyInfo.BT_STACK_STATE_STATE_IDLE) {
            // Energy is product of mA, V and ms. If the chipset doesn't
            // report it, we have to compute it from time
            if (energy_used == 0) {
                try {
                    final long txMah = Math.multiplyExact(tx_time, getTxCurrentMa());
                    final long rxMah = Math.multiplyExact(rx_time, getRxCurrentMa());
                    final long idleMah = Math.multiplyExact(idle_time, getIdleCurrentMa());
                    energy_used = (long) (Math.addExact(Math.addExact(txMah, rxMah), idleMah)
                            * getOperatingVolt());
                } catch (ArithmeticException e) {
                    Slog.wtf(TAG, "overflow in bluetooth energy callback", e);
                    // Energy is already 0 if the exception was thrown.
                }
            }

            synchronized (mEnergyInfoLock) {
                mStackReportedState = ctrl_state;
                long totalTxTimeMs;
                long totalRxTimeMs;
                long totalIdleTimeMs;
                long totalEnergy;
                try {
                    totalTxTimeMs = Math.addExact(mTxTimeTotalMs, tx_time);
                    totalRxTimeMs = Math.addExact(mRxTimeTotalMs, rx_time);
                    totalIdleTimeMs = Math.addExact(mIdleTimeTotalMs, idle_time);
                    totalEnergy = Math.addExact(mEnergyUsedTotalVoltAmpSecMicro, energy_used);
                } catch (ArithmeticException e) {
                    // This could be because we accumulated a lot of time, or we got a very strange
                    // value from the controller (more likely). Discard this data.
                    Slog.wtf(TAG, "overflow in bluetooth energy callback", e);
                    totalTxTimeMs = mTxTimeTotalMs;
                    totalRxTimeMs = mRxTimeTotalMs;
                    totalIdleTimeMs = mIdleTimeTotalMs;
                    totalEnergy = mEnergyUsedTotalVoltAmpSecMicro;
                }

                mTxTimeTotalMs = totalTxTimeMs;
                mRxTimeTotalMs = totalRxTimeMs;
                mIdleTimeTotalMs = totalIdleTimeMs;
                mEnergyUsedTotalVoltAmpSecMicro = totalEnergy;

                for (UidTraffic traffic : data) {
                    UidTraffic existingTraffic = mUidTraffic.get(traffic.getUid());
                    if (existingTraffic == null) {
                        mUidTraffic.put(traffic.getUid(), traffic);
                    } else {
                        existingTraffic.addRxBytes(traffic.getRxBytes());
                        existingTraffic.addTxBytes(traffic.getTxBytes());
                    }
                }
                mEnergyInfoLock.notifyAll();
            }
        }

        debugLog("energyInfoCallback() status = " + status +
                "tx_time = " + tx_time + "rx_time = " + rx_time +
                "idle_time = " + idle_time + "energy_used = " + energy_used +
                "ctrl_state = " + ctrl_state +
                "traffic = " + Arrays.toString(data));
    }

    private int getIdleCurrentMa() {
    	Log.e(TAG,"getIdleCurrentMa");
        return getResources().getInteger(R.integer.config_bluetooth_idle_cur_ma);
    }

    private int getTxCurrentMa() {
    	Log.e(TAG,"getTxCurrentMa");
        return getResources().getInteger(R.integer.config_bluetooth_tx_cur_ma);
    }

    private int getRxCurrentMa() {
    	Log.e(TAG,"getRxCurrentMa");
        return getResources().getInteger(R.integer.config_bluetooth_rx_cur_ma);
    }

    private double getOperatingVolt() {
    	Log.e(TAG,"getOperatingVolt");
        return getResources().getInteger(R.integer.config_bluetooth_operating_voltage_mv) / 1000.0;
    }

    private String getStateString() {
    Log.e(TAG,"getStateString");
        int state = getState();
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
                return "STATE_OFF";
            case BluetoothAdapter.STATE_TURNING_ON:
                return "STATE_TURNING_ON";
            case BluetoothAdapter.STATE_ON:
                return "STATE_ON";
            case BluetoothAdapter.STATE_TURNING_OFF:
                return "STATE_TURNING_OFF";
            case BluetoothAdapter.STATE_BLE_TURNING_ON:
                return "STATE_BLE_TURNING_ON";
            case BluetoothAdapter.STATE_BLE_ON:
                return "STATE_BLE_ON";
            case BluetoothAdapter.STATE_BLE_TURNING_OFF:
                return "STATE_BLE_TURNING_OFF";
            default:
                return "UNKNOWN STATE: " + state;
        }
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
    Log.e(TAG,"dump");
        enforceCallingOrSelfPermission(android.Manifest.permission.DUMP, TAG);

        if (args.length > 0) {
            debugLog("dumpsys arguments, check for protobuf output: " +
                    TextUtils.join(" ", args));
            if (args[0].startsWith("--proto")) {
                if (args[0].equals("--proto-java-bin")) {
                    dumpJava(fd);
                } else {
                    dumpNative(fd, args);
                }
                return;
            }
        }

        long onDuration = System.currentTimeMillis() - mBluetoothStartTime;
        String onDurationString = String.format("%02d:%02d:%02d.%03d",
                                      (int)(onDuration / (1000 * 60 * 60)),
                                      (int)((onDuration / (1000 * 60)) % 60),
                                      (int)((onDuration / 1000) % 60),
                                      (int)(onDuration % 1000));

        writer.println("Bluetooth Status");
        writer.println("  enabled: " + isEnabled());
        writer.println("  state: " + getStateString());
        writer.println("  address: " + getAddress());
        writer.println("  name: " + getName());
        writer.println("  time since enabled: " + onDurationString + "\n");

        writer.println("Bonded devices:");
        for (BluetoothDevice device : getBondedDevices()) {
          writer.println("  " + device.getAddress() +
              " [" + DEVICE_TYPE_NAMES[device.getType()] + "] " +
              device.getName());
        }

        // Dump profile information
        StringBuilder sb = new StringBuilder();
        synchronized (mProfiles) {
            for (ProfileService profile : mProfiles) {
                profile.dump(sb);
            }
        }

        writer.write(sb.toString());
        writer.flush();

        dumpNative(fd, args);
    }

    private void dumpJava(FileDescriptor fd) {
    	Log.e(TAG,"dumpJava");
        BluetoothProto.BluetoothLog log = new BluetoothProto.BluetoothLog();

        for (ProfileService profile : mProfiles) {
            profile.dumpProto(log);
        }

        try {
            FileOutputStream protoOut = new FileOutputStream(fd);
            String protoOutString =
                Base64.encodeToString(log.toByteArray(), Base64.DEFAULT);
            protoOut.write(protoOutString.getBytes(StandardCharsets.UTF_8));
            protoOut.close();
        } catch (IOException e) {
            errorLog("Unable to write Java protobuf to file descriptor.");
        }
    }

    private void debugLog(String msg) {
    	Log.e(TAG,"debugLog");
        if (DBG) Log.d(TAG, msg);
    }

    private void errorLog(String msg) {
    	Log.e(TAG,"errorLog");
        Log.e(TAG, msg);
    }

    private final BroadcastReceiver mAlarmBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Log.e(TAG,"mAlarmBroadcastReceiver onReceive");
            synchronized (AdapterService.this) {
                mPendingAlarm = null;
                alarmFiredNative();
            }
        }
    };

    private native static void classInitNative();
    private native boolean initNative();
    private native void cleanupNative();
    /*package*/ native boolean enableNative(boolean startRestricted);
    /*package*/ native boolean disableNative();
    /*package*/ native boolean setAdapterPropertyNative(int type, byte[] val);
    /*package*/ native boolean getAdapterPropertiesNative();
    /*package*/ native boolean getAdapterPropertyNative(int type);
    /*package*/ native boolean setAdapterPropertyNative(int type);
    /*package*/ native boolean
        setDevicePropertyNative(byte[] address, int type, byte[] val);
    /*package*/ native boolean getDevicePropertyNative(byte[] address, int type);

    /*package*/ native boolean createBondNative(byte[] address, int transport);
    /*package*/ native boolean createBondOutOfBandNative(byte[] address, int transport, OobData oobData);
    /*package*/ native boolean removeBondNative(byte[] address);
    /*package*/ native boolean cancelBondNative(byte[] address);
    /*package*/ native boolean sdpSearchNative(byte[] address, byte[] uuid);

    /*package*/ native int getConnectionStateNative(byte[] address);

    private native boolean startDiscoveryNative();
    private native boolean cancelDiscoveryNative();

    private native boolean pinReplyNative(byte[] address, boolean accept, int len, byte[] pin);
    private native boolean sspReplyNative(byte[] address, int type, boolean
            accept, int passkey);

    /*package*/ native boolean getRemoteServicesNative(byte[] address);
    /*package*/ native boolean getRemoteMasInstancesNative(byte[] address);

    private native int readEnergyInfo();
    // TODO(BT) move this to ../btsock dir
    private native int connectSocketNative(byte[] address, int type,
                                           byte[] uuid, int port, int flag, int callingUid);
    private native int createSocketChannelNative(int type, String serviceName,
                                                 byte[] uuid, int port, int flag, int callingUid);

    /*package*/ native boolean configHciSnoopLogNative(boolean enable);
    /*package*/ native boolean factoryResetNative();

    private native void alarmFiredNative();
    private native void dumpNative(FileDescriptor fd, String[] arguments);

    private native void interopDatabaseClearNative();
    private native void interopDatabaseAddNative(int feature, byte[] address, int length);

    protected void finalize() {
        cleanup();
        if (TRACE_REF) {
            synchronized (AdapterService.class) {
                sRefCount--;
                debugLog("finalize() - REFCOUNT: FINALIZED. INSTANCE_COUNT= " + sRefCount);
            }
        }
    }
}
