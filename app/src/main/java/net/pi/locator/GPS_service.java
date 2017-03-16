package net.pi.locator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by filipp on 6/16/2016.
 */
public class GPS_service extends Service {

    public static final String PREFS = "examplePrefs";

    private LocationListener listener;
    private LocationManager locationManager;

    double latitude;
    double longitude;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = location.getLatitude();
                longitude = location.getLongitude();

                Intent i = new Intent("location_update");
                i.putExtra("coordinates","Lng: "+location.getLongitude()+"\n"+"Lat: "+location.getLatitude());
                sendBroadcast(i);

                sendData();

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        //noinspection MissingPermission
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,0,listener);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(locationManager != null){
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
        }
    }

    public void sendData() {
        new Thread() {
            public void run() {

                OkHttpClient client = new OkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");

                SharedPreferences example = getSharedPreferences(PREFS, 0);
                String userDevice = example.getString("deviceID", "Device ID seems to be wrong");
                String userHostname = example.getString("hostName", "Host Name seems to be wrong");
                String mobilefNo = example.getString("MobileNo", "Mobile no seems to be wrong");

                //MyNewDevice
                //imran-iot-labs.azure-devices.net

                final RequestBody body = RequestBody.create(mediaType, "{\n\"Longitude\": "+longitude+",\n\"Latitude\": "+latitude+",\n\"Mobile\": "+mobilefNo+",\n\"In/out\": \"In\"\n}");
                Request request = new Request.Builder()
                        .url("https://"+userHostname+"/devices/"+userDevice+"/messages/events?api-version=2016-02-03")
                        .post(body)
                        .addHeader("authorization", "SharedAccessSignature sr=imran-iot-labs.azure-devices.net&sig=KmiOZUA5wJpJXeTKX3PDQxoOZG%2BjT%2BT%2BTLbqU%2BG%2FaMU%3D&se=1519197378&skn=iothubowner")
                        .addHeader("content-type", "application/json")
                        .addHeader("cache-control", "no-cache")
                        .build();
                try {
                    Response response = client.newCall(request).execute();
                    response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

}
