package com.example.i_delivery.ui.adapter;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.i_delivery.R;
import com.example.i_delivery.model.AllOrderResponse;
import com.example.i_delivery.model.Order;
import com.example.i_delivery.ui.OrderDetailsActivity;
import com.example.i_delivery.utils.PrefClient;
import com.example.i_delivery.utils.custom_interface.CustomOnItemClickListener;
import com.example.i_delivery.utils.custom_interface.OtpEventListener;
import com.example.i_delivery.utils.signature.SignDialogListener;
import com.example.i_delivery.utils.signature.SignatureDialogFragment;
import com.example.i_delivery.viewmodel.DataViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.example.i_delivery.utils.Utils.showAlert;

public class AdapterOrder extends RecyclerView.Adapter<AdapterOrder.DeliveryViewHolder> {

    private static final String TAG = "AdapterOrder";
    private Context context;
    private List<Order> orderList = new ArrayList<>();
    private CustomOnItemClickListener<Order> listener;

    public AdapterOrder(Context context) {
        this.context = context;
    }

    public void setList(List<Order> list) {
        this.orderList = list;
        notifyDataSetChanged();
    }

    public void setListener(CustomOnItemClickListener<Order> listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public DeliveryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_order, parent, false);

        return new DeliveryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull DeliveryViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvOrderId.setText("Order Id - " + order.getOrderid());
        holder.tvOrderDate.setText("Date - " + new SimpleDateFormat("dd MMM yyyy").format(order.getEntrydate()));
        holder.tvTotalPrice.setText("Tk " + (order.getPart_deliveryid() != null ? order.getThis_deliveryprice() : order.getTotalamount()));
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
                _captureSign(order, false);
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setChange_order_type("cancel");
                _captureSign(order, true);
            }
        });

        holder.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.setChange_order_type("return");
                _captureSign(order, true);
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

                Intent intent = new Intent(context, OrderDetailsActivity.class);
                intent.putExtra("order_id", order.getOrderid());
                intent.putExtra("part_deliveryid", order.getPart_deliveryid());
                context.startActivity(intent);

            }
        });

    }

    private void _captureSign(Order order, boolean enableRemark) {
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

                _requestOTP(new OtpEventListener() {
                    @Override
                    public void onSuccessEvent(String otp) {
                        order.setOtpcode(otp);
                        _sendOrderToDelivery(order);
                    }
                });
            }
        });
    }

    private void _requestOTP(OtpEventListener otpListener) {
        DataViewModel dataViewModel = new DataViewModel();
        dataViewModel.requestOTP(new PrefClient(context).getUser())
                .observe((LifecycleOwner) context, new Observer<AllOrderResponse>() {
                    @Override
                    public void onChanged(AllOrderResponse allOrderResponse) {
                        if (allOrderResponse != null) {
                            if (allOrderResponse.getResponse() == 200) {
                                _verifyOptDialog(otpListener);
                            } else {
                                Toast.makeText(context, allOrderResponse.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(context, "An error occurred! try again", Toast.LENGTH_LONG).show();
                        }
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

    private void _sendOrderToDelivery(Order order) {
        order.setToken(new PrefClient(context).getToken());

        Log.d(TAG, "_sendOrderToDelivery: token = " + order.getToken());

        DataViewModel dataViewModel = new DataViewModel();

        if (order.getPart_deliveryid()!=null){
            dataViewModel.sendPartOrderToDelivery(order).observe((LifecycleOwner) context, new Observer<AllOrderResponse>() {
                @Override
                public void onChanged(AllOrderResponse allOrderResponse) {
                    if (allOrderResponse != null) {
                        if (allOrderResponse.getResponse() == 200) {
                            listener.OnItemClick(order, -1);
                            showAlert(context, allOrderResponse.getMessage());
                        } else {
                            showAlert(context, allOrderResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(context, "An error occurred! try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            dataViewModel.sendOrderToDelivery(order).observe((LifecycleOwner) context, new Observer<AllOrderResponse>() {
                @Override
                public void onChanged(AllOrderResponse allOrderResponse) {
                    if (allOrderResponse != null) {
                        if (allOrderResponse.getResponse() == 200) {
                            listener.OnItemClick(order, -1);
                            showAlert(context, allOrderResponse.getMessage());
                        } else {
                            showAlert(context, allOrderResponse.getMessage());
                        }
                    } else {
                        Toast.makeText(context, "An error occurred! try again.", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
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
        return orderList.size();
    }

    public class DeliveryViewHolder extends RecyclerView.ViewHolder {
        TextView tvFullName, tvAddress, product, tvOrderId, tvMobile, tvPaymentType, tvOrderDate, tvTotalPrice;
        Button btnAccept, btnCancel, btnReturn;
        FloatingActionButton favCall;
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
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnReturn = itemView.findViewById(R.id.btnReturn);
            favCall = itemView.findViewById(R.id.favCall);
            pDetails = itemView.findViewById(R.id.pDetails);

        }
    }
}
