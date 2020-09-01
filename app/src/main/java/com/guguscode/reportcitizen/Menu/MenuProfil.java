package com.guguscode.reportcitizen.Menu;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guguscode.reportcitizen.Controller.SessionManager;
import com.guguscode.reportcitizen.R;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MenuProfil extends AppCompatActivity {

    private TextView mNama, mEmail;
    private Button logout_btn;
    private ProgressBar progressBar;
    SessionManager sessionManager;

    Handler handler;
    Runnable runnable;
    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_profil);
        //title action bar
        getSupportActionBar().setTitle("Profil Pengguna");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        mNama = findViewById(R.id.prof_nama);
        mEmail = findViewById(R.id.prof_email);
        logout_btn = findViewById(R.id.logout_btn);
        progressBar = findViewById(R.id.loading);

        HashMap<String, String> user = sessionManager.getUserDetail();
        String nNama = user.get(sessionManager.NAMA);
        String nEmail = user.get(sessionManager.EMAIL);

        mNama.setText(nNama);
        mEmail.setText(nEmail);

        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                logout_btn.setVisibility(View.GONE);
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
                }, 3000);
                sessionManager.logout();
            }
        });
    }


}
