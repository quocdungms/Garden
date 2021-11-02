package com.kevin.garden;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import java.util.concurrent.TimeUnit;

public class Register extends AppCompatActivity {

    EditText inputName, inputEmail, inputPassword, inputConfirmPassword;
    String name, email, password, confirmPassword;
    TextView stt;
    Button btn_register, btn_sw_login;
    Helper helper;

    PhpAPI phpAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        btn_sw_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoLogin();
            }
        });

        inputName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(v);
                }
            }
        });

        inputEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(v);
                }
            }
        });

        inputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(v);
                }
            }
        });

        inputConfirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                {
                    hideKeyboard(v);
                }
            }
        });


    }

    public void init()
    {
        inputName = findViewById(R.id.input_name);
        inputEmail = findViewById(R.id.input_email);
        inputPassword = findViewById(R.id.input_password);
        inputConfirmPassword = findViewById(R.id.input_confirm_password);

        btn_register = findViewById(R.id.register);
        btn_sw_login = findViewById(R.id.sw_login);

        stt = findViewById(R.id.status);
        stt.setText("");
        name = email = password = confirmPassword = "";

        helper = new Helper();
        phpAPI = new PhpAPI();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager;
        inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void register()
    {
        name = inputName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        password = inputPassword.getText().toString().trim();
        confirmPassword = inputConfirmPassword.getText().toString().trim();

        Log.d("status", "password =" + password);
        Log.d("status", "confirm =" + confirmPassword);

        if(!password.equals(confirmPassword))
        {
            stt.setText("*Password does not match!");
            Toast.makeText(this, "Password does not match!", Toast.LENGTH_SHORT).show();
        }
        else if(!name.equals("") && !email.equals("") && !password.equals(""))
        {
            if(!helper.validate(email))
            {
                stt.setText("*Invalid email format!");
                Toast.makeText(Register.this, "Invalid email format!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, phpAPI.REGISTER(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("mqtt", response);
                        if (response.contains("success")) {
                            stt.setText("*Registered Successfully!");
                            Toast.makeText(Register.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();

                            // delay 3s
                            try {
                                TimeUnit.SECONDS.sleep(3);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            gotoLogin();

                        } else if (response.contains("failure")) {
                            Toast.makeText(Register.this, "Somethings went wrong!", Toast.LENGTH_SHORT).show();
                            stt.setText("*Something went wrong!");
                        }
                        else if(response.contains("exists"))
                        {
                            Toast.makeText(Register.this, "This email already exists!", Toast.LENGTH_SHORT).show();
                            stt.setText("**This email already exists!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Register.this, error.toString(), Toast.LENGTH_SHORT).show();
                        stt.setText(error.toString());
                    }
                })
                {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("name", name);
                        data.put("email", email);
                        data.put("password", password);
                        return data;
                    }
                };

                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                requestQueue.add(stringRequest);
            }

        }

        else
        {
            Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
            stt.setText("*All fields are required!");
        }
    }

    public void gotoLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}