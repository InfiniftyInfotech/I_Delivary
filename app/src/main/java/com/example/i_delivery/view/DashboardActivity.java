package com.example.i_delivery.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.Observer;
import io.realm.Realm;
import io.realm.RealmConfiguration;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.i_delivery.R;
import com.example.i_delivery.data.DataViewModel;
import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.User;
import com.example.i_delivery.utils.local_db.PrefClient;
import com.example.i_delivery.utils.Utils;

import org.sumon.eagleeye.EagleEyeObserver;
import org.sumon.eagleeye.OnChangeConnectivityListener;

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    private CardView btnPending, btnSuccess, btnHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name("myrealm.realm")
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .allowQueriesOnUiThread(true)
                .build();
        Realm.setDefaultConfiguration(config);

        EagleEyeObserver.setConnectivityListener(new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {

                Log.d(TAG, "onChanged: " + status);
                if (status) {
                    Utils.hideNoInternetDialog(DashboardActivity.this);
                } else {
                    Utils.showNoInternetDialog(DashboardActivity.this);
                }
            }
        });

        View header = findViewById(R.id.header);
        View content = findViewById(R.id.content);

        Animation anim_left_to_right = AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_left_to_right);
        Animation anim_bottom_to_top = AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_bottom_to_top);
        header.setAnimation(anim_left_to_right);
        content.setAnimation(anim_bottom_to_top);

        btnPending = findViewById(R.id.btnPending);
        btnPending.setOnClickListener(this);
        btnSuccess = findViewById(R.id.btnSuccess);
        btnSuccess.setOnClickListener(this);
        btnHistory = findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        ImageView fabProfile = findViewById(R.id.fabProfile);
        Glide.with(this)
                .load("https://infinitynetworkmarketing.com/infinity-backend/public/deliveryman/" + new PrefClient(this).getUser().getAvatar())
                .circleCrop()
                .into(fabProfile);

        fabProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // View v = findViewById(R.id.fabProfile);
                PopupMenu pm = new PopupMenu(DashboardActivity.this, fabProfile);
                pm.getMenuInflater().inflate(R.menu.main_menu, pm.getMenu());
                pm.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @SuppressLint("NonConstantResourceId")
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.action_logout:
                                new PrefClient(DashboardActivity.this).removeUser();
                                Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                DashboardActivity.this.finish();
                                break;
                            default:
                                break;
                        }
                        return true;
                    }
                });
                pm.show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        _getDelivery();
    }

    private void _getDelivery() {

        User user = new PrefClient(DashboardActivity.this).getUser();
        DataViewModel viewModel = new DataViewModel();
        viewModel.getData(user).observe(DashboardActivity.this, new Observer<DataModel>() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onChanged(DataModel dataModel) {

                if (dataModel == null) {
                    Toast.makeText(DashboardActivity.this, "Please try again", Toast.LENGTH_LONG).show();
                    //Utils.logout(DashboardActivity.this);
                   // DashboardActivity.this.finish();
                    return;
                }

                if (dataModel.getResponse() == 200) {
                    if (dataModel.getOrder_list() != null) {
                        TextView textView = findViewById(R.id.badgePending);
                        textView.setText(dataModel.getOrder_list().size() + "");
                    }
                    if (dataModel.getOrder_list() != null) {
                        TextView textView = findViewById(R.id.badgeSuccess);
                        textView.setText(dataModel.getSuccess_list().size() + "");
                    }
                    if (dataModel.getOrder_list() != null) {
                        TextView textView = findViewById(R.id.badgeAll);
                        textView.setText((dataModel.getSuccess_list().size() + dataModel.getOrder_list().size()) + "");
                    }


                } else {
                    Toast.makeText(DashboardActivity.this, dataModel.getMessage(), Toast.LENGTH_LONG).show();
                    //Utils.logout(DashboardActivity.this);
                    DashboardActivity.this.finish();
                }
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPending:
                startActivity(new Intent(DashboardActivity.this, DeliveryActivity.class)
                        .putExtra("type", "onprocess"));
                break;
            case R.id.btnSuccess:
                startActivity(new Intent(DashboardActivity.this, DeliveryActivity.class)
                        .putExtra("type", "success"));
                break;
            case R.id.btnHistory:
                startActivity(new Intent(DashboardActivity.this, DeliveryActivity.class)
                        .putExtra("type", "history"));
                break;
        }
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}