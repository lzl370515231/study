/*
 * Copyright (C) 2012-2014 The Android Open Source Project
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

package com.android.bluetooth.btservice;

import android.util.Log;

final class JniCallbacks {

    private RemoteDevices mRemoteDevices;
    private AdapterProperties mAdapterProperties;
    private AdapterState mAdapterStateMachine;
    private BondStateMachine mBondStateMachine;
	private static final String TAG ="LZLB_JniCallbacks";

    JniCallbacks(AdapterState adapterStateMachine,AdapterProperties adapterProperties) {
		Log.e(TAG,"JniCallbacks");
        mAdapterStateMachine = adapterStateMachine;
        mAdapterProperties = adapterProperties;
    }

    void init(BondStateMachine bondStateMachine, RemoteDevices remoteDevices) {
    	Log.e(TAG,"init");
        mRemoteDevices = remoteDevices;
        mBondStateMachine = bondStateMachine;
    }

    void cleanup() {
    	Log.e(TAG,"cleanup");
        mRemoteDevices = null;
        mAdapterProperties = null;
        mAdapterStateMachine = null;
        mBondStateMachine = null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
    	Log.e(TAG,"clone");
        throw new CloneNotSupportedException();
    }

    void sspRequestCallback(byte[] address, byte[] name, int cod, int pairingVariant,
            int passkey) {
            Log.e(TAG,"sspRequestCallback");
        mBondStateMachine.sspRequestCallback(address, name, cod, pairingVariant,
            passkey);
    }
    void devicePropertyChangedCallback(byte[] address, int[] types, byte[][] val) {
    Log.e(TAG,"devicePropertyChangedCallback");
        mRemoteDevices.devicePropertyChangedCallback(address, types, val);
    }

    void deviceFoundCallback(byte[] address) {
    	Log.e(TAG,"deviceFoundCallback");
        mRemoteDevices.deviceFoundCallback(address);
    }

    void pinRequestCallback(byte[] address, byte[] name, int cod, boolean min16Digits) {
    Log.e(TAG,"pinRequestCallback");
        mBondStateMachine.pinRequestCallback(address, name, cod, min16Digits);
    }

    void bondStateChangeCallback(int status, byte[] address, int newState) {
    Log.e(TAG,"bondStateChangeCallback");
        mBondStateMachine.bondStateChangeCallback(status, address, newState);
    }

    void aclStateChangeCallback(int status, byte[] address, int newState) {
    Log.e(TAG,"aclStateChangeCallback");
        mRemoteDevices.aclStateChangeCallback(status, address, newState);
    }

    void stateChangeCallback(int status) {
    	Log.e(TAG,"stateChangeCallback");
        mAdapterStateMachine.stateChangeCallback(status);
    }

    void discoveryStateChangeCallback(int state) {
    	Log.e(TAG,"discoveryStateChangeCallback");
        mAdapterProperties.discoveryStateChangeCallback(state);
    }

    void adapterPropertyChangedCallback(int[] types, byte[][] val) {
    	Log.e(TAG,"adapterPropertyChangedCallback");
        mAdapterProperties.adapterPropertyChangedCallback(types, val);
    }

}
