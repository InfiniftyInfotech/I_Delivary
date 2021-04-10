package com.example.i_delivery.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.example.i_delivery.utils.local_db.PrefClient;
import com.example.i_delivery.view.LoginActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

public class Utils {
    private static NoInternetAlertDialog dialog = new NoInternetAlertDialog();

    public static void showNoInternetDialog(Context context){
        FragmentTransaction ft = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
        dialog.show(ft, "NoInternetAlertDialog");
    }

    public static void hideNoInternetDialog(Context context){
        if (dialog != null
                && dialog.getDialog() != null
                && dialog.getDialog().isShowing()
                && !dialog.isRemoving()) {

            new Handler(Looper.myLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                }
            }, 1000);

        }
    }


    public static void showAlert(Context context, String msg){
        new MaterialAlertDialogBuilder(context)
                .setTitle("Its important!")
                .setMessage(msg)
                .setPositiveButton("ok", null)
                .setCancelable(false)
                .show();
    }

    public static void logout(Context context){
        new PrefClient(context).removeUser();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
