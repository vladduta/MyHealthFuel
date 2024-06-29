package com.healthfuel.myhealthfuel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.healthfuel.myhealthfuel.mainpage.HomeFragment;


public class StepCounterService extends Service  {

    private SensorManager sensorManager;
    private Sensor stepSensor;
  //  FirebaseAuth auth;
  //  FirebaseUser user;
    private static final int NOTIFICATION_ID = 123;
    private static final String CHANNEL_ID = "StepCounterChannel";

    @Override
    public void onCreate() {
        super.onCreate();
       // auth = FirebaseAuth.getInstance();
      //  user = auth.getCurrentUser();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (stepSensor == null) {
            stopSelf(); // oprim serviciul daca nu exista senzorul de pasi pe dispozitiv
        } else {
            // pornim serviciul in foreground
            createNotificationChannel();
            startForeground(NOTIFICATION_ID, buildNotification());
        }
    }

    private Notification buildNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Step Counter Service")
                .setContentText("Service is running in the background")
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationCompat.PRIORITY_LOW);

        Intent notificationIntent = new Intent(this, HomeFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(pendingIntent);

        return builder.build();
    }
    private void createNotificationChannel() {
        CharSequence name = "Step Counter Service";
        String description = "Notification channel for Step Counter Service";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
