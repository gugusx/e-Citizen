package com.guguscode.reportcitizen.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.guguscode.reportcitizen.Dashboard.LoginPage;
import com.guguscode.reportcitizen.Menu.MenuProfil;
import com.guguscode.reportcitizen.R;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public Context mCtx;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME ="LOGIN";
    private static final String LOGIN = "IS_LOGIN";
    public static final String NAMA = "NAMA";
    public static final String EMAIL = "EMAIL";

    public static final String PASS = "PASSWORD";

    public SessionManager(Context mCtx) {
        this.mCtx = mCtx;
        sharedPreferences = mCtx.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createSession (String nama, String email){
        editor.putBoolean(LOGIN, true);
        editor.putString(NAMA, nama);
        editor.putString(EMAIL, email);
        editor.commit();
    }

    public void writeLoginStatus (boolean status){
        editor = sharedPreferences.edit();
        editor.putBoolean(LOGIN, status);
        editor.commit();
    }

    public boolean isLoggin(){
        boolean status = false;
        status = sharedPreferences.getBoolean(LOGIN, false);
        return status;
    }

    public void checkLogin(){
        if (!this.isLoggin()){
            Intent i = new Intent(mCtx, LoginPage.class);
            mCtx.startActivity(i);
        }
    }

    public HashMap <String, String> getUserDetail(){
        HashMap<String,String> user = new HashMap<>();
        user.put(NAMA, sharedPreferences.getString(NAMA, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));

        return user;
    }

    public void logout(){
        editor.clear();
        editor.commit();
        Intent i = new Intent(mCtx, LoginPage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        mCtx.startActivity(i);
    }
}
