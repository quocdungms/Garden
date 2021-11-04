package com.kevin.garden;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Graph extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    TextView user_name, user_email;

    LineChart chart;


    private int max = 10;
    private int count = 0;

    Button temp, humid, light, mois;

    Button home, devices, plant;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        init();


//        LineDataSet lineDataSet = new LineDataSet(dataValues(), "Data1");
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        dataSets.add(lineDataSet);
//
//        LineData data = new LineData(dataSets);
//        chart.setData(data);
//        chart.invalidate();

        drawFeedsGraph("bbc-temp");
        temp.setTextColor(Color.parseColor("#FF0000"));
        humid.setTextColor(Color.parseColor("#000000"));
        light.setTextColor(Color.parseColor("#000000"));
        mois.setTextColor(Color.parseColor("#000000"));

        temp.setOnClickListener(v -> {
            drawFeedsGraph("bbc-temp");
            temp.setTextColor(Color.parseColor("#FF0000"));
            humid.setTextColor(Color.parseColor("#000000"));
            light.setTextColor(Color.parseColor("#000000"));
            mois.setTextColor(Color.parseColor("#000000"));
        });

        humid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFeedsGraph("bbc-humid");
                humid.setTextColor(Color.parseColor("#FF00BCD4"));
                temp.setTextColor(Color.parseColor("#000000"));
                light.setTextColor(Color.parseColor("#000000"));
                mois.setTextColor(Color.parseColor("#000000"));
            }
        });

        light.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFeedsGraph("bbc-light");
                temp.setTextColor(Color.parseColor("#000000"));
                humid.setTextColor(Color.parseColor("#000000"));
                light.setTextColor(Color.parseColor("#ECDA42"));
                mois.setTextColor(Color.parseColor("#000000"));
            }
        });

        mois.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawFeedsGraph("bbc-mois");
                temp.setTextColor(Color.parseColor("#000000"));
                humid.setTextColor(Color.parseColor("#000000"));
                light.setTextColor(Color.parseColor("#000000"));
                mois.setTextColor(Color.parseColor("#CA451B"));
            }
        });


        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, Dashboard.class);
                startActivity(intent);
            }
        });

        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, Devices.class);
                startActivity(intent);
            }
        });

        plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Graph.this, Plants.class);
                startActivity(intent);
            }
        });


    }
    private ArrayList<Entry> dataValues()
    {
        ArrayList<Entry> dataVals = new ArrayList<Entry>();
        dataVals.add(new Entry(3, 1));
        dataVals.add(new Entry(1, 3));
        dataVals.add(new Entry(2, 4));
        dataVals.add(new Entry(3, 6));
        dataVals.add(new Entry(5, 7));

        return dataVals;

    }

    public void init()
    {
        sharedPreferences = getSharedPreferences("data_login", MODE_PRIVATE);
        user_name = findViewById(R.id.user_name);
        user_email = findViewById(R.id.user_email);

        user_name.setText(sharedPreferences.getString("username", ""));
        user_email.setText(sharedPreferences.getString("email", ""));

        chart  = findViewById(R.id.chart_temp);

        temp = findViewById(R.id.temp);
        humid = findViewById(R.id.humid);
        light = findViewById(R.id.light);
        mois = findViewById(R.id.mois);

        home = findViewById(R.id.home);
        devices = findViewById(R.id.devices);
        plant = findViewById(R.id.plant);
    }


    public void drawFeedsGraph(String feed_key) {


        String url = "https://io.adafruit.com/api/v2/quocdungms/feeds/";
        url = url + feed_key + "/data/chart";
        RequestQueue queue = Volley.newRequestQueue(this);



        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {


                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(String response) {

                        // Display the first 500 characters of the response string.
                        Log.d("mqtt", "Response : " + response);

                        try {
                            JSONObject object = new JSONObject(response);
                            JSONArray array = object.getJSONArray("data");


                            ArrayList<Entry> dataVals = new ArrayList<Entry>();
                            int size = array.length();
                            if (size != 0) {
//                                if (max > size) {
//                                    count = size;
//                                    Log.d("mqtt", "count: " + count);
//                                } else {
//                                    count = max;
//                                }
                                String t = "";
                                int left = 0;
                                int index = 1;
                                for (int i = size-max; i < size; i++) {

                                    t = array.getString(i).toString();
                                    left = t.indexOf(",");
                                    t = t.substring(left + 1);
                                    t = t.replace("\"", "");
                                    t = t.replace("]", "");
                                    Log.d("mqtt", "t = " + t);

                                    dataVals.add(new Entry(index++, Float.parseFloat(t)));


                                }

                                LineDataSet lineDataSet = null;
                                if(feed_key.contains("temp"))
                                {
                                    lineDataSet = new LineDataSet(dataVals, "Temperature");
                                    lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                    lineDataSet.setColor(Color.rgb(255, 0, 0));
                                    //set.setCircleColor(Color.WHITE);
                                    lineDataSet.setLineWidth(2f);
                                    //set.setCircleRadius(4f);
                                    lineDataSet.setFillAlpha(65);
                                    lineDataSet.setFillColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setHighLightColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextSize(9f);
                                    lineDataSet.setDrawValues(false);
                                }
                                else if(feed_key.contains("humid"))
                                {
                                    lineDataSet = new LineDataSet(dataVals, "Humidity");
                                    lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                    lineDataSet.setColor(Color.rgb(0, 188, 212));
                                    //set.setCircleColor(Color.WHITE);
                                    lineDataSet.setLineWidth(2f);
                                    //set.setCircleRadius(4f);
                                    lineDataSet.setFillAlpha(65);
                                    lineDataSet.setFillColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setHighLightColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextSize(9f);
                                    lineDataSet.setDrawValues(false);
                                }
                                else if (feed_key.contains("light"))
                                {
                                    lineDataSet = new LineDataSet(dataVals, "Light Intensity");

                                    lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                    lineDataSet.setColor(Color.rgb(238, 216, 88));
                                    //set.setCircleColor(Color.WHITE);
                                    lineDataSet.setLineWidth(2f);
                                    //set.setCircleRadius(4f);
                                    lineDataSet.setFillAlpha(65);
                                    lineDataSet.setFillColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setHighLightColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextSize(9f);
                                    lineDataSet.setDrawValues(false);
                                }
                                else if(feed_key.contains("mois"))
                                {
                                    lineDataSet = new LineDataSet(dataVals, "Soil Moisture");
                                    lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
                                    lineDataSet.setColor(Color.rgb(202, 69, 27));
                                    //set.setCircleColor(Color.WHITE);
                                    lineDataSet.setLineWidth(2f);
                                    //set.setCircleRadius(4f);
                                    lineDataSet.setFillAlpha(65);
                                    lineDataSet.setFillColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setHighLightColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextColor(Color.rgb(255, 0, 0));
                                    lineDataSet.setValueTextSize(9f);
                                    lineDataSet.setDrawValues(false);
                                }


                                ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                                dataSets.add(lineDataSet);
                                LineData data = new LineData(dataSets);
                                chart.setData(data);
                                chart.invalidate();

                            } else {
                                Toast.makeText(Graph.this, "No data", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
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