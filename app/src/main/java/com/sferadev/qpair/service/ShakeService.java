package com.sferadev.qpair.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;

import com.lge.qpair.api.r1.IPeerContext;
import com.lge.qpair.api.r1.IPeerIntent;
import com.lge.qpair.api.r1.QPairConstants;
import com.sferadev.qpair.Utils;
import com.sferadev.qpair.listener.ShakeListener;

public class ShakeService extends Service {

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShakeDetector;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeListener();
        mShakeDetector.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake(int count) {
                if (count > 1) {
                    ActivityManager am = (ActivityManager) ShakeService.this.getSystemService(ACTIVITY_SERVICE);
                    ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                    Utils.createToast("QPair: Opening app in G Pad");
                    String packageName = foregroundTaskInfo.topActivity.getPackageName();
                    String activityName = foregroundTaskInfo.topActivity.getShortClassName();
                    Utils.createToast("DEBUG: " + packageName + " " + activityName);
                    final Intent intent = new Intent(QPairConstants.ACTION_QPAIR_SERVICE);

                    // Bind to the QPair service
                    boolean bindResult = bindService(intent, new MyServiceConnection(packageName, activityName), 0);

                    if (!bindResult) {
                        Utils.createToast("QPair: Binding to service has failed");
                    }
                }
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer,	SensorManager.SENSOR_DELAY_UI);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public class MyServiceConnection implements ServiceConnection {

        String myPackage;
        String myActivity;

        MyServiceConnection(String mPackage, String mActivity) {
            myPackage = mPackage;
            myActivity = mActivity;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            // create an IPeerContext when the service is connected.
            IPeerContext peerContext = IPeerContext.Stub.asInterface(service);

            try {
                // create an IPeerIntent to be sent.
                IPeerIntent peerIntent = peerContext.newPeerIntent();

                // set the Activity class to be invoked on the peer.
                peerIntent.setClassName(myPackage, myActivity);

                // call startActivityOnPeer() with an IPeerIntent using IPeerContext.
                peerContext.startActivityOnPeer(peerIntent, null);

            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // unbind the QPair service.
            unbindService(this);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

    }

}

