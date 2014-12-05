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
import com.sferadev.qpair.App;

import static com.sferadev.qpair.utils.Utils.createExplicitFromImplicitIntent;

// Utils to handle connection with QPair
public class QPairUtils {
    public static final String EXTRA_LOCAL_VERSION = "/local/qpair/version";
    public static final String EXTRA_PEER_VERSION = "/peer/qpair/version";
    public static final String EXTRA_QPAIR_DEVICE_TYPE = "/local/qpair/device_type";
    public static final String EXTRA_QPAIR_IS_CONNECTED = "/local/qpair/is_connected";
    public static final String EXTRA_QPAIR_IS_ON = "/local/qpair/is_on";

    public static final String EXTRA_SCHEME_AUTHORITY = com.lge.qpair.api.r2.QPairConstants.PROPERTY_SCHEME_AUTHORITY;

    // Get Preference Stored on the QPair Service
    public static String getQPairProperty(String uriString) {
        Uri uri = Uri.parse(EXTRA_SCHEME_AUTHORITY + uriString);
        Cursor cursor = App.getContext().getContentResolver().query(uri, null, null, null, null);
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
        Utils.setPreferences(Utils.KEY_IS_CONNECTED, isConnected);
        return isConnected;
    }

    // Boolean that states if Device is Phone (true) or tablet (false)
    public static boolean isPhone() {
        if (getQPairProperty(EXTRA_QPAIR_DEVICE_TYPE).equals("phone")) {
            Utils.setPreferences(Utils.KEY_IS_PHONE, true);
            return true;
        } else {
            Utils.setPreferences(Utils.KEY_IS_PHONE, false);
            return false;
        }
    }

    // Boolean with QPair Service Status
    public static boolean isQPairOn() {
        boolean isOn = Boolean.parseBoolean(getQPairProperty(EXTRA_QPAIR_IS_ON));
        Utils.setPreferences(Utils.KEY_IS_ON, isOn);
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

    // Class that handles the communication between devices
    public static class sendBroadcastConnection implements ServiceConnection {
        final String myAction;
        String myExtra[];

        public sendBroadcastConnection(String mAction) {
            myAction = mAction;
        }

        public sendBroadcastConnection(String mAction, String mExtraName, String mExtraValue) {
            myAction = mAction;
            myExtra = new String[] {mExtraName, mExtraValue};
        }

        public sendBroadcastConnection(String mAction, String mExtraName, int mExtraValue) {
            myAction = mAction;
            myExtra = new String[] {mExtraName, String.valueOf(mExtraValue)};
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();
                peerIntent.setAction(myAction);
                try {
                    if (myExtra[0] != null && myExtra[1] != null) {
                        peerIntent.putStringExtra(myExtra[0], myExtra[1]);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                peerContext.sendBroadcastOnPeer(peerIntent, null, null);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            App.getContext().unbindService(this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    }
}
