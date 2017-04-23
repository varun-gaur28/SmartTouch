/*
 * Copyright (C) 2017 Varun Gaur - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * For more information, please write to:  varun.gaur@usask.ca, or visit : www.varungaur.com
 */

package com.hci.gaur.smarttouch;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.hci.gaur.smarttouch.io.hybrid.interaction.dollar.Dollar;
import com.hci.gaur.smarttouch.io.hybrid.interaction.dollar.Point;

import java.util.Vector;

/**
 *  PopupView is a custom view.
 *  It lets the user to draw the shape/gesture.
 *  It collects all the touch points and send it to One Dollar Recognizer (Dollar Class).
 *  Method recognize() of Dollar class recognizes the shape/gesture done.
 *  Methods getName() and getScore() returns the name and accuracy score of the shape/gesture done.
 *  PopView uses the name of ths shape and accuracy score to determine which phone feature needs to be activated.
 *  @author     Varun Gaur
 *  @version    3.0
 *  @since      2017-04-18
 */

class PopupView extends View {
    private Paint mLoadPaint;
    private float touchX;
    private float touchY;
    Dollar dollar;
    private static final float MINIMUM_SCORE=0.8f;
    private static final int ALPHA_COMPONENT=255;
    private static final int RED_COMPONENT=10;
    private static final int GREEN_COMPONENT=10;
    private static final int BLUE_COMPONENT=10;
    private static final int ALPHA_COMPONENT_SECOND=255;
    private static final int RED_COMPONENT_SECOND=150;
    private static final int GREEN_COMPONENT_SECOND=150;
    private static final int BLUE_COMPONENT_SECOND=150;
    private static final int RECT_LEFT=0;
    private static final int RECT_TOP=0;
    private static final int TEXT_SIZE=48;

    /**
     * Initiates the class variables
     * @param context is a handle to the system; it provides services like resolving resources, obtaining access to databases and preferences.
     */
    public PopupView(Context context) {
        super(context);
        dollar = new Dollar(Dollar.GESTURES_DEFAULT);   //creates an instance of Dollar class.
        mLoadPaint = new Paint();
        mLoadPaint.setAntiAlias(true);
        mLoadPaint.setTextSize(TEXT_SIZE);
    }

    /**
     * Called when the popup view should render its content.
     * @param canvas is a Canvas object that the view can use to draw itself.
     *               The Canvas class defines methods for drawing text, lines, bitmaps, and many other graphics primitives.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        mLoadPaint.setARGB(ALPHA_COMPONENT, RED_COMPONENT, GREEN_COMPONENT, BLUE_COMPONENT);
        mLoadPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(RECT_LEFT, RECT_TOP, width, height, mLoadPaint);
        mLoadPaint.setARGB(ALPHA_COMPONENT_SECOND, RED_COMPONENT_SECOND, GREEN_COMPONENT_SECOND, BLUE_COMPONENT_SECOND);
        Vector points = dollar.getPoints();
        if(points.size()>1){
            for (int i = 0; i < points.size()-1; i++)
            {
                Point p1 = (Point)points.elementAt(i);
                Point p2 = (Point)points.elementAt(i+1);
                canvas.drawLine((float)p1.X, (float)p1.Y, (float)p2.X, (float)p2.Y, mLoadPaint); //draws the shape/gesture done by user on the screen.
            }
        }
    }

    /**
     * Called when popup view should assign a size and position to all of its children.
     * @param arg0 This is a new size or position for this view
     * @param arg1 Left position, relative to parent
     * @param arg2 Top position, relative to parent
     * @param arg3 Right position, relative to parent
     * @param arg4 Bottom position, relative to parent
     */
    @Override
    protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
    }

    /**
     * It uses the name and score of the shape/gesture done and activates/deactivates or launch the respective feature of phone.
     * @param name Name of the shape/gesture (performed by user) provided by One Dollar Recognizer
     * @param score Accuracy score for the shape/gesture (performed by user) calculated by One Dollar Recognizer
     */
    public void activateFeature (String name, double score)
    {
        Intent intent;
        MyService m = (MyService)getContext();

        if(score>=MINIMUM_SCORE) {
            switch (name) {
                case "triangle":
                    Toast.makeText(m,"Gesture done is " + name, Toast.LENGTH_LONG).show();
                    WifiManager wifiManager = (WifiManager) (m.getApplicationContext().getSystemService(Context.WIFI_SERVICE));
                    if (!wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(true);
                        Toast.makeText(m,"WiFi enabled", Toast.LENGTH_LONG).show();
                    } else  {
                        wifiManager.setWifiEnabled(false);
                        Toast.makeText(m,"WiFi disabled", Toast.LENGTH_LONG).show();
                    }
                    m.removePopupView();
                    break;
                case "circle CCW":
                    Toast.makeText(m,"Gesture done is " + name, Toast.LENGTH_LONG).show();
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.disable();
                        Toast.makeText(m,"Bluetooth disabled", Toast.LENGTH_LONG).show();
                    }
                    else if (!mBluetoothAdapter.isEnabled())
                    {
                        mBluetoothAdapter.enable();
                        Toast.makeText(m,"Bluetooth enabled", Toast.LENGTH_LONG).show();
                    }
                    m.removePopupView();
                    break;
                case "delete":
                    Toast.makeText(m,"Gesture done is " + name, Toast.LENGTH_LONG).show();
                    String PhoneNumber = "3063708660";
                    intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + PhoneNumber));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    m.startActivity(intent);
                    m.removePopupView();
                    Toast.makeText(m,"Phone dialer launched", Toast.LENGTH_LONG).show();
                    break;
                case "leftSquareBracket":
                    Toast.makeText(m,"Gesture done is " + name, Toast.LENGTH_LONG).show();
                    intent = new Intent(MediaStore.INTENT_ACTION_VIDEO_CAMERA);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    m.startActivity(intent);
                    m.removePopupView();
                    Toast.makeText(m,"Video camera launched", Toast.LENGTH_LONG).show();
                    break;
                case "check":
                    Toast.makeText(m,"Gesture done is " + name, Toast.LENGTH_LONG).show();
                    intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    m.startActivity(intent);
                    m.removePopupView();

                    Toast.makeText(m,"Music player launched and playing music", Toast.LENGTH_LONG).show();
                    break;
                case "v":
                    Toast.makeText(m,"Gesture done is " + name, Toast.LENGTH_LONG).show();
                    Vibrator v = (Vibrator)( m.getSystemService(Context.VIBRATOR_SERVICE));
                    v.vibrate(2000);
                    m.removePopupView();
                    Toast.makeText(m,"Phone vibrating", Toast.LENGTH_LONG).show();
                    break;
                default:
                    m.removePopupView();
                    Toast.makeText(m,"Gesture not supported", Toast.LENGTH_LONG).show();
                    break;
            }
        }
        else if (score<MINIMUM_SCORE)
        {
            m.removePopupView();
            Toast.makeText(m,"Gesture not supported", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Called when a touch screen motion event occurs.
     * @param event The motion event.  It provides X and Y coordinates of touch point.
     * @return True if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action=event.getAction();
        touchX = event.getX();
        touchY = event.getY();
        switch(action)
        {
            case MotionEvent.ACTION_DOWN: //this action code specifies that user touches the screen for first time.
                dollar.clear();
                dollar.setActive(true);
                dollar.addPoint((int)touchX,(int)touchY);
                this.invalidate();
                break;
            case MotionEvent.ACTION_MOVE: //this action code specifies that user is moving the finger over the screen (multiple touch points).
                dollar.addPoint((int)touchX,(int)touchY);
                this.invalidate();
                break;
            case MotionEvent.ACTION_UP: //this action code specifies that user has lifted their finger up.
                dollar.recognize();
                activateFeature(dollar.getName(),dollar.getScore());
                this.invalidate();
                break;
        }
        return true;
    }
}
