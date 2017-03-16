package net.pi.locator;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;
import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by ilan on 3/7/2017.
 */

public class MyService extends Service {

    public static final String PREFS = "examplePrefs";

    public boolean isRunning = false;
    final class MyThreadClass implements Runnable
    {
        int service_id;
        MyThreadClass(int service_id)
        {
            this.service_id = service_id;
        }

        static final long DELAY = 3000;

        @Override
        public void run() {
            synchronized (this) {
                while (isRunning) {
                    try {
                        sendData();
                        Thread.sleep(DELAY);
                    } catch (InterruptedException e) {
                        isRunning = false;
                        e.printStackTrace();
                    }
                }
                stopSelf(service_id);
            }
        }
    }

    public void sendData(){
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");

        SharedPreferences example = getSharedPreferences(PREFS, 0);
        String userDevice = example.getString("deviceID", "Device ID seems to be wrong");
        String userHostname = example.getString("hostName", "Host Name seems to be wrong");

        //MyNewDevice
        //imran-iot-labs.azure-devices.net

        final RequestBody body = RequestBody.create(mediaType, "{\r\n\"Longitude\": 9.96233,\r\n\"Latitude\": 49.80404\r\n}");
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



    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId) {

        Toast.makeText(this, "Service started...", Toast.LENGTH_LONG).show();

        Thread thread = new Thread(new MyThreadClass(startId));
        thread.start();
        isRunning = true;
        return START_STICKY;
    }



    @Override
    public synchronized void onDestroy() {

        isRunning = false;
        Toast.makeText(this, "Service stopped...", Toast.LENGTH_LONG).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
