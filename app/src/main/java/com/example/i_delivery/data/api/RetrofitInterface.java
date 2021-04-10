package com.example.i_delivery.data.api;

import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.Order;
import com.example.i_delivery.data.model.ProductResponse;
import com.example.i_delivery.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST ("login_delivery_man")
    Call<DataModel> getData(@Body User user);

    @POST ("requestDeliveryOrder")
    Call<DataModel> sendData(@Body Order order);

    @POST ("otp_request")
    Call<DataModel> otp_request(@Body User user);

    @POST ("getOrderedDetails")
    Call<ProductResponse> getProductByOrderID(@Body ProductResponse productResponse);

}

// login_delivery_man
// requestDeliveryOrder
// otp_request
// getOrderedDetails
