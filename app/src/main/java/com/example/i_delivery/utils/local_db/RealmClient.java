package com.example.i_delivery.utils.local_db;

import android.content.Context;

import com.example.i_delivery.data.model.Order;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

public class RealmClient {

    private Context context;
    private Realm realm;

    public RealmClient(Context context) {
        this.context = context;
        realm = Realm.getDefaultInstance();
    }

    public void deleteRealm(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });

    }

    public void saveOrders(List<Order> orders){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmList<Order> _orderList = new RealmList<>();
                _orderList.addAll(orders);
                realm.insertOrUpdate(_orderList);
            }
        });
    }
}
