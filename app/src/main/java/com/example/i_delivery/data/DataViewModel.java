package com.example.i_delivery.data;

import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.User;

import androidx.lifecycle.LiveData;

public class DataViewModel {

    private final DataRepo repo;

    public DataViewModel() {
        repo = DataRepo.getInstance();
    }

    public LiveData<DataModel> getData(User user) {
        return repo.getData(user);
    }
}
