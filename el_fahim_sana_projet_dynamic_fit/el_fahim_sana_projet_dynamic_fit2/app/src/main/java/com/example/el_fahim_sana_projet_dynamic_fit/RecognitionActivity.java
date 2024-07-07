package com.example.el_fahim_sana_projet_dynamic_fit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RecognitionActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private TableLayout tableLayout;
    private float[] gravity = new float[3];
    private float[] linear_acceleration = new float[3];
    private int[] confidences = new int[4]; // Réduit à 4 pour les activités restantes
    private Button homeButton;
    private TextView confidenceDebout, confidenceAssis, confidenceMarcher, confidenceSauter;
    private DBHelper myDB;
    private ActivityHistory currentActivity = null;
    private long lastRecordTime = 0;
    private static final long RECORD_INTERVAL = 5 * 1000; // 5 secondes en millisecondes

    // Notification ID pour identifier la notification
    private static final int NOTIFICATION_ID = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        myDB = new DBHelper(this);

        // Initialisation des vues
        confidenceDebout = findViewById(R.id.debout);
        confidenceAssis = findViewById(R.id.assis);
        confidenceMarcher = findViewById(R.id.marche);
        confidenceSauter = findViewById(R.id.saut);
        homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(RecognitionActivity.this, MainActivity.class);
                startActivity(intent9);
                finish();
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            final float alpha = 0.9f;

            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

            linear_acceleration[0] = event.values[0] - gravity[0];
            linear_acceleration[1] = event.values[1] - gravity[1];
            linear_acceleration[2] = event.values[2] - gravity[2];

            long currentTime = System.currentTimeMillis();
            int activite = 0;
            if (currentTime - lastRecordTime >= RECORD_INTERVAL) {
                activite = TypeActivite(linear_acceleration);
                confidences[activite]++;

                String activityName = getActivityName(activite);
                String currentDateTime = getCurrentDateTime();

                if (currentActivity == null || !currentActivity.getActivityName().equals(activityName)) {
                    if (currentActivity != null) {
                        currentActivity.setEndDateTime(currentDateTime);
                        myDB.insertActivity(currentActivity.getActivityName(), currentActivity.getStartDateTime(), currentActivity.getEndDateTime());
                    }
                    currentActivity = new ActivityHistory(activityName, currentDateTime, null);
                }

                updateConfidenceDisplay();
                showNotification(activityName); // Toujours afficher la notification lorsqu'une activité est détectée

                lastRecordTime = currentTime;
            }
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Méthode non utilisée dans cet exemple
    }

    private void updateConfidenceDisplay() {
        int totalConfidence = 0;
        for (int value : confidences) {
            totalConfidence += value;
        }
        if (totalConfidence > 0) {
            confidenceAssis.setText(String.format("%d%%", (confidences[0] * 100 / totalConfidence)));
            confidenceDebout.setText(String.format("%d%%", (confidences[1] * 100 / totalConfidence)));
            confidenceMarcher.setText(String.format("%d%%", (confidences[2] * 100 / totalConfidence)));
            confidenceSauter.setText(String.format("%d%%", (confidences[3] * 100 / totalConfidence)));
        }
    }

    private int TypeActivite(float[] acceleration) {
        float x = acceleration[0];
        float y = acceleration[1];
        float z = acceleration[2];
        float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

        if (magnitude < 0.1f) return 0; // Assis
        else if (magnitude < 5.0f) return 1; // Debout
        else if (magnitude < 9.0f) return 2; // Marcher
        else return 3; // Sauter
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    private String getActivityName(int activity) {
        switch (activity) {
            case 0:
                return "Assis";
            case 1:
                return "Debout";
            case 2:
                return "Marcher";
            case 3:
                return "Sauter";
            default:
                return "Inconnu";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        lastRecordTime = System.currentTimeMillis(); // Mettre à jour le temps de dernier enregistrement à la reprise de l'activité
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (currentActivity != null) {
            currentActivity.setEndDateTime(getCurrentDateTime());
            myDB.insertActivity(currentActivity.getActivityName(), currentActivity.getStartDateTime(), currentActivity.getEndDateTime());
            currentActivity = null;
        }
    }

    // Classe pour représenter l'historique des activités
    private static class ActivityHistory {
        private String activityName;
        private String startDateTime;
        private String endDateTime;

        ActivityHistory(String activityName, String startDateTime, String endDateTime) {
            this.activityName = activityName;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
        }

        public String getActivityName() {
            return activityName;
        }

        public String getStartDateTime() {
            return startDateTime;
        }

        public String getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(String endDateTime) {
            this.endDateTime = endDateTime;
        }
    }

    // Méthode pour afficher la notification
    private void showNotification(String activityName) {
        // Création d'un canal pour les notifications (obligatoire pour Android Oreo et versions supérieures)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("activity_channel", "Activity Channel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Notification pour l'activité détectée");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Création de la notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "activity_channel")
                .setSmallIcon(R.drawable.ic_notification) // ic_notification est votre icône de notification personnalisée
                .setContentTitle("Activité détectée")
                .setContentText("Vous êtes en train de " + activityName)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Affichage de la notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // Vérification de la permission pour les notifications
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED) {
            // Si la permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.VIBRATE}, 1);
        } else {
            // Si la permission est accordée, affichez la notification
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }
}
