package com.example.i_delivery.data;

import android.util.Log;
import android.widget.Toast;

import com.example.i_delivery.data.api.RetrofitClient;
import com.example.i_delivery.data.api.RetrofitInterface;
import com.example.i_delivery.model.AllOrderResponse;
import com.example.i_delivery.model.Order;
import com.example.i_delivery.model.User;
import com.example.i_delivery.utils.PrefClient;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.i_delivery.utils.Utils.showAlert;

public class DataRepo {
    private static final String TAG = "DataRepo";
    public static DataRepo instance;

    public static DataRepo getInstance() {
        if (instance == null) {
            instance = new DataRepo();
        }
        return instance;
    }

    public MutableLiveData<AllOrderResponse> getAllOrderByLogin(User user) {
        MutableLiveData<AllOrderResponse> data = new MutableLiveData<>();

        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .getAllOrderByLogin(user).enqueue(new Callback<AllOrderResponse>() {
            @Override
            public void onResponse(Call<AllOrderResponse> call, Response<AllOrderResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<AllOrderResponse> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<AllOrderResponse> sendOrderToDelivery(Order order) {
        MutableLiveData<AllOrderResponse> data = new MutableLiveData<>();

        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .sendOrderToDelivery(order).enqueue(new Callback<AllOrderResponse>() {
            @Override
            public void onResponse(Call<AllOrderResponse> call, Response<AllOrderResponse> response) {
                data.setValue(response.body());
            }

            @Override
            public void onFailure(Call<AllOrderResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                data.setValue(null);
            }
        });
        return data;
    }

    public MutableLiveData<AllOrderResponse> requestOTP(User user) {
        MutableLiveData<AllOrderResponse> data = new MutableLiveData<>();

        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .requestOTP(user)
                .enqueue(new Callback<AllOrderResponse>() {
                    @Override
                    public void onResponse(Call<AllOrderResponse> call, Response<AllOrderResponse> response) {
                        data.setValue(response.body());
                    }

                    @Override
                    public void onFailure(Call<AllOrderResponse> call, Throwable t) {
                        Log.d(TAG, "onFailure: " + t.getMessage());
                        data.setValue(null);
                    }
                });
        return data;
    }
}
