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
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.guguscode.reportcitizen.Controller.SessionManager;
import com.guguscode.reportcitizen.Dashboard.TampilanMenu;
import com.guguscode.reportcitizen.Menu.MenuProfil;
import com.guguscode.reportcitizen.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class LoginEmail extends AppCompatActivity {

    private TextInputLayout nEmail, nPassword;

    private ProgressBar progressBar;
    private Button login_btn;
    private static String URL_LOGIN = "http://aplikasi-tesis.info/tesis/pgsql_login.php";

    Handler handler;
    Runnable runnable;
    Timer timer;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_email);
        getSupportActionBar().setTitle("Halaman Masuk"); //title action bar
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sessionManager = new SessionManager(this);

        progressBar = findViewById(R.id.loading);

        nEmail = findViewById(R.id.user_email);
        nPassword = findViewById(R.id.user_pass);
        login_btn = findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login();
            }
        });
    }

    private void Login() {

        final String email = nEmail.getEditText().getText().toString().trim();
        final String password = nPassword.getEditText().getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            nEmail.setError("Harap periksa kembali alamat email");
            nEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            nPassword.setError("Email tidak valid");
            nPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            nPassword.setError("Password tidak boleh kosong");
            nPassword.requestFocus();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String success = jsonObject.getString("success");
                            JSONArray jsonArray = jsonObject.getJSONArray("login");

                            if (success.equals("1")){
                                for (int i = 0; i <  jsonArray.length(); i++){
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    String nama = object.getString("user_nama").trim();
                                    String email = object.getString("user_email").trim();

                                    sessionManager.createSession(nama, email);

                                    Toast.makeText(LoginEmail.this,"Login berhasil", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(LoginEmail.this, MenuProfil.class);
                                    intent.putExtra("user_nama",nama);
                                    intent.putExtra("user_email", email);
                                    startActivity(new Intent(getApplicationContext(), TampilanMenu.class));
                                }
                            } else {
                                Toast.makeText(LoginEmail.this,"Login gagal", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginEmail.this,"Login gagal" + e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginEmail.this,"Login gagal" + error.toString(), Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_email", email);
                params.put("user_password", password);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
