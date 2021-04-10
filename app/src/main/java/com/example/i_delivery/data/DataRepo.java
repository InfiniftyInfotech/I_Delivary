package com.example.i_delivery.data;

import com.example.i_delivery.data.api.RetrofitClient;
import com.example.i_delivery.data.api.RetrofitInterface;
import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.User;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataRepo {
    private static final String TAG = "DataRepo";
    public static DataRepo instance;

    public static DataRepo getInstance() {
        if (instance == null) {
            instance = new DataRepo();
        }
        return instance;
    }

    public MutableLiveData<DataModel> getData(User user) {
        MutableLiveData<DataModel> data = new MutableLiveData<>();

        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .getData(user).enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {

                data.setValue(response.body());

            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
                data.setValue(null);
            }
        });
        return data;
    }
}
