package com.guguscode.reportcitizen.View;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guguscode.reportcitizen.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RegisterUser extends AppCompatActivity {

    private TextInputLayout nNama, nEmail, nPassword;
    private ProgressBar progressBar;
    private Button register_btn;
    private static String URL_REGIST = "http://aplikasi-tesis.info/tesis/pgsql_register.php";

    Handler handler;
    Runnable runnable;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);
        //title action bar
        getSupportActionBar().setTitle("Halaman Daftar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); //hidden keyboard

        progressBar = findViewById(R.id.loading);

        nNama = findViewById(R.id.regUserName);
        nEmail = findViewById(R.id.regUserEmail);
        nPassword = findViewById(R.id.regUserPass);
        register_btn = findViewById(R.id.register_btn);

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Register();
            }
        });
    }

    private void Register(){

        final String nama = nNama.getEditText().getText().toString().trim();
        final String email = nEmail.getEditText().getText().toString().trim();
        final String password = nPassword.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(nama)){
            nNama.setError("Masukkan nama lengkap");
            nNama.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)){
            nEmail.setError("Silahkan masukkan email");
            nEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            nEmail.setError("Email tidak valid");
            nEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)){
            nPassword.setError("Password tidak boleh kosong");
            nPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6){
            nPassword.setError("Password tidak boleh kurang dari 6 karakter");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                timer.cancel();
            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 2000);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_REGIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");

                            if (success.equals("1")){
                                Toast.makeText(RegisterUser.this,"Daftar berhasil" , Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), LoginEmail.class));
                            }else {
                                if (success.equals("0")){
                                    Toast.makeText(RegisterUser.this,"Daftar gagal" , Toast.LENGTH_SHORT).show();
                                    nEmail.setError("Email sudah terdaftar");
                                    nEmail.requestFocus();
                                    return;
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterUser.this,"Daftar gagal" + error.toString() , Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //tulisan di dalam tanda petik disesuaikan dengan nama kolom basisdata
                params.put("user_nama", nama);
                params.put("user_email", email);
                params.put("user_password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}
