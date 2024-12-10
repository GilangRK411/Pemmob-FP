package com.example.pemmob_fp;

import android.os.Bundle;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainPageActivity extends AppCompatActivity {

    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor lightSensor;
    private LocationManager locationManager;
    private SoundPool soundPool;
    private int soundShake, soundLight;

    // Flags to check if sound has already been played
    private boolean isShakeSoundPlayed = false;
    private boolean isLightSoundPlayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        // Find the TextView by its ID
        TextView textView = findViewById(R.id.textViewTerimakasih);
        textView.setText("Thank you for using EcoTrack!");

        // Initialize sensor manager and other components
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Initialize SoundPool
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundShake = soundPool.load(this, R.raw.sound2, 1); // Sound for shaking
        soundLight = soundPool.load(this, R.raw.sound3, 1); // Sound for light detection

        // Register Accelerometer Sensor for shaking detection
        if (accelerometer != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float x = event.values[0];
                    float y = event.values[1];
                    float z = event.values[2];

                    // Calculate the movement vector (magnitude of acceleration)
                    float magnitude = (float) Math.sqrt(x * x + y * y + z * z);

                    // If magnitude exceeds a threshold (e.g., 15), it's considered a shake
                    if (magnitude > 15 && !isShakeSoundPlayed) {
                        soundPool.play(soundShake, 1, 1, 0, 0, 1); // Play shake sound
                        isShakeSoundPlayed = true; // Set flag to true after playing sound
                    }

                    // Log accelerometer values (optional)
                    Log.d("Accelerometer", "X: " + x + ", Y: " + y + ", Z: " + z + ", Magnitude: " + magnitude);
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            }, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("Accelerometer", "Accelerometer is not available!");
        }

        // Register Light Sensor for light intensity detection
        if (lightSensor != null) {
            sensorManager.registerListener(new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    float light = event.values[0];

                    // Log light intensity values
                    Log.d("LightSensor", "Light Intensity: " + light + " lux");

                    // If light intensity is above a threshold (e.g., 100 lux), play light sound
                    if (light > 100 && !isLightSoundPlayed) {
                        soundPool.play(soundLight, 1, 1, 0, 0, 1); // Play light sound
                        isLightSoundPlayed = true; // Set flag to true after playing sound
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {}
            }, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Log.e("LightSensor", "Light sensor is not available!");
        }

        // Location updates (optional, not used for sound)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    Log.d("GPS", "Latitude: " + latitude + ", Longitude: " + longitude);
                } else {
                    Log.e("GPS", "Location not available.");
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release(); // Release resources
    }

    // Method to reset sound flags (optional, if you want to allow sounds to play again later)
    public void resetSoundFlags() {
        isShakeSoundPlayed = false;
        isLightSoundPlayed = false;
    }
}
