/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.android.bluetooth.hfpclient.connserv;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadsetClient;
import android.bluetooth.BluetoothHeadsetClientCall;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.net.Uri;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.TelecomManager;
import android.util.Log;

public class HfpClientConnection extends Connection {
    private static final String TAG = "LZLB_HfpClientConnection";

    private final Context mContext;
    private final BluetoothDevice mDevice;

    private BluetoothHeadsetClient mHeadsetProfile;
    private BluetoothHeadsetClientCall mCurrentCall;
    private boolean mClosed;
    private boolean mLocalDisconnect;
    private boolean mClientHasEcc;
    private boolean mAdded;

    public HfpClientConnection(Context context, BluetoothDevice device, BluetoothHeadsetClient client,
            BluetoothHeadsetClientCall call, Uri number) {
        mDevice = device;
        mContext = context;
        mHeadsetProfile = client;
        mCurrentCall = call;
        if (mHeadsetProfile != null) {
            mClientHasEcc = HfpClientConnectionService.hasHfpClientEcc(mHeadsetProfile, mDevice);
        }
        setAudioModeIsVoip(false);
        setAddress(number, TelecomManager.PRESENTATION_ALLOWED);
        setInitialized();

        if (mHeadsetProfile != null) {
            finishInitializing();
        }
    }

    public void onHfpConnected(BluetoothHeadsetClient client) {
    	Log.e(TAG,"onHfpConnected");
        mHeadsetProfile = client;
        mClientHasEcc = HfpClientConnectionService.hasHfpClientEcc(mHeadsetProfile, mDevice);
        finishInitializing();
    }

    public void onHfpDisconnected() {
    	Log.e(TAG,"onHfpDisconnected");
        mHeadsetProfile = null;
        close(DisconnectCause.ERROR);
    }

    public void onAdded() {
        mAdded = true;
    }

    public BluetoothHeadsetClientCall getCall() {
        return mCurrentCall;
    }

    public boolean inConference() {
        return mAdded && mCurrentCall != null && mCurrentCall.isMultiParty() &&
                getState() != Connection.STATE_DISCONNECTED;
    }

    public void enterPrivateMode() {
    	Log.e(TAG,"enterPrivateMode");
        mHeadsetProfile.enterPrivateMode(mDevice, mCurrentCall.getId());
        setActive();
    }

    public void handleCallChanged(BluetoothHeadsetClientCall call) {
    	Log.e(TAG,"handleCallChanged");
        HfpClientConference conference = (HfpClientConference) getConference();
        mCurrentCall = call;

        int state = call.getState();
        Log.e(TAG, "Got call state change to " + state);
        switch (state) {
            case BluetoothHeadsetClientCall.CALL_STATE_ACTIVE:
                setActive();
                if (conference != null) {
                    conference.setActive();
                }
                break;
            case BluetoothHeadsetClientCall.CALL_STATE_HELD_BY_RESPONSE_AND_HOLD:
            case BluetoothHeadsetClientCall.CALL_STATE_HELD:
                setOnHold();
                if (conference != null) {
                    conference.setOnHold();
                }
                break;
            case BluetoothHeadsetClientCall.CALL_STATE_DIALING:
            case BluetoothHeadsetClientCall.CALL_STATE_ALERTING:
                setDialing();
                break;
            case BluetoothHeadsetClientCall.CALL_STATE_INCOMING:
            case BluetoothHeadsetClientCall.CALL_STATE_WAITING:
                setRinging();
                break;
            case BluetoothHeadsetClientCall.CALL_STATE_TERMINATED:
                // TODO Use more specific causes
                close(mLocalDisconnect ? DisconnectCause.LOCAL : DisconnectCause.REMOTE);
                break;
            default:
                Log.wtf(TAG, "Unexpected phone state " + state);
        }
        setConnectionCapabilities(CAPABILITY_SUPPORT_HOLD | CAPABILITY_MUTE |
                CAPABILITY_SEPARATE_FROM_CONFERENCE | CAPABILITY_DISCONNECT_FROM_CONFERENCE |
                (getState() == STATE_ACTIVE || getState() == STATE_HOLDING ? CAPABILITY_HOLD : 0));
    }

    private void finishInitializing() {
    	Log.e(TAG,"finishInitializing");
        if (mCurrentCall == null) {
            String number = getAddress().getSchemeSpecificPart();
            Log.d(TAG, "Dialing " + number);
            mHeadsetProfile.dial(mDevice, number);
            setDialing();
            // We will change state dependent on broadcasts from BluetoothHeadsetClientCall.
        } else {
            handleCallChanged(mCurrentCall);
        }
    }

    private void close(int cause) {
        Log.e(TAG, "Closing " + mClosed);
        if (mClosed) {
            return;
        }
        setDisconnected(new DisconnectCause(cause));

        mClosed = true;
        mCurrentCall = null;

        destroy();
    }

    @Override
    public void onPlayDtmfTone(char c) {
        Log.e(TAG, "onPlayDtmfTone " + c + " " + mCurrentCall);
        if (!mClosed && mHeadsetProfile != null) {
            mHeadsetProfile.sendDTMF(mDevice, (byte) c);
        }
    }

    @Override
    public void onDisconnect() {
        Log.e(TAG, "onDisconnect " + mCurrentCall);
        if (!mClosed) {
            if (mHeadsetProfile != null && mCurrentCall != null) {
                mHeadsetProfile.terminateCall(mDevice, mClientHasEcc ? mCurrentCall.getId() : 0);
                mLocalDisconnect = true;
            } else {
                close(DisconnectCause.LOCAL);
            }
        }
    }

    @Override
    public void onAbort() {
        Log.e(TAG, "onAbort " + mCurrentCall);
        onDisconnect();
    }

    @Override
    public void onHold() {
        Log.e(TAG, "onHold " + mCurrentCall);
        if (!mClosed && mHeadsetProfile != null) {
            mHeadsetProfile.holdCall(mDevice);
        }
    }

    @Override
    public void onUnhold() {
    	Log.e(TAG,"onUnhold");
        if (getConnectionService().getAllConnections().size() > 1) {
            Log.w(TAG, "Ignoring unhold; call hold on the foreground call");
            return;
        }
        Log.d(TAG, "onUnhold " + mCurrentCall);
        if (!mClosed && mHeadsetProfile != null) {
            mHeadsetProfile.acceptCall(mDevice, BluetoothHeadsetClient.CALL_ACCEPT_HOLD);
        }
    }

    @Override
    public void onAnswer() {
        Log.e(TAG, "onAnswer " + mCurrentCall);
        if (!mClosed) {
            mHeadsetProfile.acceptCall(mDevice, BluetoothHeadsetClient.CALL_ACCEPT_NONE);
        }
    }

    @Override
    public void onReject() {
        Log.d(TAG, "onReject " + mCurrentCall);
        if (!mClosed) {
            mHeadsetProfile.rejectCall(mDevice);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof HfpClientConnection)) {
            return false;
        }
        Uri otherAddr = ((HfpClientConnection) o).getAddress();
        return getAddress() == otherAddr || otherAddr != null && otherAddr.equals(getAddress());
    }

    @Override
    public int hashCode() {
        return getAddress() == null ? 0 : getAddress().hashCode();
    }

    @Override
    public String toString() {
        return "HfpClientConnection{" + getAddress() + "," + stateToString(getState()) + "," +
                mCurrentCall + "}";
    }
}
