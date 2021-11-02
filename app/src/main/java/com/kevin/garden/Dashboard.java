package com.kevin.garden;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class Dashboard extends AppCompatActivity {

    TextView user_name, user_email;
    Button logout;

    MQTTHelper mqttHelper;

    SharedPreferences sharedPreferences;

    ChartHelper mChart;
    LineChart chart;

    TextView temperature, syncTemperature;
    TextView humidity, syncHumidity;
    TextView light, syncLight;
    TextView moisture, syncMoisture;


    TextView plantName;
    TextView maxTemp, maxHumid, maxLight, maxMois;

    Button devices, plants, schedule, reload;

    int MAX_TEMPERATUE = 0;
    int MAX_HUMIDITY = 0;
    int MAX_LIGHT = 0;
    int MAX_MOIS = 0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init();

        getLastValue("bbc-temp");
        getLastValue("bbc-humid");
        getLastValue("bbc-light");
        getLastValue("bbc-mois");

        //ensuresParameter();

        startMQTT();




        //asdaa




        devices.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Devices.class);
            startActivity(intent);
        });

        plants.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Plants.class);
            startActivity(intent);
        });


        logout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("remember", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("remember", "false");
            editor.apply();
            Toast.makeText(Dashboard.this, "Logged out!!!", Toast.LENGTH_SHORT).show();
            finish();

        });

        reload.setOnClickListener(v -> {
            getLastValue("bbc-temp");
            getLastValue("bbc-humid");
            getLastValue("bbc-light");
            getLastValue("bbc-mois");
        });


    }

    @SuppressLint("SetTextI18n")
    public void init()
    {
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);
        logout = findViewById(R.id.logout);
        mqttHelper = new MQTTHelper(getApplicationContext(), "111");
        sharedPreferences = getSharedPreferences("data_login", MODE_PRIVATE);
        user_name.setText(sharedPreferences.getString("username", ""));
        user_email.setText(sharedPreferences.getString("email", ""));

        //chart = (LineChart) findViewById(R.id.chart);
        //mChart = new ChartHelper(chart);

        temperature = findViewById(R.id.temp);
        syncTemperature = findViewById(R.id.syncTemp);

        humidity = findViewById(R.id.humid);
        syncHumidity = findViewById(R.id.syncHumid);

        light = findViewById(R.id.light);
        syncLight = findViewById(R.id.syncLight);

        moisture = findViewById(R.id.mois);
        syncMoisture = findViewById(R.id.syncMois);

        devices = findViewById(R.id.devices);
        plants = findViewById(R.id.plants);

        schedule = findViewById(R.id.schedule);

        maxTemp = findViewById(R.id.max_temperature);
        maxHumid = findViewById(R.id.max_humidity);
        maxLight = findViewById(R.id.max_light);
        maxMois = findViewById(R.id.max_moisture);



        maxLight.setText(getSharedPreferences("plant", MODE_PRIVATE)
                .getString("max_light", "####") + " cd");
        maxMois.setText(getSharedPreferences("plant", MODE_PRIVATE)
                .getString("max_mois", "####") + " %");

        String buffer = getSharedPreferences("plant", MODE_PRIVATE).getString("max_temp", "####");
        if(!buffer.equals("####"))
        {
            MAX_TEMPERATUE = Integer.parseInt(buffer);
        }
        maxTemp.setText(buffer + " °C");

        buffer = getSharedPreferences("plant", MODE_PRIVATE)
                        .getString("max_humid", "####");
        if(!buffer.equals("####"))
        {
            MAX_HUMIDITY = Integer.parseInt(buffer);
        }
        maxHumid.setText(buffer + " %");

        buffer = getSharedPreferences("plant", MODE_PRIVATE)
                .getString("max_light", "####");
        if(!buffer.equals("####"))
        {
            MAX_LIGHT = Integer.parseInt(buffer);
        }
        maxLight.setText(buffer + " cd");

        buffer = getSharedPreferences("plant", MODE_PRIVATE)
                .getString("max_mois", "####");
        if(!buffer.equals("####"))
        {
            MAX_MOIS = Integer.parseInt(buffer);
        }
        maxMois.setText(buffer + " %");




        reload = findViewById(R.id.reload);

        plantName = findViewById(R.id.plantName);
        plantName.setText(getSharedPreferences("plant", MODE_PRIVATE).getString("plant_name", "Nothing"));


//        temp.setText(sharedPreferences.getString("temp", "Waiting"));
//        syncTemp.setText("Last synced\n" + sharedPreferences.getString("syncTemp", "Waiting"));


    }

    private void startMQTT()
    {
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                Log.d("mqtt", "Message: " + message.toString());

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String time = dtf.format(now);

                String test = mqttHelper.mqttAndroidClient.getResultData();
                Log.d("mqtt", "test = " + test);


                //SharedPreferences.Editor editor = sharedPreferences.edit();
                if(topic.contains("temp")) {
                    temperature.setText(message.toString() + " °C");
                    syncTemperature.setText("Last synced\n" + time);
                    if(Integer.parseInt(message.toString()) > MAX_TEMPERATUE)
                    {
                        pushNotification("Temperature", "sdhfjdasfj");
                    }

                }
                else if(topic.contains("humid"))
                {
                    humidity.setText(message.toString() + " %");
                    syncHumidity.setText("Last synced\n" + time);
                }
                else if(topic.contains("light"))
                {
                    light.setText(message.toString() + " cd");
                    syncLight.setText("Last synced\n" + time);
                }
                else if(topic.contains("mois"))
                {
                    moisture.setText(message.toString() + " %");
                    syncMoisture.setText("Last synced\n" + time);
                }
//                else if(topic.contains("pump"))
//                {
//                    sharedPreferences = getSharedPreferences("devices", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("pump", message.toString());
//
//                }
//                else if(topic.contains("awning"))
//                {
//                    sharedPreferences = getSharedPreferences("devices", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("awning", message.toString());
//
//                }
//                else if(topic.contains("fan"))
//                {
//                    sharedPreferences = getSharedPreferences("devices", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("fan", message.toString());
//
//                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    public void sendDataToMQTT(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);
        msg.setPayload(value.getBytes(Charset.forName("UTF-8")));
        try {
            this.mqttHelper.mqttAndroidClient.publish(topic, msg);

        } catch (MqttException e) {
        }
    }

    public void pushNotification(String title, String text)
    {


        Notification notification = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(notificationManager!=null)
        {
            notificationManager.notify(getNotificationID(), notification);
        }
    }

    private int getNotificationID() {
        return (int) new Date().getTime();
    }


    public void getLastValue(String feed_key)
    {
        String url = "https://io.adafruit.com/api/v2/quocdungms/feeds/";
        url = url + feed_key + "/data";
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //Log.d("mqtt", "Response : "+ response.substring(0,250));

                        if(!response.equals(""))
                        {
                            int left = 0;
                            int right = 0;
                            left = response.indexOf("{");
                            right = response.indexOf("}");
                            String data = "";
                            if(left != right)
                            {
                                data = response.substring(left, right + 1);
                                //Log.d("mqtt", "data = " + data);
                                try {
                                    JSONObject jsonObject = new JSONObject(data);

                                    String value = jsonObject.getString("value");
                                    String time = jsonObject.getString("created_at");
                                    time = time.replace("T", " ");
                                    time = time.replace("Z", "");



                                    Log.d("mqtt", "time: "  + time);


                                    if(feed_key.contains("temp"))
                                    {
                                        temperature.setText(value + " °C");
                                        syncTemperature.setText("Last synced\n" + time);
                                        if(Integer.parseInt(value) > MAX_TEMPERATUE)
                                        {
                                            pushNotification("High Temperature", "Curent: "
                                                    + value + " °C. Max: " + MAX_TEMPERATUE + " °C");
                                        }
                                    }
                                    else if(feed_key.contains("humid"))
                                    {
                                        humidity.setText(value + " %");
                                        syncHumidity.setText("Last synced\n" + time);
                                        if(Integer.parseInt(value) > MAX_HUMIDITY)
                                        {
                                            pushNotification("High Humidity", "Curent: "
                                                    + value + " %. Max: " + MAX_HUMIDITY + " %");
                                        }
                                    }
                                    else if(feed_key.contains("light"))
                                    {
                                        light.setText(value + " cd");
                                        syncLight.setText("Last synced\n" + time);
                                        if(Integer.parseInt(value) > MAX_LIGHT)
                                        {
                                            pushNotification("High Light Intensity", "Curent: "
                                                    + value + " cd. Max: " + MAX_LIGHT + " cd");
                                        }
                                    }
                                    else if(feed_key.contains("mois"))
                                    {
                                        moisture.setText(value + " %");
                                        syncMoisture.setText("Last synced\n" + time);
                                        if(Integer.parseInt(value) > MAX_MOIS)
                                        {
                                            pushNotification("High soil moisture", "Curent: "
                                                    + value + " %. Max: " + MAX_MOIS + " %");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("mqtt", "nothing");
            }


        });


        queue.add(stringRequest);
    }

    public void ensuresParameter()
    {
        int vTemp = Integer.parseInt(temperature.getText().toString());
        Log.d("mqtt", "Vtemp: " + vTemp);
    }
}
