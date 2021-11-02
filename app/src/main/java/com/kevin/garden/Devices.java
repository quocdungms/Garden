package com.kevin.garden;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class Devices extends AppCompatActivity {



    TextView username, email;

    Switch sw_pump, sw_awning, sw_fan;

    Button home, plant, schedule;


    MQTTHelper mqttHelper;

    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);



        initial();

        getLastValue("bbc-pump");

        getLastValue("bbc-fan");

        getLastValue("bbc-awning");

        mqttHelper = new MQTTHelper(getApplicationContext(), "111");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("mqtt", "Topic: " + topic+ " Message: " + message.toString());
                if(message.toString().equals("1"))
                {
                    if(topic.contains("pump"))
                    {
                        sw_pump.setChecked(true);
                    }
                    else if(topic.contains("awning"))
                    {
                        sw_awning.setChecked(true);
                    }
                    else if(topic.contains("fan"))
                    {
                        sw_fan.setChecked(true);
                    }
                }
                else if(message.toString().equals("0"))
                {
                    if(topic.contains("pump"))
                    {
                        sw_pump.setChecked(false);
                    }
                    else if(topic.contains("awning"))
                    {
                        sw_awning.setChecked(false);
                    }
                    else if(topic.contains("fan"))
                    {
                        sw_fan.setChecked(false);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });


        sw_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(Devices.this, "Water pump is ON", Toast.LENGTH_SHORT).show();
                    sendDataToMQTT("quocdungms/feeds/bbc-pump", "1");


                }
                else
                {
                    Toast.makeText(Devices.this, "Water pump is OFF", Toast.LENGTH_SHORT).show();
                    sendDataToMQTT("quocdungms/feeds/bbc-pump", "0");

                }
            }
        });

        sw_awning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(Devices.this, "Awning is OPEN", Toast.LENGTH_SHORT).show();
                    sendDataToMQTT("quocdungms/feeds/bbc-awning", "1");

                }
                else
                {
                    Toast.makeText(Devices.this, "Awning is CLOSE", Toast.LENGTH_SHORT).show();
                    sendDataToMQTT("quocdungms/feeds/bbc-awning", "0");

                }
            }
        });

        sw_fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(Devices.this, "Fan is ON", Toast.LENGTH_SHORT).show();
                    //sendDataToMQTT("quocdungms/feeds/bbc-fan", "1");

                }
                else
                {
                    Toast.makeText(Devices.this, "Fan is OFF", Toast.LENGTH_SHORT).show();
                    sendDataToMQTT("quocdungms/feeds/bbc-fan", "0");
                }
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Devices.this, Dashboard.class);
                startActivity(intent);

            }
        });

        plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Devices.this, Plants.class);
                startActivity(intent);
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    public void initial()
    {
        username = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);
        username.setText(getSharedPreferences("data_login", MODE_PRIVATE).getString("username", ""));
        email.setText(getSharedPreferences("data_login", MODE_PRIVATE).getString("email", ""));

        sw_pump = findViewById(R.id.sw_pump);
        sw_awning = findViewById(R.id.sw_awning);
        sw_fan = findViewById(R.id.sw_fan);

        home = findViewById(R.id.home);
        plant = findViewById(R.id.plants);
        schedule = findViewById(R.id.schedule);



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



    public void getLastValue(String feed_key)
    {
        String url = "https://io.adafruit.com/api/v2/quocdungms/feeds/";
        url = url + feed_key + "/data";
        RequestQueue queue = Volley.newRequestQueue(this);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.d("mqtt", "Response : "+ response.substring(0,250));

                        if(!response.equals("")) {
                            int left = 0;
                            int right = 0;
                            left = response.indexOf("{");
                            right = response.indexOf("}");
                            String data = "";
                            if (left != right) {
                                data = response.substring(left, right + 1);
                                Log.d("mqtt", "data = " + data);

                                try {
                                    JSONObject jsonObject = new JSONObject(data);
                                    String value = jsonObject.getString("value");
                                    Log.d("mqtt", "value = " + value);
                                    if(Integer.parseInt(value) == 1)
                                    {
                                        if(feed_key.contains("pump"))
                                        {
                                            sw_pump.setChecked(true);
                                        }
                                        else if(feed_key.contains("awning"))
                                        {
                                            sw_awning.setChecked(true);
                                        }
                                        else if(feed_key.contains("fan"))
                                        {
                                            sw_fan.setChecked(true);
                                        }

                                    }
                                    else if(Integer.parseInt(value) == 0)
                                    {
                                        if(feed_key.contains("pump"))
                                        {
                                            sw_pump.setChecked(false);
                                        }
                                        else if(feed_key.contains("awning"))
                                        {
                                            sw_awning.setChecked(false);
                                        }
                                        else if(feed_key.contains("fan"))
                                        {
                                            sw_fan.setChecked(false);
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
}