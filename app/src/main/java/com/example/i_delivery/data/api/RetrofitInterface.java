package com.example.i_delivery.data.api;

import com.example.i_delivery.model.AllOrderResponse;
import com.example.i_delivery.model.Order;
import com.example.i_delivery.model.SingleOrderResponse;
import com.example.i_delivery.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST ("login_delivery_man")
    Call<AllOrderResponse> getAllOrderByLogin(@Body User user);

    @POST ("requestDeliveryOrder")
    Call<AllOrderResponse> sendOrderToDelivery(@Body Order order);

    @POST ("requestPartDeliveryOrder")
    Call<AllOrderResponse> sendPartOrderToDelivery(@Body Order order);

    @POST ("otp_request")
    Call<AllOrderResponse> requestOTP(@Body User user);

    @POST ("getOrderedDetails")
    Call<SingleOrderResponse> getOrderDetails(@Body SingleOrderResponse singleOrderResponse);

    @POST ("getPrductByPartOrders")
    Call<SingleOrderResponse> getPartOrderDetails(@Body SingleOrderResponse singleOrderResponse);

}