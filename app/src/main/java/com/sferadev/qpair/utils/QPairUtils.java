package com.sferadev.qpair.utils;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.App;

public class QPairUtils {

    private static final String EXTRA_SCHEME_AUTHORITY = QPairConstants.PROPERTY_SCHEME_AUTHORITY;
    public static String EXTRA_LOCAL_VERSION = "/local/qpair/version";
    public static String EXTRA_PEER_VERSION = "/peer/qpair/version";
    private static final String EXTRA_QPAIR_IS_ON = "/local/qpair/is_on";
    private static final String EXTRA_QPAIR_IS_CONNECTED = "/local/qpair/is_connected";
    private static final String EXTRA_QPAIR_DEVICE_TYPE = "/local/qpair/device_type";

    private static String getQPairProperty(String uriString) {
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

    public static boolean isPhone() {
        if (getQPairProperty(EXTRA_QPAIR_DEVICE_TYPE).equals("phone")) {
            Utils.setPreferences(Utils.KEY_IS_PHONE, true);
            return true;
        } else {
            Utils.setPreferences(Utils.KEY_IS_PHONE, false);
            return false;
        }
    }

    public static boolean isQPairOn() {
        boolean isOn = Boolean.parseBoolean(getQPairProperty(EXTRA_QPAIR_IS_ON));
        Utils.setPreferences(Utils.KEY_IS_ON, isOn);
        return isOn;
    }

    public static boolean isConnected() {
        boolean isConnected = Boolean.parseBoolean(getQPairProperty(EXTRA_QPAIR_IS_CONNECTED));
        Utils.setPreferences(Utils.KEY_IS_CONNECTED, isConnected);
        return isConnected;
    }

    public static class sendBroadcastConnection implements ServiceConnection {

        final String myAction;
        String myExtra[];

        public sendBroadcastConnection(String mAction) {
            myAction = mAction;
        }

        public sendBroadcastConnection(String mAction, String mExtraName, String mExtraValue) {
            myAction = mAction;
            myExtra = new String[]{mExtraName, mExtraValue};
        }

        public sendBroadcastConnection(String mAction, String mExtraName, int mExtraValue) {
            myAction = mAction;
            myExtra = new String[]{mExtraName, String.valueOf(mExtraValue)};
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();
                peerIntent.setAction(myAction);
                if (myExtra[0] != null && myExtra[1] != null) {
                    peerIntent.putStringExtra(myExtra[0], myExtra[1]);
                }
                peerContext.sendBroadcastOnPeer(peerIntent, null);
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
