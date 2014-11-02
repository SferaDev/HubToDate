package com.sferadev.qpair.utils;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.sferadev.qpair.App;

public class QPairUtils {

    public static class MyActivityConnection implements ServiceConnection {

        String myPackage;
        String myActivity;

        public MyActivityConnection(String mPackage, String mActivity) {
            myPackage = mPackage;
            myActivity = mActivity;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();

                if (myActivity.startsWith(".")) {
                    peerIntent.setClassName(myPackage, myActivity);
                } else {
                    Utils.createToast("QPair: Apologies, this app is not supported"); //TODO
                }

                peerContext.startActivityOnPeer(peerIntent, null);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            App.getContext().unbindService(this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    }

    public static class MyWebsiteConnection implements ServiceConnection {

        String myURL;

        public MyWebsiteConnection(String url) {
            myURL = url;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();

                peerIntent.setAction(Intent.ACTION_VIEW);
                peerIntent.setData(myURL);

                peerContext.startActivityOnPeer(peerIntent, null);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            App.getContext().unbindService(this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    }

    public static class MyUninstallConnection implements ServiceConnection {

        String myPackage;

        public MyUninstallConnection(String packageName) {
            myPackage = packageName;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                IPeerIntent peerIntent = peerContext.newPeerIntent();

                peerIntent.setAction(Intent.ACTION_UNINSTALL_PACKAGE);
                peerIntent.setData(myPackage);

                peerContext.startActivityOnPeer(peerIntent, null);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            App.getContext().unbindService(this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    } //TODO
}
