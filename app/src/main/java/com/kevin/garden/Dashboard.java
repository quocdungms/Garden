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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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
    TextView mediumTemp, mediumHumid, mediumLight, mediumMois;

    Button devices, plants, graph, reload;

    float MAX_TEMPERATUE = 0;
    float MAX_HUMIDITY = 0;
    float MAX_LIGHT = 0;
    float MAX_MOIS = 0;

    float MIN_TEMPERATUE = 0;
    float MIN_HUMIDITY = 0;
    float MIN_LIGHT = 0;
    float MIN_MOIS = 0;

    float MEDIUM_TEMPERATUE = 0;
    float MEDIUM_HUMIDITY = 0;
    float MEDIUM_LIGHT = 0;
    float MEDIUM_MOIS = 0;





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
            editor.putString("logout", "true");
            editor.apply();

            SharedPreferences s = getSharedPreferences("data_login", MODE_PRIVATE);
            SharedPreferences.Editor editor1 = s.edit();
            editor1.putBoolean("newSession", false);
            editor1.apply();
            Toast.makeText(Dashboard.this, "Logged out!!!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Dashboard.this, MainActivity.class);
            startActivity(intent);
            finish();

        });

        graph.setOnClickListener(v -> {
            Intent intent = new Intent(Dashboard.this, Graph.class);
            startActivity(intent);
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

        graph = findViewById(R.id.graph);

        mediumTemp = findViewById(R.id.medium_temperature);
        mediumHumid = findViewById(R.id.medium_humidity);
        mediumLight = findViewById(R.id.medium_light);
        mediumMois = findViewById(R.id.medium_moisture);


        getPlantDetails();
//        mediumLight.setText(getSharedPreferences("plant", MODE_PRIVATE)
//                .getString("max_light", "####") + " cd");
//        mediumMois.setText(getSharedPreferences("plant", MODE_PRIVATE)
//                .getString("max_mois", "####") + " %");








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

                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String time = dtf.format(now);

                String test = mqttHelper.mqttAndroidClient.getResultData();
                Log.d("mqtt", "test = " + test);


                //SharedPreferences.Editor editor = sharedPreferences.edit();
                if(topic.contains("temp")) {
                    temperature.setText(message.toString() + " °C");
                    syncTemperature.setText("Last synced\n" + time);
                    if(Float.parseFloat(message.toString()) > MAX_TEMPERATUE)
                    {
                        pushNotification("High Temperature", "Current: " + message.toString()
                                + " °C. Max: " + MAX_TEMPERATUE);
                        //turnOnFan(10000);
                        closeAwning();
                    }

                }
                else if(topic.contains("humid"))
                {
                    humidity.setText(message.toString() + " %");
                    syncHumidity.setText("Last synced\n" + time);
                    if(Float.parseFloat(message.toString()) > MAX_HUMIDITY)
                    {
                        pushNotification("High Humidity", "Current: " + message.toString()
                                + " %. Max: " + MAX_HUMIDITY);
                        turnOnFan(10000);
                    }

                }
                else if(topic.contains("light"))
                {
                    light.setText(message.toString() + " cd");
                    syncLight.setText("Last synced\n" + time);
                    if(Float.parseFloat(message.toString()) > MAX_LIGHT)
                    {
                        pushNotification("High Light Intensity", "Current: " + message.toString()
                                + " %. Max: " + MAX_LIGHT);
                        closeAwning();
                    }


                }
                else if(topic.contains("mois"))
                {
                    moisture.setText(message.toString() + " %");
                    syncMoisture.setText("Last synced\n" + time);
                    if(Float.parseFloat(message.toString()) > MAX_MOIS)
                    {
                        pushNotification("High Soil Moisture", "Current: " + message.toString()
                                + " %. Max: " + MAX_MOIS);

                    }
                    else if(Float.parseFloat(message.toString()) < MIN_MOIS)
                    {
                        pushNotification("Low Soil Moisture", "Current: " + message.toString()
                                + " %. Min: " + MIN_MOIS);
                        turnOnPump(10000);
                    }
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

                                    DateFormat utc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    utc.setTimeZone(TimeZone.getTimeZone("UTC"));

                                    Date date = utc.parse(time);
                                    DateFormat ptsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    ptsFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                                    assert date != null;
                                    String result = ptsFormat.format(date);
                                    Log.d("mqtt", "result: " + result);



                                    if(feed_key.contains("temp"))
                                    {
                                        temperature.setText(value + " °C");
                                        syncTemperature.setText("Last synced\n" + result);
                                        if(Integer.parseInt(value) > MAX_TEMPERATUE)
                                        {
                                            pushNotification("High Temperature", "Current: "
                                                    + value + " °C. Max: " + MAX_TEMPERATUE + " °C");
                                            closeAwning();
                                        }
                                    }
                                    else if(feed_key.contains("humid"))
                                    {
                                        humidity.setText(value + " %");
                                        syncHumidity.setText("Last synced\n" + result);
                                        if(Integer.parseInt(value) > MAX_HUMIDITY)
                                        {
                                            pushNotification("High Humidity", "Current: "
                                                    + value + " %. Max: " + MAX_HUMIDITY + " %");
                                            turnOnFan(10000);
                                        }
                                    }
                                    else if(feed_key.contains("light"))
                                    {
                                        light.setText(value + " cd");
                                        syncLight.setText("Last synced\n" + result);
                                        if(Integer.parseInt(value) > MAX_LIGHT)
                                        {
                                            pushNotification("High Light Intensity", "Current: "
                                                    + value + " cd. Max: " + MAX_LIGHT + " cd");
                                            closeAwning();
                                        }
                                    }
                                    else if(feed_key.contains("mois"))
                                    {
                                        moisture.setText(value + " %");
                                        syncMoisture.setText("Last synced\n" + result);
                                        if(Integer.parseInt(value) > MAX_MOIS)
                                        {
                                            pushNotification("High soil moisture", "Current: "
                                                    + value + " %. Max: " + MAX_MOIS + " %");
                                        }
                                        else if(Float.parseFloat(value) < MIN_MOIS)
                                        {
                                            pushNotification("Low soil moisture", "Current: "
                                                    + value + " %. Min: " + MIN_MOIS + " %");
                                            turnOnPump(10000);
                                        }
                                    }
                                } catch (JSONException | ParseException | InterruptedException e) {
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
    public void getPlantDetails()
    {
        SharedPreferences sp = getSharedPreferences("plant", MODE_PRIVATE);
        String buffer = sp.getString("medium_temp", "37");
        if(!buffer.equals("37"))
        {
            MEDIUM_TEMPERATUE = Float.parseFloat(buffer);
        }
        mediumTemp.setText(buffer + " °C");

        buffer = sp.getString("medium_humid", "70");
        if(!buffer.equals("70"))
        {
            MEDIUM_HUMIDITY = Float.parseFloat(buffer);
        }
        mediumHumid.setText(buffer + " %");

        buffer = sp.getString("medium_light", "50");
        if(!buffer.equals("50"))
        {
            MEDIUM_LIGHT = Float.parseFloat(buffer);
        }
        mediumLight.setText(buffer + " cd");

        buffer = sp.getString("medium_mois", "67");
        if(!buffer.equals("67"))
        {
            MEDIUM_MOIS = Float.parseFloat(buffer);
        }
        mediumMois.setText(buffer + " %");


        buffer = sp.getString("max_temp", "100");
        if(!buffer.equals("100"))
        {
            MAX_TEMPERATUE = Float.parseFloat(buffer);
        }
        buffer = sp.getString("max_humid", "100");
        if(!buffer.equals("100"))
        {
            MAX_HUMIDITY = Float.parseFloat(buffer);
        }
        buffer = sp.getString("max_light", "100");
        if(!buffer.equals("100"))
        {
            MAX_LIGHT = Float.parseFloat(buffer);
        }
        buffer = sp.getString("max_mois", "100");
        if(!buffer.equals("100"))
        {
            MAX_MOIS = Float.parseFloat(buffer);
        }

        buffer = sp.getString("min_temp", "0");
        if(!buffer.equals("0"))
        {
            MIN_TEMPERATUE = Float.parseFloat(buffer);
        }
        buffer = sp.getString("max_humid", "0");
        if(!buffer.equals("0"))
        {
            MIN_HUMIDITY = Float.parseFloat(buffer);
        }
        buffer = sp.getString("max_light", "0");
        if(!buffer.equals("0"))
        {
            MIN_LIGHT = Float.parseFloat(buffer);
        }
        buffer = sp.getString("max_mois", "0");
        if(!buffer.equals("0"))
        {
            MIN_MOIS = Float.parseFloat(buffer);
        }

    }
    public void turnOnPump(int miliSecond) throws InterruptedException {
        sendDataToMQTT("bbc-pump", "1");
        Toast.makeText(Dashboard.this, "Turn on pump " + miliSecond/1000 + "s", Toast.LENGTH_SHORT).show();
        pushNotification("Low soil moisture", "Turn on pump " + miliSecond/1000 + "s");
        Thread.sleep(miliSecond);
        sendDataToMQTT("bbc-pump", "0");
    }

    public void turnOnFan(int miliSecond) throws InterruptedException {
        sendDataToMQTT("quocdungms/feeds/bbc-fan", "1");
        Toast.makeText(Dashboard.this, "Turn on fan " + miliSecond/1000 + "s", Toast.LENGTH_SHORT).show();
        pushNotification("High humuity", "Turn on fan " + miliSecond/1000 + "s");
        Thread.sleep(miliSecond);
        sendDataToMQTT("quocdungms/feeds/bbc-fan", "0");
    }

    public void openAwning(){
        sendDataToMQTT("bbc-awning", "1");
        Toast.makeText(Dashboard.this, "Open awning ",Toast.LENGTH_SHORT).show();
        pushNotification("Low light intensity", "Open awning ");
    }

    public void closeAwning(){
        sendDataToMQTT("bbc-awning", "1");
        Toast.makeText(Dashboard.this, "Close awning ",Toast.LENGTH_SHORT).show();
        pushNotification("High light intensity", "Close awning ");
    }
}
