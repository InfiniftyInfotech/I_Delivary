package com.example.i_delivery.viewmodel;

import com.example.i_delivery.data.DataRepo;
import com.example.i_delivery.model.AllOrderResponse;
import com.example.i_delivery.model.Order;
import com.example.i_delivery.model.User;

import androidx.lifecycle.LiveData;

public class DataViewModel {

    private final DataRepo repo;

    public DataViewModel() {
        repo = DataRepo.getInstance();
    }

    public LiveData<AllOrderResponse> getAllOrderByLogin(User user) {
        return repo.getAllOrderByLogin(user);
    }

    public LiveData<AllOrderResponse> sendOrderToDelivery(Order order) {
        return repo.sendOrderToDelivery(order);
    }

    public LiveData<AllOrderResponse> requestOTP(User user) {
        return repo.requestOTP(user);
    }
}
