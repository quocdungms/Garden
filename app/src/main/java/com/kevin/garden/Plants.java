package com.kevin.garden;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class Plants extends AppCompatActivity {


    TextView username, email;
    EditText inputName, inputTemperature, inputLight, inputHumidity, inputMoisture;

    SharedPreferences sharedPreferences;

    Button min, medium, max;

    Button home, devices, graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plants);


        initial();

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboard(v);
            }
        });

        inputTemperature.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboard(v);
            }
        });

        inputLight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboard(v);
            }
        });

        inputHumidity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboard(v);
            }
        });

        inputMoisture.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                hideKeyboard(v);
            }
        });

        min.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String buffer = inputName.getText().toString();
                if(!buffer.equals("")) {
                    editor.putString("plant_name", buffer);
                }


                buffer = inputTemperature.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("min_temp", buffer);
                }

                buffer = inputHumidity.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("min_humid", buffer);
                }

                buffer = inputLight.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("min_light", buffer);
                }

                buffer = inputMoisture.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("min_mois", buffer);
                }

                editor.apply();
                Toast.makeText(Plants.this, "Min values applied", Toast.LENGTH_SHORT).show();
            }
        });

        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String buffer = inputName.getText().toString();
                if(!buffer.equals("")) {
                    editor.putString("plant_name", buffer);
                }


                buffer = inputTemperature.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("medium_temp", buffer);
                }

                buffer = inputHumidity.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("medium_humid", buffer);
                }

                buffer = inputLight.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("medium_light", buffer);
                }

                buffer = inputMoisture.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("medium_mois", buffer);
                }

                editor.apply();
                Toast.makeText(Plants.this, "Min values applied", Toast.LENGTH_SHORT).show();
            }
        });

        max.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                String buffer = inputName.getText().toString();
                if(!buffer.equals("")) {
                    editor.putString("plant_name", buffer);
                }


                buffer = inputTemperature.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("max_temp", buffer);
                }

                buffer = inputHumidity.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("max_humid", buffer);
                }

                buffer = inputLight.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("max_light", buffer);
                }

                buffer = inputMoisture.getText().toString();
                if(!buffer.equals("")){
                    editor.putString("max_mois", buffer);
                }

                editor.apply();
                Toast.makeText(Plants.this, "Max values applied", Toast.LENGTH_SHORT).show();
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Plants.this, Dashboard.class);
                startActivity(intent);

            }
        });

        devices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Plants.this, Devices.class);
                startActivity(intent);

            }
        });

        graph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Plants.this, Graph.class);
                startActivity(intent);

            }
        });
    }



    public void initial()
    {
        username = findViewById(R.id.user_name);
        email = findViewById(R.id.user_email);

        sharedPreferences = getSharedPreferences("plant", MODE_PRIVATE);

        username.setText(getSharedPreferences("data_login", MODE_PRIVATE).getString("username", ""));
        email.setText(getSharedPreferences("data_login", MODE_PRIVATE).getString("email", ""));

        inputName = findViewById(R.id.plant_name);
        inputTemperature = findViewById(R.id.max_temperature);
        inputLight = findViewById(R.id.max_light);
        inputHumidity = findViewById(R.id.max_humidity);
        inputMoisture = findViewById(R.id.max_moisture);

        min = findViewById(R.id.min);
        medium = findViewById(R.id.medium);
        max  = findViewById(R.id.max);

        home = findViewById(R.id.home);
        devices = findViewById(R.id.devices);
        graph = findViewById(R.id.graph);
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}