package com.example.el_fahim_sana_projet_dynamic_fit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.SleepSegmentRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class BackgroundDetectedActivity extends Service {

    private PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;

    IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {

        public BackgroundDetectedActivity getServerInstance() {
            return BackgroundDetectedActivity.this;
        }
    }

    public BackgroundDetectedActivity() {

    }

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    public void onCreate() {
        super.onCreate();
        activityRecognitionClient = new ActivityRecognitionClient() {
            @NonNull
            @Override
            public ApiKey<Api.ApiOptions.NoOptions> getApiKey() {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> requestActivityUpdates(long l, @NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> removeActivityUpdates(@NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> requestActivityTransitionUpdates(@NonNull ActivityTransitionRequest activityTransitionRequest, @NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> removeActivityTransitionUpdates(@NonNull PendingIntent pendingIntent) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> requestSleepSegmentUpdates(@NonNull PendingIntent pendingIntent, @NonNull SleepSegmentRequest sleepSegmentRequest) {
                return null;
            }

            @NonNull
            @Override
            public Task<Void> removeSleepSegmentUpdates(@NonNull PendingIntent pendingIntent) {
                return null;
            }
        };
        Intent intentService = new Intent(this, DetectedActivityService.class);
        pendingIntent = PendingIntent.getService(this, 1, intentService, PendingIntent.FLAG_UPDATE_CURRENT);
        requestActivityUpdatesButtonHandler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void requestActivityUpdatesButtonHandler() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Void> task = activityRecognitionClient.requestActivityUpdates(10000, pendingIntent);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {

            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(), "Mises à jour d'activité demandées avec succès", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "La demande de mises à jour d'activité n'a pas pu démarrer", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        removeActivityUpdate();
    }

    public void removeActivityUpdate() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Void> task = activityRecognitionClient.removeActivityUpdates(pendingIntent);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(getApplicationContext(), "Mises à jour d'activité supprimées avec succès !", Toast.LENGTH_SHORT).show();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Échec de la suppression des mises à jour d'activité!", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
