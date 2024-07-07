package com.example.el_fahim_sana_projet_dynamic_fit;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private Marker userMarker; // Marqueur pour représenter l'utilisateur
    private Polyline polyline; // Polyline pour représenter le chemin parcouru

    private static final int REQUEST_CODE = 101;
    private List<LatLng> pathPoints;

    private Location previousLocation;
    private double totalDistance; // Distance totale parcourue
    private double currentSpeed; // Vitesse actuelle

    private TextView distanceTextView;
    private TextView speedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        pathPoints = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.myMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        distanceTextView = findViewById(R.id.distanceTextView); // Référence à TextView dans le layout
        speedTextView = findViewById(R.id.speedTextView); // Référence à TextView dans le layout

        createLocationRequest();

        // Initier le bouton pour retourner à l'écran d'accueil
        Button homeButton = findViewById(R.id.homeButton);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent9 = new Intent(MapsActivity.this, MainActivity.class);
                startActivity(intent9);
                finish();
            }
        });

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    Log.d("MapsActivity", "Location result is null");
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    Log.d("MapsActivity", "Location received: " + location.toString());
                    updateLocation(location);
                }
            }
        };
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // 10 seconds
        locationRequest.setFastestInterval(5000); // 5 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateLocation(Location location) {
        if (location == null) {
            Log.d("MapsActivity", "Location is null");
            return;
        }
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("MapsActivity", "Location updated: " + latLng.toString());

        if (userMarker == null) {
            initializeMarkerAndPolyline(latLng);
        } else {
            updateMarkerPosition(latLng);
        }

        float distanceInMeters = calculateDistance(previousLocation, location);
        totalDistance += distanceInMeters;
        currentSpeed = calculateSpeed(previousLocation, location);

        updatePolyline(latLng);
        updatePreviousLocation(location);
        updateDistanceTextView();
        updateSpeedTextView();
    }

    private float calculateDistance(Location previousLocation, Location currentLocation) {
        if (previousLocation == null) {
            return 0;
        }
        return previousLocation.distanceTo(currentLocation);
    }

    private double calculateSpeed(Location previousLocation, Location currentLocation) {
        if (previousLocation == null) {
            return 0;
        }
        double timeDiffSeconds = (currentLocation.getTime() - previousLocation.getTime()) / 1000.0; // en secondes
        if (timeDiffSeconds > 0) {
            float distanceInMeters = calculateDistance(previousLocation, currentLocation);
            return distanceInMeters / timeDiffSeconds; // Vitesse en m/s
        } else {
            return 0;
        }
    }

    private void initializeMarkerAndPolyline(LatLng latLng) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Départ")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        userMarker = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

        PolylineOptions polylineOptions = new PolylineOptions()
                .width(10)
                .color(Color.BLUE)
                .geodesic(true);
        polyline = mMap.addPolyline(polylineOptions);

        previousLocation = new Location("initial");
        previousLocation.setLatitude(latLng.latitude);
        previousLocation.setLongitude(latLng.longitude);
    }

    private void updateMarkerPosition(LatLng latLng) {
        userMarker.setPosition(latLng);
    }

    private void updatePolyline(LatLng latLng) {
        pathPoints.add(latLng);
        polyline.setPoints(pathPoints);
    }

    private void updatePreviousLocation(Location location) {
        previousLocation = location;
    }

    private void updateDistanceTextView() {
        DecimalFormat df = new DecimalFormat("#.##");
        String distanceText = "Distance : " + df.format(totalDistance) + " m"; // Afficher la distance en mètres
        distanceTextView.setText(distanceText);
    }

    private void updateSpeedTextView() {
        DecimalFormat df = new DecimalFormat("#.#");
        String speedText = "Vitesse : " + df.format(currentSpeed) + " m/s"; // Afficher la vitesse en mètres par seconde
        speedTextView.setText(speedText);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        mMap.setMyLocationEnabled(true); // Activer le bouton "Ma position"

        // Démarrer les mises à jour de localisation
        startLocationUpdates();
    }

    @Override
    protected void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
