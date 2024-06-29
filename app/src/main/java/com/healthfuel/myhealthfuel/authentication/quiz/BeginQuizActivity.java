package com.healthfuel.myhealthfuel.authentication.quiz;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.healthfuel.myhealthfuel.NotificationService;
import com.healthfuel.myhealthfuel.R;
import com.healthfuel.myhealthfuel.StepCounterService;


public class BeginQuizActivity extends AppCompatActivity {
    private Button beginQuizButton;
    private static final int REQUEST_PERMISION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin_quiz);

        beginQuizButton = findViewById(R.id.beginQuizButton);


        beginQuizButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
            @Override
            public void onClick(View v) {


                if (areAllPermissionsGranted()) {
                    if (stepCounterAvailable()) {
                        // Pornirea serviciului stepCounter
                        Intent serviceIntent = new Intent(BeginQuizActivity.this, StepCounterService.class);
                        startForegroundService(serviceIntent);
                    }
                    Intent intent = new Intent(BeginQuizActivity.this, QuizActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }else {
                    ActivityCompat.requestPermissions(BeginQuizActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.CAMERA, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION}, REQUEST_PERMISION);

                }
            }
        });
    }

    private boolean stepCounterAvailable() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        return stepSensor != null;
    }


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private boolean areAllPermissionsGranted() {
        return  ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
               // ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED;
    }
}

