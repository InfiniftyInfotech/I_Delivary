package com.example.i_delivery.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.i_delivery.model.User;
import com.google.gson.Gson;

/**
 * Created by SumOn on Dec 09,2020 at 1:06 AM
 */
public class PrefClient {
    private Context context;
    private SharedPreferences pref;

    public PrefClient(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    public void saveUser(String strUser){
        pref.edit().putString("user", strUser).apply();
    }

    public User getUser(){
        return new Gson().fromJson(pref.getString("user", null), User.class);
    }

    public void removeUser(){
        pref.edit().clear().apply();
    }

    public boolean checkLogin(){
       String strUser = pref.getString("user", null);
       return strUser != null;
    }

    public void saveToken(String token) {
        pref.edit().putString("token", token).apply();
    }

    public String getToken() {
        return pref.getString("token", null);
    }


}
