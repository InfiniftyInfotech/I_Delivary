package com.example.i_delivery.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.i_delivery.R;
import com.example.i_delivery.viewmodel.DataViewModel;
import com.example.i_delivery.model.AllOrderResponse;
import com.example.i_delivery.model.User;
import com.example.i_delivery.utils.PrefClient;
import com.example.i_delivery.utils.Utils;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import org.sumon.eagleeye.EagleEyeObserver;
import org.sumon.eagleeye.OnChangeConnectivityListener;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private Button btnLogin;
    private TextInputEditText etName,etPass;
    private SpinKitView spin_kit;
    //
    private String valueName, valuePass;
    private User user;

    interface OnLoginListener{
        void onLoginSuccess(AllOrderResponse allOrderResponse);
        void onLoginFailed(String errorMessage);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EagleEyeObserver.setConnectivityListener(new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                if (status) {
                    Utils.hideNoInternetDialog(LoginActivity.this);
                } else {
                    Utils.showNoInternetDialog(LoginActivity.this);
                }
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        etName = findViewById(R.id.inputName);
        etPass = findViewById(R.id.inputPass);
        spin_kit = findViewById(R.id.spin_kit);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if (_isValid()){
                   btnLogin.setEnabled(false);
                   btnLogin.setBackgroundColor(getResources().getColor(R.color.color_inactive));
                   spin_kit.setVisibility(View.VISIBLE);
                   _getAllOrderByLogin(user, new OnLoginListener() {
                       @Override
                       public void onLoginSuccess(AllOrderResponse allOrderResponse) {

                           String strUser = new Gson().toJson(allOrderResponse.getUser_details().get(0));
                           new PrefClient(LoginActivity.this).saveUser(strUser);
                           Log.d(TAG, "onLoginSuccess getToken: " + new PrefClient(LoginActivity.this).getToken());

                           startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                           finish();
                       }

                       @Override
                       public void onLoginFailed(String errorMessage) {
                           Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                           btnLogin.setEnabled(true);
                           btnLogin.setBackgroundColor(getResources().getColor(R.color.color_infinity_theme));
                           spin_kit.setVisibility(View.GONE);
                       }
                   });
               }
            }
        });
    }

    private void _getAllOrderByLogin(User user, OnLoginListener listener) {
        DataViewModel viewModel = new DataViewModel();
        viewModel.getAllOrderByLogin(user).observe(LoginActivity.this, new Observer<AllOrderResponse>() {
            @Override
            public void onChanged(AllOrderResponse allOrderResponse) {
                if (allOrderResponse != null){
                    if (allOrderResponse.getResponse() == 200){
                        listener.onLoginSuccess(allOrderResponse);
                    } else {
                        listener.onLoginFailed(allOrderResponse.getMessage());
                    }
                } else {
                    listener.onLoginFailed("An error occurred! try again.");
                }
            }
        });
    }

    private boolean _isValid() {

        valueName = etName.getText().toString();
        valuePass = etPass.getText().toString();

        // valueName = "Ibrahim";
        // valuePass = "I123456m";

        if (TextUtils.isEmpty(valueName)) {
            etName.setError("Can't be empty");
            return false;
        }

        if (TextUtils.isEmpty(valuePass)) {
            etPass.setError("Can't be empty");
            return false;
        }

        user = new User();
        user.setUsername(valueName);
        user.setPassword(valuePass);

        return true;
    }

}