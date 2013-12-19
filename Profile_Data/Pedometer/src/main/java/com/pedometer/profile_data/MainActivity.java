package com.pedometer.profile_data;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Parcelable;
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
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends ActionBarActivity implements SensorEventListener {

    private SensorManager accSensorManager;
    private Sensor accSensor;
    private String accData = null;
    private boolean runStatus = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
        accSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accSensor = (Sensor) accSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long actualTime = System.currentTimeMillis();
        float [] values = event.values;
        if(runStatus) {
            if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accData += actualTime + " " + event.values[0] + "  " + event.values[1] + "  " + event.values[2] + "\n";
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
     * This function starts when profiling starts
     */
    public void startProfiling(View v) {
        if(!runStatus) {
            accData = "";
            runStatus = true;
        }
    }

    /**
     *
     */
    public void endProfiling(View v) {
        if(runStatus) {
            EditText et = (EditText) findViewById(R.id.numSteps);
            String buffer = et.getText().toString();
            if(buffer == "" ) {
                 buffer = "30";
            }

            String fileName = "ProfileData" + buffer;
            saveFile(fileName+"_Acc.txt",accData);
            runStatus = false;
        }
    }

    /**
     * This function saves a file
     */

    public void saveFile(String fileName, String dataString) {
        File file = new File(getExternalFilesDir(null), fileName);

        try {
            OutputStream os =new FileOutputStream(file);
            os.write(dataString.getBytes());
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
