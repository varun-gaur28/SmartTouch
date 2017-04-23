/*
 * Copyright (C) 2017 Varun Gaur - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * For more information, please write to:  varun.gaur@usask.ca, or visit : www.varungaur.com
 */

package com.hci.gaur.smarttouch;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

/**
 *  MyService listens to shaking of the phone.
 *  Once the phone is shook, it presents user with a pop-up view.
 *  User performs a gesture and the respective feature is activated or deactivated.
 *  @author     Varun Gaur
 *  @version    3.0
 *  @since      2017-04-18
 */

public class MyService extends Service implements SensorEventListener {
    PopupView mView;
    private long lastUpdate = 0;
    private float x, y, z;
    private float lastX, lastY, lastZ;
    private static final int SHAKE_THRESHOLD= 300;
    private static final int MINIMUM_TIME_DIFF=100;
    private boolean isPopupViewShowed;

    /**
     * Return the communication channel to the service.
     * @param arg0 The Intent that was used to bind to this service, as given to Context.bindService.
     * @return null if clients can not bind to the service.
     */
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    /**
     * Called by the system when MyService is first created
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(),"Please shake the phone", Toast.LENGTH_SHORT).show();
        SensorManager sensorMgr = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorMgr.registerListener(this, sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        isPopupViewShowed = false;
    }

    /**
     * Called by the system to notify MyService that it is no longer used and is being removed
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        removePopupView();
    }

    /**
     * Called when the accuracy of a sensor has changed.
     * @param sensor The ID of the sensor being monitored.
     * @param accuracy The new accuracy of this sensor.
     */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Called when there is a new sensor event. It is also called when we have a new reading from the sensor.
     * @param event It holds the information such as the sensor's type, the time-stamp, accuracy and the sensor's date.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long curTime = System.currentTimeMillis();
            if ((curTime - lastUpdate) > MINIMUM_TIME_DIFF) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                x = event.values[0];
                y = event.values[1];
                z = event.values[2];
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                if (speed >= SHAKE_THRESHOLD && !isPopupViewShowed) {
                    invokePopupView();
                    isPopupViewShowed = true;
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    /**
     * Creates a new popup View.
     * User can draw shape/gesture on this view.
     */
    public void invokePopupView(){
        mView = new PopupView(this);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                0,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.RIGHT | Gravity.TOP;
        params.setTitle("Test Popup");
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.addView(mView, params); //adds the new custom view to the screen.
    }

    /**
     * Removes the popup view.
     */
    public void removePopupView(){
        if(mView != null)
        {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
            isPopupViewShowed = false;
        }
    }
}
