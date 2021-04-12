package com.example.i_delivery.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.i_delivery.R;
import com.example.i_delivery.data.api.RetrofitClient;
import com.example.i_delivery.data.api.RetrofitInterface;
import com.example.i_delivery.model.SingleOrderResponse;
import com.example.i_delivery.ui.adapter.AdapterProduct;
import com.example.i_delivery.viewmodel.DataViewModel;

public class OrderDetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private AdapterProduct adapterProduct;
    private String order_id, part_deliveryid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        //
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //
        order_id = getIntent().getStringExtra("order_id");
        part_deliveryid = getIntent().getStringExtra("part_deliveryid");
        //

        RecyclerView rvProduct = findViewById(R.id.rvProduct);
        adapterProduct = new AdapterProduct(OrderDetailsActivity.this);
        rvProduct.setAdapter(adapterProduct);
        rvProduct.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        if (part_deliveryid != null){
            _getPartOrderDetails();
        } else {
            _getOrderDetails();
        }

    }

    private void _getPartOrderDetails() {
        SingleOrderResponse singleOrderResponse = new SingleOrderResponse();
        singleOrderResponse.setOrder_id(order_id);
        singleOrderResponse.setPart_deliveryid(part_deliveryid);

        DataViewModel dataViewModel = new DataViewModel();
        dataViewModel.getPartOrderDetails(singleOrderResponse)
                .observe(this, new Observer<SingleOrderResponse>() {
                    @Override
                    public void onChanged(SingleOrderResponse singleOrderResponse) {
                        if (singleOrderResponse != null){
                            if (singleOrderResponse.getResponse() == 200){
                                adapterProduct.setProductList(singleOrderResponse.getResult());
                            }else {
                                Toast.makeText(OrderDetailsActivity.this, "An error occurred! try again.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, "An error occurred! try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void _getOrderDetails() {
        SingleOrderResponse singleOrderResponse = new SingleOrderResponse();
        singleOrderResponse.setOrder_id(order_id);

        DataViewModel dataViewModel = new DataViewModel();
        dataViewModel.getOrderDetails(singleOrderResponse)
                .observe(this, new Observer<SingleOrderResponse>() {
                    @Override
                    public void onChanged(SingleOrderResponse singleOrderResponse) {
                        if (singleOrderResponse != null){
                            if (singleOrderResponse.getResponse() == 200){
                                adapterProduct.setProductList(singleOrderResponse.getResult());
                            }else {
                                Toast.makeText(OrderDetailsActivity.this, "An error occurred! try again.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(OrderDetailsActivity.this, "An error occurred! try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}