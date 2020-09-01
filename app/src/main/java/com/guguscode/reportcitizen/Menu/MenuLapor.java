package com.guguscode.reportcitizen.Menu;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.guguscode.reportcitizen.Controller.AppController;
import com.guguscode.reportcitizen.Controller.SessionManager;
import com.guguscode.reportcitizen.R;
import com.tooltip.Tooltip;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MenuLapor extends AppCompatActivity {

    String currentImagePath = null;

    Handler handler;
    Runnable runnable;
    Timer timer;
    Bitmap decoded;
    SessionManager sessionManager;

    Spinner classSpinner, divSpinner;
    String selectedClass, selectedDiv;

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
    private Button fetch, kirimLaporan; //tombol dapatkan lokasi
    private ImageView Foto;
    private EditText etLat, etLng, etNamaPelapor, etKeterangan, etTempat;
    private ProgressBar progressBar;

    int success;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";
    private String KEY_IMAGE = "gambar";
    private Bitmap bitmap;
    private static final String TAG = MenuLapor.class.getSimpleName();
    String tag_json_obj = "json_obj_req";

    private static String URL_KIRIM = "http://aplikasi-tesis.info/tesis/pgsql_lapor.php";
    private FusedLocationProviderClient fusedLocationClient;
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_lapor);
        //title action bar
        getSupportActionBar().setTitle("Halaman Lapor");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //Add back button
        getWindow().setSoftInputMode(WindowManager.
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        fetch = findViewById(R.id.fetch_location); //tombol dapatkan lokasi

        etNamaPelapor = findViewById(R.id.idPelapor);
        HashMap<String, String> user = sessionManager.getUserDetail();
        String NamaPelapor = user.get(sessionManager.NAMA);
        etNamaPelapor.setText(NamaPelapor);

        classSpinner = findViewById(R.id.classSpinner);
        divSpinner = findViewById(R.id.divSpinner);

        etTempat = findViewById(R.id.namaTempat);
        etLat = findViewById(R.id.lat_location);
        etLng = findViewById(R.id.lng_location);
        etKeterangan = findViewById(R.id.idKomentar);
        kirimLaporan = findViewById(R.id.KirimLaporan); //tombol kirim laporan
        Foto = findViewById(R.id.path_photo);
        progressBar = findViewById(R.id.loading);

        final Tooltip tooltip = new Tooltip.Builder(Foto)
                .setText("Klik ikon untuk ambil gambar")
                .setTextColor(Color.BLACK)
                .setCancelable(false)
                .show();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tooltip.dismiss();
                CapturePicture();
            }
        });

        kirimLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendLaporan();
            }
        });

        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                selectedClass = parent.getItemAtPosition(position).toString();

                switch (selectedClass)
                {
                    case "Pilih Kategori":
                        divSpinner.setVisibility(View.GONE);
                        break;

                    case "Fasilitas Kesehatan":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_fakes)));
                        break;

                    case "Fasilitas Peribadatan":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_fas_ibadah)));
                        break;

                    case "Fasilitas Sosial":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_fasos)));
                        break;

                    case "Gedung Pemerintahan":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_gedung_pemerintahan)));
                        break;

                    case "Infrastruktur Transportasi":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_infra_transportasi)));
                        break;

                    case "Infrastruktur Jalan":
                        // assigning div item list defined in XML to the div Spinner
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_jalan)));
                        break;

                    case "Infrastruktur Pendidikan":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_pendidikan)));
                        break;

                    case "Fasilitas Umum":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_fasum)));
                        break;

                    case "Lain-lain":
                        divSpinner.setVisibility(View.VISIBLE);
                        divSpinner.setAdapter(new ArrayAdapter<String>(MenuLapor.this,
                                android.R.layout.simple_spinner_dropdown_item,
                                getResources().getStringArray(R.array.items_lain)));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        divSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) parent.getChildAt(0)).setTextSize(20);
                selectedDiv = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchLocation();
            }
        });
    }

    //tombol dapatkan lokasi
    private void fetchLocation() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(MenuLapor.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MenuLapor.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {

                new AlertDialog.Builder(this)
                        .setTitle("Diperlukan izin lokasi")
                        .setMessage("Apakah Anda akan memberikan izin untuk mengakses fitur ini ?")
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MenuLapor.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create()
                        .show();

            } else {
                ActivityCompat.requestPermissions(MenuLapor.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);

            }
        } else {
            //permissions has already been granted
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                Double lat = location.getLatitude();
                                Double lng = location.getLongitude();

                                etLat.setText("" + lat);
                                etLng.setText("" + lng);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {

            }
        }

        if (requestCode == 0) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                CapturePicture();
            }
        }

    }

    //tombol kamera
    private void CapturePicture(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                File imageFile1 = null;
                try {
                    imageFile1 = getImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (imageFile1!= null){
                    Uri imageUri = FileProvider.getUriForFile(this,"com.guguscode.reportcitizen.fileprovider",imageFile1);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        }
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentImagePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                bitmap = BitmapFactory.decodeFile(currentImagePath);
                //Foto.setImageBitmap(bitmap);
                rotateImage(getResizedBitmap(bitmap, 512));
            }
        }
    }


    private void kosong() {
        etTempat.setText(null);
        Foto.setImageResource(0);
        etLat.setText(null);
        etLng.setText(null);
        etKeterangan.setText(null);
    }

    public String getStringImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void setToImageView(Bitmap bitmap) {
        //compress image
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(bytes.toByteArray()));

        //menampilkan gambar yang dipilih dari camera/gallery ke ImageView
        Foto.setImageBitmap(decoded);
    }

    // fungsi resize image
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private void rotateImage (Bitmap bitmap){
        ExifInterface exifInterface = null;
        try{
            exifInterface = new ExifInterface(currentImagePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            default:
        }
        Bitmap rotateBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        setToImageView(rotateBmp);
    }

    private void SendLaporan(){

        final String np = etNamaPelapor.getText().toString().trim();
        final String nt = etTempat.getText().toString().trim();
        final String lat = etLat.getText().toString().trim();
        final String lng = etLng.getText().toString().trim();
        final String ket = etKeterangan.getText().toString().trim();
        final String firstspin=classSpinner.getSelectedItem().toString();
        final String secondspin=divSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(nt)){
            etTempat.setError("Kolom tidak boleh kosong");
            etTempat.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lat)){
            etLat.setError("Kolom tidak boleh kosong");
            etLat.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lng)){
            etLng.setError("Kolom tidak boleh kosong");
            etLng.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(ket)){
            etKeterangan.setError("Kolom tidak boleh kosong");
            etKeterangan.requestFocus();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_KIRIM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                Log.e(TAG, "Response: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);
                    if (success == 1) {
                        Log.e("v Add", jObj.toString());
                        Toast.makeText(MenuLapor.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        kosong();
                        finish();
                    } else {
                        Toast.makeText(MenuLapor.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MenuLapor.this, error.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, error.getMessage().toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                //menambah parameter yang di kirim ke web servis
                params.put("nama_pelapor", np);
                params.put("nama_tempat", nt);
                params.put("kategori", firstspin);
                params.put("sub_kategori", secondspin);
                params.put("lat", lat);
                params.put("lng", lng);
                params.put("keterangan", ket);
                params.put(KEY_IMAGE, getStringImage(decoded));
                //params.put(KEY_NAME, txt_name.getText().toString().trim());

                //kembali ke parameters
                Log.e(TAG, "" + params);
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag_json_obj);
    }

}
