package com.example.i_delivery.model;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Order extends RealmObject {
    @PrimaryKey
    private String orderid;
    private String otpcode;
    private String id, userid, part_deliveryid, fullname, mobile, email, address, products, payment_type, order_type, change_order_type, signature, remark;
    private double totalamount, this_deliveryprice;
    private Date entrydate;
    private String token;

    public String getPart_deliveryid() {
        return part_deliveryid;
    }

    public void setPart_deliveryid(String part_deliveryid) {
        this.part_deliveryid = part_deliveryid;
    }

    public double getThis_deliveryprice() {
        return this_deliveryprice;
    }

    public void setThis_deliveryprice(double this_deliveryprice) {
        this.this_deliveryprice = this_deliveryprice;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOtpcode() {
        return otpcode;
    }

    public void setOtpcode(String otpcode) {
        this.otpcode = otpcode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        if (address !=null){
           return address.replace("Address:", "").replaceAll("\n", "");
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrderid() {
        return orderid;
    }

    public void setOrderid(String orderid) {
        this.orderid = orderid;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public double getTotalamount() {
        return totalamount;
    }

    public void setTotalamount(double totalamount) {
        this.totalamount = totalamount;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getOrder_type() {
        return order_type;
    }

    public void setOrder_type(String order_type) {
        this.order_type = order_type;
    }

    public String getChange_order_type() {
        return change_order_type;
    }

    public void setChange_order_type(String change_order_type) {
        this.change_order_type = change_order_type;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public Date getEntrydate() {
        return entrydate;
    }

    public void setEntrydate(Date entrydate) {
        this.entrydate = entrydate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderid='" + orderid + '\'' +
                ", otpcode='" + otpcode + '\'' +
                ", id='" + id + '\'' +
                ", userid='" + userid + '\'' +
                ", part_deliveryid='" + part_deliveryid + '\'' +
                ", fullname='" + fullname + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", products='" + products + '\'' +
                ", payment_type='" + payment_type + '\'' +
                ", order_type='" + order_type + '\'' +
                ", change_order_type='" + change_order_type + '\'' +
                ", signature='" + signature + '\'' +
                ", remark='" + remark + '\'' +
                ", totalamount=" + totalamount +
                ", this_deliveryprice=" + this_deliveryprice +
                ", entrydate=" + entrydate +
                ", token='" + token + '\'' +
                '}';
    }
}

/*

{
        "response":200,
        "user_details":[
        {
        "id":"1",
        "company_name":"infinity marketing limited",
        "username":"test",
        "password":"123456",
        "fullname":"test",
        "address":"gulshan",
        "mobile":"01632774774",
        "nid":"123456789",
        "avatar":"test.jpg",
        "isactive":"1",
        "entrydate":"2020-12-08
        12:21:37"
        }
        ],
        "order_list":[
        {
        "id":"78",
        "userid":"195232",
        "fullname":"user44",
        "mobile":"01676007671",
        "email":"infinity@infotech.com",
        "address":"gulshan1",
        "sponsorid":"139533",
        "orderid":"I1pGncDOwP",
        "products":"[{\"productid\":\"68\",\"productname\":\"Toilet
        Tissue\",\"totalprice\":\"17\",\"totalItem\":1,\"photo\":\"1939432459.png\",\"warehouse\":\"3\"}]",
        "totalamount":"17",
        "payment_type":"onlinepayment",
        "payment_transaction_id":"TuQsz6i51oGZ3mHXzxNm2Zk6MaCju8gz",
        "payment_source":"onlinepayment",
        "order_status":"1",
        "order_type":"onprocess",
        "change_order_type":"onprocess",
        "delivery_man_id":"1",
        "warehouse_id":"3",
        "assign_warehouse_by":"3",
        "assign_warehouse_date":"2020-12-08
        15:11:53",
        "order_onprocess_by":"3",
        "order_onprocess_date":"2020-12-08 15:12:16",
        "order_success_by":"0",
        "order_success_date":"2020-12-02 22:46:57",
        "entrydate":"2020-12-02 22:46:57"
        }
        ],
        "message":""
        }*/
