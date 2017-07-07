package com.example.toxy.dawid_danieluk_kompas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class main extends AppCompatActivity implements SensorEventListener{

    private static final boolean AUTO_HIDE = true;

    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;


    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private SensorManager SensManager;

    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    private Sensor kSensor;
    private Sensor tSensor;
    private Sensor cSensor;
    private Sensor wSensor;

    private ImageView kompas;

    private TextView temperaturaTXT;
    private TextView cisnienieTXT;
    private TextView wilgotnoscTXT;
    ImageView weather;
    ImageView ground;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SensManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);

        //połączenie wszystkich elementów związanych z czujnikami
        kSensor = SensManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        tSensor = SensManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        cSensor = SensManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        wSensor = SensManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);

        temperaturaTXT = (TextView) findViewById(R.id.temp);
        cisnienieTXT = (TextView) findViewById(R.id.cisnienie);
        wilgotnoscTXT = (TextView) findViewById(R.id.wilgotnosc);
        kompas = (ImageView) findViewById(R.id.kompas);

        SensManager.registerListener(this,kSensor,SensorManager.SENSOR_DELAY_NORMAL);
        SensManager.registerListener(this,tSensor,SensorManager.SENSOR_DELAY_NORMAL);
        SensManager.registerListener(this,cSensor,SensorManager.SENSOR_DELAY_NORMAL);
        SensManager.registerListener(this,wSensor,SensorManager.SENSOR_DELAY_NORMAL);
        //kuniec łączenia czujników

        weather = (ImageView) findViewById(R.id.weather);
        ground = (ImageView) findViewById(R.id.ground);

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.fullscreen_content);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor==kSensor){
            zmianaKompas(event.values);
        } else if (event.sensor==tSensor) {
            zmianaTemperatura(event.values);
        } else if (event.sensor==cSensor) {
            zmianaCisnienie(event.values);
        } else {
            zmianaWilgotnosc(event.values);
        }
    }

    private void zmianaKompas(float [] values){
        kompas.setRotation(-1*values[0]);
    }

    private void zmianaCisnienie(float [] values){
        cisnienieTXT.setText(String.valueOf(values[0]) + " hPa");
        if (values[0]>1013){
            weather.setImageResource(R.drawable.sunnynoclouds);
        } else weather.setImageResource(R.drawable.sunnynclouds);
    }



    private void zmianaTemperatura(float [] values){
        temperaturaTXT.setText(String.valueOf(values[0]) + " °C");
        if (values[0]>0){
            ground.setImageResource(R.drawable.ground1);
        } else ground.setImageResource(R.drawable.ground2);
    }

    private void zmianaWilgotnosc(float [] values){
        wilgotnoscTXT.setText(String.valueOf(values[0]) + " %");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    Calendar c = Calendar.getInstance();

    public void Zapisz(View view){
        try {
            OutputStreamWriter out = new
                    OutputStreamWriter(openFileOutput("dane.txt",MODE_APPEND));
            String text = "";
            text += String.valueOf(c.get(Calendar.YEAR) + "." + String.valueOf(c.get(Calendar.MONTH)) + "." + String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + " " +
                String.valueOf(c.get(Calendar.HOUR)) + ":" + String.valueOf(c.get(Calendar.MINUTE)));
            out.write(text);
            out.write('\n');
            text = "";
            text += "Temperatura= " + temperaturaTXT.getText() + " | " + "Ciśnienie= " + cisnienieTXT.getText() + " | " + "Wilgotność Powietrza= " +wilgotnoscTXT.getText();
            out.write(text);
            out.write('\n');
            out.close();
            Toast.makeText(this,"Dane zostały zapisane C:",Toast.LENGTH_LONG).show();
        } catch (java.io.IOException e) {
            Toast.makeText(this,"Nie udało się zapisać danych :C",Toast.LENGTH_LONG).show();
        }
    }

    public void Wczytaj(View view){
        Context context;
        context = getApplicationContext();
        Intent intent = new Intent(context,dane.class);
        startActivity(intent);
    }


}
