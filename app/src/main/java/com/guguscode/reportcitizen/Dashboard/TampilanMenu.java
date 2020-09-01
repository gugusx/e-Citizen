package com.guguscode.reportcitizen.Dashboard;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.guguscode.reportcitizen.Menu.MenuBeranda;
import com.guguscode.reportcitizen.Menu.MenuLapor;
import com.guguscode.reportcitizen.Menu.MenuPeta;
import com.guguscode.reportcitizen.Menu.MenuProfil;
import com.guguscode.reportcitizen.R;

public class TampilanMenu extends AppCompatActivity implements View.OnClickListener{

    private CardView beranda, lapor, peta, profil;
    private TextView tv1, tv2, tv3, tv4, tvWel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampilan_menu);
        getSupportActionBar().setTitle("Dashboard Menu"); //title action bar
        //var CardView
        beranda = findViewById(R.id.menuberanda);
        lapor = findViewById(R.id.menulapor);
        peta = findViewById(R.id.menupeta);
        profil = findViewById(R.id.menuprofil);

        //var TextView //change font style
        Typeface myfont = Typeface.createFromAsset(getAssets(),"fonts/odb.ttf");
        tv1 = findViewById(R.id.tvBeranda); tv1.setTypeface(myfont);
        tv2 = findViewById(R.id.tvLapor); tv2.setTypeface(myfont);
        tv3 = findViewById(R.id.tvPeta); tv3.setTypeface(myfont);
        tv4 = findViewById(R.id.tvProfil); tv4.setTypeface(myfont);


        beranda.setOnClickListener(this);
        lapor.setOnClickListener(this);
        peta.setOnClickListener(this);
        profil.setOnClickListener(this);

    }

    //keluar dari aplikasi
    @Override
    public void onBackPressed() {
           finishAffinity();
           super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        Intent i;

        switch (v.getId()) {
            case R.id.menuberanda : i = new Intent(this, MenuBeranda.class); startActivity(i); break;
            case R.id.menulapor : i = new Intent(this, MenuLapor.class); startActivity(i); break;
            case R.id.menupeta: i = new Intent(this, MenuPeta.class); startActivity(i); break;
            case R.id.menuprofil : i = new Intent(this, MenuProfil.class); startActivity(i); break;
            default: break;
        }

    }
}
