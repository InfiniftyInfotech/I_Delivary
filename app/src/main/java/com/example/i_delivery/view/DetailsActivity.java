package com.example.i_delivery.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.i_delivery.data.model.ProductResponse;
import com.example.i_delivery.view.adapter.AdapterProduct;

public class DetailsActivity extends AppCompatActivity {

    private static final String TAG = "DetailsActivity";
    private AdapterProduct adapterProduct;
    private String order_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Details");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //
        order_id = getIntent().getStringExtra("order_id");
        //

        RecyclerView rvProduct = findViewById(R.id.rvProduct);
        adapterProduct = new AdapterProduct(DetailsActivity.this);
        rvProduct.setAdapter(adapterProduct);
        rvProduct.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        _getProduct();


    }

    private void _getProduct() {
        ProductResponse response = new ProductResponse();
        response.setOrder_id(order_id);
        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .getProductByOrderID(response)
                .enqueue(new Callback<ProductResponse>() {
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        Log.d(TAG, "onResponse: " + response.body());
                        if (response.body() == null) {
                            Toast.makeText(DetailsActivity.this, "Error in database.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (response.body().getResponse() == 200) {
                            //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                            adapterProduct.setProductList(response.body().getResult());
                        }

                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {

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