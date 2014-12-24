package com.sferadev.qpair.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import com.lge.qpair.api.r2.IPeerContext;
import com.lge.qpair.api.r2.IPeerIntent;
import com.lge.qpair.api.r2.QPairConstants;

import static com.sferadev.qpair.App.getContext;
import static com.sferadev.qpair.utils.Constants.ACTION_CALLBACK_FAILURE;
import static com.sferadev.qpair.utils.Constants.ACTION_OPEN_ACTIVITY;
import static com.sferadev.qpair.utils.Constants.EXTRA;
import static com.sferadev.qpair.utils.Constants.EXTRA_LOCAL_VERSION;
import static com.sferadev.qpair.utils.Constants.EXTRA_QPAIR_DEVICE_TYPE;
import static com.sferadev.qpair.utils.Constants.EXTRA_QPAIR_IS_CONNECTED;
import static com.sferadev.qpair.utils.Constants.EXTRA_QPAIR_IS_ON;
import static com.sferadev.qpair.utils.Constants.EXTRA_SCHEME_AUTHORITY;
import static com.sferadev.qpair.utils.Constants.KEY_IS_CONNECTED;
import static com.sferadev.qpair.utils.Constants.KEY_IS_ON;
import static com.sferadev.qpair.utils.Constants.KEY_IS_PHONE;
import static com.sferadev.qpair.utils.PreferenceUtils.setPreference;
import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;

// Utils to handle connection with QPair
public class QPairUtils {
    // Get Preference Stored on the QPair Service
    public static String getQPairProperty(String uriString) {
        Uri uri = Uri.parse(EXTRA_SCHEME_AUTHORITY + uriString);
        Cursor cursor = getContext().getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.moveToFirst()) {
                    return cursor.getString(0);
                }
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    // Create proper intent according if user is on r1 or r2
    public static Intent getQpairIntent() {
        return createExplicitFromImplicitIntent(new Intent(QPairConstants.ACTION_SERVICE));
    }

    // Boolean with QPair Connection Status
    public static boolean isConnected() {
        boolean isConnected = Boolean.parseBoolean(getQPairProperty(EXTRA_QPAIR_IS_CONNECTED));
        setPreference(KEY_IS_CONNECTED, isConnected);
        return isConnected;
    }

    // Boolean that states if Device is Phone (true) or tablet (false)
    public static boolean isPhone() {
        if (getQPairProperty(EXTRA_QPAIR_DEVICE_TYPE).equals("phone")) {
            setPreference(KEY_IS_PHONE, true);
            return true;
        } else {
            setPreference(KEY_IS_PHONE, false);
            return false;
        }
    }

    // Boolean with QPair Service Status
    public static boolean isQPairOn() {
        boolean isOn = Boolean.parseBoolean(getQPairProperty(EXTRA_QPAIR_IS_ON));
        setPreference(KEY_IS_ON, isOn);
        return isOn;
    }

    // Check if user is updated to r2
    public static boolean isR2D2() {
        if (Integer.parseInt(getQPairProperty(EXTRA_LOCAL_VERSION)) > 4200241) {
            return true;
        } else {
            return false;
        }
    }

    // Class that handles the broadcast between devices
    public static class sendBroadcastConnection implements ServiceConnection {
        final String myAction;
        String myExtra = null;

        public sendBroadcastConnection(String mAction) {
            myAction = mAction;
        }

        public sendBroadcastConnection(String mAction, String mExtraValue) {
            myAction = mAction;
            myExtra = mExtraValue;
        }

        public sendBroadcastConnection(String mAction, int mExtraValue) {
            myAction = mAction;
            myExtra = String.valueOf(mExtraValue);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);
            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();
                peerIntent.setAction(myAction);
                if (myExtra != null) {
                    peerIntent.putStringExtra(EXTRA, myExtra);
                }

                peerContext.sendBroadcastOnPeer(peerIntent, null, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getContext().unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    // Class that handles the Activity between devices
    public static class startActivityConnection implements ServiceConnection {
        final String myPackage;
        final String myActivity;

        public startActivityConnection(String mPackage, String mActivity) {
            myPackage = mPackage;
            myActivity = mActivity;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);
            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();
                peerIntent.setAction(ACTION_OPEN_ACTIVITY);
                peerIntent.putStringExtra(EXTRA, myPackage);

                IPeerIntent failureCallback = peerContext.newPeerIntent();
                failureCallback.setAction(ACTION_CALLBACK_FAILURE);

                peerContext.sendBroadcastOnPeer(peerIntent, null, failureCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getContext().unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }

    // Class that handles the URL between devices
    public static class openURLConnection implements ServiceConnection {
        final String myURL;

        public openURLConnection(String mURL) {
            myURL = mURL;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);
            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();
                peerIntent.setAction(Intent.ACTION_VIEW);
                peerIntent.setData(myURL);

                peerContext.startActivityOnPeer(peerIntent, null, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            getContext().unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
