package com.sferadev.qpair.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;

import com.sferadev.qpair.listener.ShakeListener;

import static com.sferadev.qpair.utils.Utils.createAssistDialog;
import static com.sferadev.qpair.utils.Utils.isScreenOn;

// Service that binds the ShakeListener and receives the Shakes
public class ShakeService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SensorManager mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        ShakeListener mShakeDetector = new ShakeListener();
        mShakeDetector.setOnShakeListener(new ShakeListener.OnShakeListener() {

            @Override
            public void onShake(int count) {
                // Avoid non-desired shaking! (2 or more shakes and screenOn)
                if (count > 1 && isScreenOn()) {
                    // Launch Assist Dialog when Shake received
                    createAssistDialog();
                }
            }
        });
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

}

