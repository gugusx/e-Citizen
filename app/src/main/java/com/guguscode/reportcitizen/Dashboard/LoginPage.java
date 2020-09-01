package com.guguscode.reportcitizen.Dashboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.guguscode.reportcitizen.Controller.SessionManager;
import com.guguscode.reportcitizen.R;
import com.guguscode.reportcitizen.View.LoginEmail;
import com.guguscode.reportcitizen.View.RegisterUser;


public class LoginPage extends AppCompatActivity {

    private Button loginemail;
    private TextView txtregister;

    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        //hide action bar
        getSupportActionBar().hide();

        sessionManager = new SessionManager(this);

        loginemail = findViewById(R.id.btn_login);
        Typeface myfont = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Bold.ttf"); //change font style
        loginemail.setTypeface(myfont);
        txtregister = findViewById(R.id.register_txt);

        if (sessionManager.isLoggin()){
            startActivity(new Intent(getApplicationContext(),TampilanMenu.class));
        }

        txtregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, RegisterUser.class));
            }
        });

        loginemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginPage.this, LoginEmail.class));
            }
        });


    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        super.onBackPressed();
    }
}
