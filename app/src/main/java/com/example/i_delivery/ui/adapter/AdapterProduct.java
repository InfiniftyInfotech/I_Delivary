package com.example.i_delivery.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.i_delivery.R;
import com.example.i_delivery.model.SingleOrderResponse;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterProduct extends RecyclerView.Adapter<AdapterProduct.ProductViewHolder> {

    private static final String TAG = "AdapterProduct";
    private Context context;
    private List<SingleOrderResponse.Product> productList = new ArrayList<>();

    public AdapterProduct(Context context) {
        this.context = context;
    }

    public void setProductList(List<SingleOrderResponse.Product> productList) {
        this.productList = productList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order_product, parent, false);

        return new ProductViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SingleOrderResponse.Product product = productList.get(position);

        Glide.with(context).load("https://infinitynetworkmarketing.com/infinity-backend/products/" + product.getPhoto())
                .placeholder(R.drawable.logo)
                .into(holder.pThumb);

        holder.code.setText("Code: " + product.getCode());
        holder.name.setText("Name: " + product.getProduct_name());
        holder.price.setText("Price: TK " + product.getPrice() + "");
        holder.count.setText(product.getDelivery_qty() != null ? "Qty: " + product.getDelivery_qty() : "Qty: " + product.getQty());
        holder.attr.setText("Attribute: " + product.getAttribute());

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }


    public class ProductViewHolder extends RecyclerView.ViewHolder {

        TextView code, name, price, count, attr;
        ImageView pThumb;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            pThumb = itemView.findViewById(R.id.pThumb);
            code = itemView.findViewById(R.id.code);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            count = itemView.findViewById(R.id.count);
            attr = itemView.findViewById(R.id.attr);

        }
    }
}
