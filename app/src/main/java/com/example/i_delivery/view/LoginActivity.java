package com.example.i_delivery.view;

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
import com.example.i_delivery.data.DataViewModel;
import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.User;
import com.example.i_delivery.utils.local_db.PrefClient;
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
        void onLoginSuccess(DataModel dataModel);
        void onLoginFailed();
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
                   _getDataByUser(user, new OnLoginListener() {
                       @Override
                       public void onLoginSuccess(DataModel dataModel) {

                           String strUser = new Gson().toJson(dataModel.getUser_details().get(0));
                           new PrefClient(LoginActivity.this).saveUser(strUser);
                           Log.d(TAG, "onLoginSuccess getToken: " + new PrefClient(LoginActivity.this).getToken());

                           startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                           finish();
                       }

                       @Override
                       public void onLoginFailed() {
                          // Toast.makeText(LoginActivity.this, "Login Failed.", Toast.LENGTH_SHORT).show();
                           btnLogin.setEnabled(true);
                           btnLogin.setBackgroundColor(getResources().getColor(R.color.color_infinity_theme));
                           spin_kit.setVisibility(View.GONE);
                       }
                   });
               }
            }
        });
    }

    private void _getDataByUser(User user, OnLoginListener listener) {
        DataViewModel viewModel = new DataViewModel();
        viewModel.getData(user).observe(LoginActivity.this, new Observer<DataModel>() {
            @Override
            public void onChanged(DataModel dataModel) {

                if (dataModel == null) {
                    Toast.makeText(LoginActivity.this, "Id pass not match", Toast.LENGTH_LONG).show();
                    listener.onLoginFailed();
                    return;
                }
                
                if (dataModel.getResponse() == 200){
                    listener.onLoginSuccess(dataModel);
                } else {
                    Toast.makeText(LoginActivity.this, dataModel.getMessage(), Toast.LENGTH_LONG).show();
                    listener.onLoginFailed();
                }
            }
        });
    }

    private boolean _isValid() {

        valueName = etName.getText().toString();
        valuePass = etPass.getText().toString();

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