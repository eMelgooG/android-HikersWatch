package com.example.hikerswatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    DecimalFormat df = new DecimalFormat("#.##");
    DecimalFormat dfLatLong = new DecimalFormat("#.######");
    LocationManager locationManager;
    LocationListener locationListener;
    TextView informationTV;

    private void updateLocation(Location location) {
        String data = "";
        data += "Latitude: " + dfLatLong.format(location.getLatitude()) + "\r\n\r\n";
        data += "Longitude: " + dfLatLong.format(location.getLongitude()) + "\r\n\r\n";
        data += "Accuracy: " + df.format(location.getAccuracy()) + "\r\n\r\n";
        data += "Altitude: " + df.format(location.getAltitude()) + "\r\n\r\n";

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "Could not find address :(";
        try {
            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (listAddresses != null && listAddresses.size() > 0) {
                address = "";

                if (listAddresses.get(0).getThoroughfare() != null) {
                    address += "Address: \r\n";
                    address += listAddresses.get(0).getThoroughfare() + " ";
                }

                if (listAddresses.get(0).getFeatureName() != null) {
                    address += listAddresses.get(0).getFeatureName() + "\r\n";
                }
                if (listAddresses.get(0).getPostalCode() != null) {
                    address += listAddresses.get(0).getPostalCode() + " ";
                }
                if (listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        data += address;
        informationTV.setText(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        informationTV = findViewById(R.id.informationTV);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (lastKnownLocation != null) {
                    updateLocation(lastKnownLocation);
                }

            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
}

