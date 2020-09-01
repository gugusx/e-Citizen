package com.guguscode.reportcitizen.Dashboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.guguscode.reportcitizen.R;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    ImageView imageView;
    TextView tv_kategori, tv_keterangan, tv_wkt, tv_tpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setTitle("Detail Laporan");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.detil_image);
        tv_kategori = findViewById(R.id.detil_judul);
        tv_wkt = findViewById(R.id.detil_waktu);
        tv_keterangan = findViewById(R.id.detil_deskripsi);
        tv_tpt = findViewById(R.id.detil_tempat);

        Picasso.get().load(getIntent().getStringExtra("gambar")).into(imageView);
        tv_kategori.setText(getIntent().getStringExtra("kategori"));
        tv_keterangan.setText(getIntent().getStringExtra("keterangan"));
        tv_wkt.setText(getIntent().getStringExtra("waktu_lapor"));
        tv_tpt.setText(getIntent().getStringExtra("nama_tempat"));
    }
}
