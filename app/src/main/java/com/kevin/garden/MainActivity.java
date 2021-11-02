package com.kevin.garden;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.PluralsRes;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    Helper helper;
    EditText inputEmail;
    EditText inputPasword;
    private String email, password;
    Button btn_login;
    TextView stt;
    CheckBox remember;
    SharedPreferences sharedPreferences;
    PhpAPI phpAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();


        if(remember.isChecked())
        {
            login();
        }

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        inputPasword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });


    }


    public void init()
    {
        inputEmail = findViewById(R.id.input_email);
        inputPasword = findViewById(R.id.input_password);
        btn_login = findViewById(R.id.btn_login);
        stt = findViewById(R.id.status);
        stt.setText("");
        email = password = "";
        helper = new Helper();
        remember = findViewById(R.id.remember);

        sharedPreferences = getSharedPreferences("data_login", MODE_PRIVATE);

        inputEmail.setText(sharedPreferences.getString("email", ""));
        inputPasword.setText(sharedPreferences.getString("password", ""));
        remember.setChecked(sharedPreferences.getBoolean("checked", false));

        phpAPI = new PhpAPI();


    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void gotoRegister(View view)
    {
        Intent intent = new Intent(MainActivity.this, Register.class);
        startActivity(intent);
        finish();
    }


    @SuppressLint("SetTextI18n")
    public void login()
    {
        email = inputEmail.getText().toString().trim();
        password = inputPasword.getText().toString().trim();

        if(!email.equals("") && !password.equals(""))
        {
            if(!helper.validate(email))
            {
                Toast.makeText(MainActivity.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
                stt.setText("**Invalid email format!");
            }
            else
            {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, phpAPI.LOGIN(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("mqtt", response);
                        if (response.contains("success")) {

                            if(remember.isChecked())
                            {
                                int idx = response.indexOf(":");
                                String username = response.substring(idx + 1);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("email", email);
                                editor.putString("password", password);
                                editor.putString("username", username);
                                editor.putBoolean("checked", true);
                                editor.apply();
                            }
                            else
                            {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("email");
                                editor.remove("password");
                                editor.remove("checked");
                                editor.apply();
                            }

                            Intent intent = new Intent(MainActivity.this, Dashboard.class);
                            Toast.makeText(MainActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                        if (response.contains("failure")) {
                            Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            stt.setText("**Invalid email or password");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                })
                {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("email", email);
                        data.put("password", password);
                        return data;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }

        }
        else if(!helper.validate(email))
        {
            Toast.makeText(MainActivity.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            stt.setText("**Invalid email format!");
        }
        else
        {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            stt.setText("**All fields are required!");
        }
    }


}