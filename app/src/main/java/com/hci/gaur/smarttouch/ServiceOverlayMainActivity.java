/*
 * Copyright (C) 2017 Varun Gaur - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * For more information, please write to:  varun.gaur@usask.ca, or visit : www.varungaur.com
 */

package com.hci.gaur.smarttouch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 *  ServiceOverlayMainActivity is the main activity of SmartTouch App.
 *  Displays the various shapes/gestures and phone features associated with them.
 *  These gestures are shortcuts for activating certain phone features.
 *  Provides option to start and stop MyService which actively listens to the shaking of phone.
 *  @author     Varun Gaur
 *  @version    3.0
 *  @since      2017-04-18
 */

public class ServiceOverlayMainActivity extends Activity {
    /**
     * Initializes ServiceOverlayMainActivity.
     * setContentView(int) is called with a layout resource to define the UI,
     * @param savedInstanceState savedInstanceState is a reference to a Bundle object that is passed into the onCreate method of ServiceOverlayMainActivity.
     *                           It is used by ServiceOverlayMainActivity to restore itself to a previous state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_overlay_main);
    }

    /**
     * It is used to specify the options menu for ServiceOverlayMainActivity.
     * In this method, you can inflate your menu resource (defined in XML) into the Menu provided in the callback.
     * @param menu Used for managing items in a menu.
     * @return true after inflating the menu resources.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_service_overlay_main, menu);
        return true;
    }

    /**
     * Invokes MyService when "Turn On" button is pressed
     * @param view Occupies a rectangular area on the screen and is responsible for drawing and event handling.
     */
    public void startMyService(View view) {
        Intent serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }

    /**
     * Stops MyService when "Turn Off" button is pressed
     * @param view occupies a rectangular area on the screen and is responsible for drawing and event handling.
     */
    public void stopMyService(View view) {
        Intent serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
    }

    /**
     * This method is called as the first indication when the user leaves ServiceOverlayMainActivity.
     */
    @Override
    public void onPause() {
        super.onPause();
        Intent serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
    }

    /**
     * It is called when user selects an item from the options menu.
     * @param item is the MenuItem selected by the user.
     * @return true if an item is selected from the options menu.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
