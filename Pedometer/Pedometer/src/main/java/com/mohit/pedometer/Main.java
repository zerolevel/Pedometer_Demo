package com.mohit.pedometer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.TextView;

public class Main extends ActionBarActivity implements SensorEventListener {

    private float   mLimit = 10;
    private float   mLastValues = 0;
    private float   mScale = 0;
    private float   mYOffset;
    private float   mLastDirections = 0;
    private float   mLastExtremes[] = new float[2];
    private float   mLastDiff = 0;
    private int     mLastMatch = -1;
    private SensorManager accSensorManager;
    private boolean rStatus = false;
    private Integer numSteps = 0;
    private float alpha = 0.5F;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        accSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Initializes all the values.
        StepDetector();
    }

    @Override
    protected void onPause() {
        // unregister listener
        super.onPause();
        accSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register this class as a listener for the orientation and
        // accelerometer sensors
        accSensorManager.registerListener(this,
                accSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);


    }

    /**
     * Sets constants for step Detections.
     */
    public void StepDetector() {
        int h = 480; //A constant that has worked on testing.
        mYOffset = h * alpha;
        mScale = - (h * (1 - alpha) * (1.0f / (SensorManager.MAGNETIC_FIELD_EARTH_MAX)));
    }

    /**
     * This method contains the procedure to find if the change in the sensor values corresponds to
     * a new step.
     */

    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER && rStatus) {
                float v = 0;
                for (int i=0 ; i<3 ; i++) {
                    final float vp = mYOffset + event.values[i] * mScale;
                    v += vp;
                }

                v/=3; //Mean for three directions.
                //Since the task is to design a pedometer therefore, this works.
                //It is assumed that the runner keeps the mobile device in his paint pockets.

                float direction = (v > mLastValues ? 1 : (v < mLastValues ? -1 : 0));
                if (direction == - mLastDirections) {
                    // Direction changed
                    int extType = (direction > 0 ? 0 : 1); // minimum or maximum?
                    mLastExtremes[extType] = mLastValues;
                    float diff = Math.abs(mLastExtremes[extType] - mLastExtremes[1 - extType]);

                    if (diff > mLimit) { //If the difference between the minimum and the maximum crosses a threshold (set to 10 here)
                        boolean isAlmostAsLargeAsPrevious = diff > (mLastDiff*2/3);
                        boolean isPreviousLargeEnough = mLastDiff > (diff/3);
                        boolean isNotContra = (mLastMatch != 1 - extType);
                        if (isAlmostAsLargeAsPrevious && isPreviousLargeEnough && isNotContra) {
                            updateStep();
                            mLastMatch = extType;
                        }
                        else {
                            mLastMatch = -1;
                        }
                    }
                    mLastDiff = diff;
                }
                mLastDirections = direction;
                mLastValues = v;
            }
        }
    }

    /**
     * This function corresponds the "Start Recording" button in the app.
     * this enables the running status of the app and starts the step count.
     */
    public void startPedometer(View v) {
        v.setEnabled(false); //disables the button
        TextView tv = (TextView) findViewById(R.id.numSteps);
        numSteps = 0; //initializes the count to 0
        tv.setText(numSteps.toString());
        Button stopButton = (Button) findViewById(R.id.end);
        stopButton.setEnabled(true); //Enables the "End Recording button"
        rStatus = true; //enables the running status
    }

    /**
     * This function corresponds the "End Recording" button in the app.
     * This disables running status and enables the other button
     */

    public void endPedometer(View v) {
        v.setEnabled(false); //disables the button
        Button stopButton = (Button) findViewById(R.id.start);
        stopButton.setEnabled(true); //enables "Start recording button for restart."
        rStatus = false; //disables the running status.
    }

    /**
     * This function updates the step.
     * It increases the count.
     * And also updates the display.
     */
    public void updateStep() {
        numSteps++; //Assumed that each hit counts one step.
        TextView tv = (TextView) findViewById(R.id.numSteps);
        tv.setText(numSteps.toString());
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}
