package com.example.i_delivery.view.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.i_delivery.R;
import com.example.i_delivery.data.api.RetrofitClient;
import com.example.i_delivery.data.api.RetrofitInterface;
import com.example.i_delivery.data.model.DataModel;
import com.example.i_delivery.data.model.Order;
import com.example.i_delivery.view.DetailsActivity;
import com.example.i_delivery.utils.local_db.PrefClient;
import com.example.i_delivery.utils.custom_interface.CustomOnItemClickListener;
import com.example.i_delivery.utils.custom_interface.OtpEventListener;
import com.example.i_delivery.utils.signature.SignDialogListener;
import com.example.i_delivery.utils.signature.SignatureDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.i_delivery.utils.Utils.showAlert;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.DeliveryViewHolder> {

    private static final String TAG = "AdapterOrder";
    private Context context;
    private List<Order> list = new ArrayList<>();
    private List<Order> filteredFinalList = new ArrayList<>();
    private CustomOnItemClickListener<Order> listener;

    public AdapterOrder(Context context) {
        this.context = context;
    }

    public void setList(List<Order> list) {
        this.list = list;
        this.filteredFinalList = list;
        notifyDataSetChanged();
    }

    public void setListener(CustomOnItemClickListener<Order> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_delivery, parent, false);

        return new DeliveryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        Order order = filteredFinalList.get(position);

        holder.tvOrderId.setText("Order Id - " + order.getOrderid());
        holder.tvOrderDate.setText("Date - " + new SimpleDateFormat("dd MMM yyyy").format(order.getEntrydate()));
        holder.tvTotalPrice.setText("Tk " + order.getTotalamount());
        holder.tvPaymentType.setText("Payment type - " + order.getPayment_type());
        holder.tvFullName.setText("Name - " + order.getFullname());
        holder.tvMobile.setText("Cell - " + order.getMobile());
        holder.tvAddress.setText("Address - " + order.getAddress());

        if (order.getOrder_type().equals("onprocess")) {
            if (order.getPayment_type().equals("onlinepayment") || order.getPayment_type().equals("walletpurchase")) {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.btnReturn.setVisibility(View.VISIBLE);
            }
            if (order.getPayment_type().equals("cashondelivery")) {
                holder.btnAccept.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnReturn.setVisibility(View.VISIBLE);
            }

        } else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnReturn.setVisibility(View.GONE);
        }

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setChange_order_type("success");
                _captureSign(order, position, false);
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setChange_order_type("cancel");
                _captureSign(order, position, true);
            }
        });

        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setChange_order_type("return");
                _captureSign(order, position, true);
            }
        });

        holder.favCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (order.getMobile() != null) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", order.getMobile(), null));
                    context.startActivity(intent);
                }
            }
        });

        holder.pDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("order_id", order.getOrderid());
                context.startActivity(intent);

            }
        });

    }


    private void _captureSign(Order order, int position, boolean enableRemark) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("enableRemark", enableRemark);
        SignatureDialogFragment dialog = new SignatureDialogFragment();
        dialog.setArguments(bundle);
        FragmentTransaction ft = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        dialog.show(ft, SignatureDialogFragment.TAG);

        dialog.setOnDrawConfirm(new SignDialogListener() {
            @Override
            public void onFinishSign(Bitmap bitmap, String s) {
                //
                order.setSignature(convert(bitmap));
                order.setRemark(s);
                order.setToken(new PrefClient(context).getToken());
               // Log.d(TAG, "onFinishSign: " + order);
               // Log.d(TAG, "onFinishSign: " + "f4aeb0737701f2333e932d2ffcf2ee73b4d4b83c17113f6e9dca56c2c2885dd8");
                //
                // TODO: 12/23/2020 show opt inputOTP

                _requestOTP(new OtpEventListener() {
                    @Override
                    public void onSuccessEvent(String otp) {
                        order.setOtpcode(otp);
                        _changeDeliveryStatus(order, position);
                    }
                });
            }
        });
    }

    private void _requestOTP(OtpEventListener otpListener) {
        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .otp_request(new PrefClient(context).getUser())
                .enqueue(new Callback<DataModel>() {
                    @Override
                    public void onResponse(Call<DataModel> call, Response<DataModel> response) {
                        if (response.body() == null) {
                            Toast.makeText(context, "Error in database.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.d(TAG, "onResponse: otp " + response.body());

                        if (response.body().getResponse() == 200) {
                            _verifyOptDialog(otpListener);

                        } else{
                            Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<DataModel> call, Throwable t) {
                        Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void _verifyOptDialog(OtpEventListener otpListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("OTP");
        builder.setMessage("Please insert OTP");
        builder.setCancelable(false);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(30, 16, 30, 16);
        final EditText inputOTP = new EditText(context);
        inputOTP.setLayoutParams(lp);
        inputOTP.setGravity(Gravity.CENTER);
        inputOTP.setInputType(InputType.TYPE_CLASS_NUMBER);
        inputOTP.setMaxLines(1);
        container.addView(inputOTP, lp);

        builder.setView(container);
        builder.setPositiveButton("Ok", null);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String value = inputOTP.getText().toString();
                        if (!value.isEmpty()) {
                            otpListener.onSuccessEvent(value);
                            alertDialog.dismiss();

                        } else
                            Toast.makeText(context, "Please input OTP", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        alertDialog.show();
    }

    private void _changeDeliveryStatus(Order order, int position) {

        order.setToken(new PrefClient(context).getToken());
        Log.d(TAG, "_changeDeliveryStatus: " + order);

        RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class)
                .sendData(order).enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {

                Log.d(TAG, "onResponse: " + response.body());

                if (response.body() == null) {
                    Toast.makeText(context, "Error in database.", Toast.LENGTH_LONG).show();
                   // Utils.logout(context);
                    return;
                }
                if (response.body().getResponse() == 200) {
                    //Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                    listener.OnItemClick(order, position);
                    showAlert(context, response.body().getMessage());
                    return;
                }
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                //Utils.logout(context);
                showAlert(context, response.body().getMessage());
            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
                //Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                showAlert(context, t.getMessage());
            }
        });
    }

    public static Bitmap convert(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    @Override
    public int getItemCount() {
        return filteredFinalList.size();
    }

    public class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvAddress, product, tvOrderId, tvMobile, tvPaymentType, tvOrderDate, tvTotalPrice;
        Button btnAccept, btnCancel, btnReturn;
        FloatingActionButton favCall;
        //LinearLayout productLinearLayout;
        TextView pDetails;

        public DeliveryViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvPaymentType = itemView.findViewById(R.id.tvPaymentType);
            tvFullName = itemView.findViewById(R.id.tvFullname);
            tvMobile = itemView.findViewById(R.id.tvMobile);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            // product = itemView.findViewById(R.id.product);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            favCall = itemView.findViewById(R.id.favCall);
            //productLinearLayout = itemView.findViewById(R.id.productLinearLayout);
            pDetails = itemView.findViewById(R.id.pDetails);

        }
    }
}
