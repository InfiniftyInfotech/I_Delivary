package com.example.i_delivery.model;

import java.util.ArrayList;
import java.util.List;

public class AllOrderResponse {
    private int response;
    private String message;
    private String token;
    private List<User> user_details = new ArrayList<>();
    private List<Order> order_list = new ArrayList<>();
    private List<Order> success_list = new ArrayList<>();
    private List<Order> partdelivery_list = new ArrayList<>();
    private List<Order> full_list = new ArrayList<>();

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<Order> getFull_list() {
        full_list.addAll(order_list);
        full_list.addAll(partdelivery_list);
        full_list.addAll(success_list);
        return full_list;
    }

    public void setFull_list(List<Order> full_list) {
        this.full_list = full_list;
    }

    public int getResponse() {
        return response;
    }

    public void setResponse(int response) {
        this.response = response;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<User> getUser_details() {
        return user_details;
    }

    public void setUser_details(List<User> user_details) {
        this.user_details = user_details;
    }

    public List<Order> getOrder_list() {
        return order_list;
    }

    public void setOrder_list(List<Order> order_list) {
        this.order_list = order_list;
    }

    public List<Order> getSuccess_list() {
        return success_list;
    }

    public void setSuccess_list(List<Order> success_list) {
        this.success_list = success_list;
    }

    public List<Order> getPartdelivery_list() {
        return partdelivery_list;
    }

    public void setPartdelivery_list(List<Order> partdelivery_list) {
        this.partdelivery_list = partdelivery_list;
    }


}
