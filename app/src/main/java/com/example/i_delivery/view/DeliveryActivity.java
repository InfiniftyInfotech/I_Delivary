package com.example.i_delivery.view;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;
import io.realm.Realm;
import io.realm.Sort;

import com.example.i_delivery.R;
import com.example.i_delivery.data.DataViewModel;
import com.example.i_delivery.view.adapter.AdapterOrder;
import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.Order;
import com.example.i_delivery.data.model.User;
import com.example.i_delivery.utils.local_db.PrefClient;
import com.example.i_delivery.utils.Utils;
import com.example.i_delivery.utils.custom_interface.CustomOnItemClickListener;
import com.example.i_delivery.utils.local_db.RealmClient;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;

import org.sumon.eagleeye.EagleEyeObserver;
import org.sumon.eagleeye.OnChangeConnectivityListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DeliveryActivity extends AppCompatActivity {

    private static final String TAG = "DeliveryActivity";
    private String type;
    private AdapterOrder adapterOrder;
    private TextView emptyText, tvCount;
    private SpinKitView spin_kit;
    private TextInputEditText search;
    private List<Order> orders = new ArrayList<>();
    //
    private Realm realm = Realm.getDefaultInstance();
    private Date valueStartDate, valueEndDate;
    //
    private boolean isApplyDateFilter = false, isApplyPaymentTypeFilter = false;
    private String valuePaymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        type = getIntent().getStringExtra("type");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(type.toUpperCase());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EagleEyeObserver.setConnectivityListener(new OnChangeConnectivityListener() {
            @Override
            public void onChanged(boolean status) {
                Log.d(TAG, "onChanged: " + status);
                if (status) {
                    Utils.hideNoInternetDialog(DeliveryActivity.this);
                } else {
                    Utils.showNoInternetDialog(DeliveryActivity.this);
                }
            }
        });

        spin_kit = findViewById(R.id.spin_kit);
        emptyText = findViewById(R.id.emptyText);
        tvCount = findViewById(R.id.count);
        emptyText.setVisibility(View.GONE);
        RecyclerView recyclerView = findViewById(R.id.rvDelivery);

        adapterOrder = new AdapterOrder(this);
        recyclerView.setAdapter(adapterOrder);
        adapterOrder.setListener(new CustomOnItemClickListener<Order>() {
            @Override
            public void OnItemClick(Order result, int position) {
                search.setText("");
                search.clearFocus();
                _getDelivery();
            }
        });

        _getDelivery();
        _handleSearch();

    }

    private void _handleSearch() {

        search = findViewById(R.id.search);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //adapterOrder.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String charString = s.toString();
                List<Order> filteredList = new ArrayList<>();
                if (charString.isEmpty()) {
                    filteredList = orders;
                } else {
                    for (Order row : orders) {
                        if ((row.getOrderid() != null && row.getOrderid().toLowerCase().contains(charString.toLowerCase().trim()))
                                || (row.getMobile() != null && row.getMobile().toLowerCase().contains(charString.toLowerCase().trim()))
                                || (row.getPayment_type() != null && row.getPayment_type().toLowerCase().contains(charString.toLowerCase().trim()))
                                || (row.getAddress() != null && row.getAddress().toLowerCase().contains(charString.toLowerCase().trim()))) {
                            filteredList.add(row);
                        }
                    }
                }
                adapterOrder.setList(filteredList);
                tvCount.setText("Count - " + filteredList.size() + "\n" + "Price = " + _calculateTotalOrderPrice(filteredList) + " tk");

                if (filteredList.size() == 0) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }
            }
        });
    }

    public String _calculateTotalOrderPrice(List<Order> filteredList) {
        double totalPrice = 0;
        for (Order order :
                filteredList) {
            totalPrice += order.getTotalamount();
        }
        return String.valueOf(totalPrice);
    }

    private void _getDelivery() {
        spin_kit.setVisibility(View.VISIBLE);
        User user = new PrefClient(DeliveryActivity.this).getUser();
        DataViewModel viewModel = new DataViewModel();
        viewModel.getData(user).observe(DeliveryActivity.this, new Observer<DataModel>() {
            @Override
            public void onChanged(DataModel dataModel) {

                if (dataModel == null) {
                    Toast.makeText(DeliveryActivity.this, "500 database error", Toast.LENGTH_LONG).show();
                    return;
                }

                new PrefClient(DeliveryActivity.this).saveToken(dataModel.getToken());

                if (dataModel.getResponse() == 200) {
                    new RealmClient(DeliveryActivity.this).deleteRealm();
                    switch (type) {
                        case "onprocess":
                            orders = dataModel.getOrder_list();
                            break;
                        case "success":
                            orders = dataModel.getSuccess_list();
                            break;
                        case "history":
                            orders.addAll(dataModel.getFull_list());
                            break;
                    }

                    // save to local for further use
                    new RealmClient(DeliveryActivity.this).saveOrders(orders);

                } else {
                    Toast.makeText(DeliveryActivity.this, dataModel.getMessage(), Toast.LENGTH_LONG).show();
                }

                if (orders.size() == 0) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    adapterOrder.setList(orders);
                    tvCount.setText("Count - " + orders.size() + "\n" + "Price = " + _calculateTotalOrderPrice(orders) + " tk");
                }

                spin_kit.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_delivery, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_daterange:
                _applyDateRangeFilter();
                return true;
            case R.id.menu_online:
                _applyOrderTypeFilter("onlinepayment");
                return true;
            case R.id.menu_wallet:
                _applyOrderTypeFilter("walletpurchase");
                return true;
            case R.id.menu_cod:
                _applyOrderTypeFilter("cashondelivery");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void _applyOrderTypeFilter(String orderType) {
        isApplyPaymentTypeFilter = true;
        valuePaymentType = orderType;

        _searchOrder();
    }

    private void _applyDateRangeFilter() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        MaterialDatePicker<androidx.core.util.Pair<Long, Long>> pickerRange = MaterialDatePicker.Builder.dateRangePicker().build();
        pickerRange.show(getSupportFragmentManager(), "MaterialDatePicker");

        pickerRange.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long, Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                try {

                    isApplyDateFilter = true;
                    valueStartDate = sdf.parse(sdf.format(selection.first));
                    valueEndDate = sdf.parse(sdf.format(selection.second));

                    _searchOrder();


                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void _searchOrder() {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy");
                TextView searchFilter = findViewById(R.id.dateFilter);
                TextView ordertypeFIlter = findViewById(R.id.ordertypeFIlter);

                List<Order> tempOrder = new ArrayList<>();

                if (isApplyPaymentTypeFilter && isApplyDateFilter) {
                    Log.d(TAG, "execute: date & order type filter apply");
                    tempOrder = realm.copyFromRealm((realm.where(Order.class)
                            .between("entrydate", valueStartDate, valueEndDate)
                            .equalTo("payment_type", valuePaymentType)
                            .sort("entrydate", Sort.DESCENDING)
                            .findAll()));

                    searchFilter.setVisibility(View.VISIBLE);
                    searchFilter.setText(sdf.format(valueStartDate) + " - " + sdf.format(valueEndDate));
                    ordertypeFIlter.setVisibility(View.VISIBLE);
                    ordertypeFIlter.setText(valuePaymentType);

                } else if (isApplyPaymentTypeFilter && !isApplyDateFilter) {
                    Log.d(TAG, "execute: payment_type filter apply");
                    tempOrder = realm.copyFromRealm((realm.where(Order.class)
                            .equalTo("payment_type", valuePaymentType)
                            .sort("entrydate", Sort.DESCENDING)
                            .findAll()));

                    ordertypeFIlter.setVisibility(View.VISIBLE);
                    ordertypeFIlter.setText(valuePaymentType);

                } else if (!isApplyPaymentTypeFilter && isApplyDateFilter) {
                    Log.d(TAG, "execute: entrydate filter apply");
                    tempOrder = realm.copyFromRealm((realm.where(Order.class)
                            .between("entrydate", valueStartDate, valueEndDate)
                            .sort("entrydate", Sort.DESCENDING)
                            .findAll()));

                    searchFilter.setVisibility(View.VISIBLE);
                    searchFilter.setText(sdf.format(valueStartDate) + " - " + sdf.format(valueEndDate));
                }

                adapterOrder.setList(tempOrder);

                if (tempOrder.size() == 0) {
                    emptyText.setVisibility(View.VISIBLE);
                } else {
                    emptyText.setVisibility(View.GONE);
                }

                tvCount.setText("Count - " + tempOrder.size() + "\n" + "Price = " + _calculateTotalOrderPrice(tempOrder) + " tk");

                searchFilter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isApplyDateFilter = false;
                        List<Order> tempOrder1 = new ArrayList<>();
                        if (isApplyPaymentTypeFilter) {
                            tempOrder1 = realm.copyFromRealm((realm.where(Order.class)
                                    .equalTo("payment_type", valuePaymentType)
                                    .sort("entrydate", Sort.DESCENDING)
                                    .findAll()));
                            Log.d(TAG, "isApplyPaymentTypeFilter: true");

                        } else {
                            Log.d(TAG, "isApplyPaymentTypeFilter: false");
                            tempOrder1 = orders;
                        }

                        adapterOrder.setList(tempOrder1);
                        searchFilter.setVisibility(View.GONE);
                        tvCount.setText("Count - " + tempOrder1.size() + "\n" + "Price = " + _calculateTotalOrderPrice(tempOrder1) + " tk");

                        if (tempOrder1.size() == 0) {
                            emptyText.setVisibility(View.VISIBLE);
                        } else {
                            emptyText.setVisibility(View.GONE);
                        }
                    }
                });

                ordertypeFIlter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isApplyPaymentTypeFilter = false;
                        List<Order> tempOrder1 = new ArrayList<>();
                        if (isApplyDateFilter) {
                            tempOrder1 = realm.copyFromRealm((realm.where(Order.class)
                                    .between("entrydate", valueStartDate, valueEndDate)
                                    .sort("entrydate", Sort.DESCENDING)
                                    .findAll()));
                        } else {
                            tempOrder1 = orders;
                        }
                        adapterOrder.setList(tempOrder1);
                        ordertypeFIlter.setVisibility(View.GONE);
                        tvCount.setText("Count - " + tempOrder1.size() + "\n" + "Price = " + _calculateTotalOrderPrice(tempOrder1) + " tk");

                        if (tempOrder1.size() == 0) {
                            emptyText.setVisibility(View.VISIBLE);
                        } else {
                            emptyText.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });
    }
}