package com.kevin.garden;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Devices extends AppCompatActivity {



    TextView username, email;

    Switch sw_pump, sw_awning, sw_fan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices);



        initial();

        sw_pump.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(Devices.this, "Water pump is ON", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(Devices.this, "Water pump is OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sw_awning.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(Devices.this, "Awning is OPEN", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(Devices.this, "Awning is CLOSE", Toast.LENGTH_SHORT).show();
                }
            }
        });

        sw_fan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    Toast.makeText(Devices.this, "Fan is ON", Toast.LENGTH_SHORT).show();

                }
                else
                {
                    Toast.makeText(Devices.this, "Fan is OFF", Toast.LENGTH_SHORT).show();
                }
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
    }
}