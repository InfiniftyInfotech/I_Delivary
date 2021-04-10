package com.example.i_delivery.utils.signature;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.i_delivery.R;
import com.google.android.material.textfield.TextInputEditText;
import com.williamww.silkysignature.views.SignaturePad;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class SignatureDialogFragment extends DialogFragment {

    public static final String TAG = "SignatureDialogFragment";
    public SignDialogListener signDialogListener;
    private boolean enableRemark = false;

    public void setOnDrawConfirm(SignDialogListener signDialogListener) {
        this.signDialogListener = signDialogListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_I_Delivery_NoActionBar);
        if (getArguments()!=null){
            enableRemark = getArguments().getBoolean("enableRemark");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.dialog_sign, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView icon_close = view.findViewById(R.id.icon_close);
        icon_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        final SignaturePad signaturePad = view.findViewById(R.id.signature_pad);
        final Button btnConfirmSign = view.findViewById(R.id.btnConfirmSign);
        final Button btnClearSign = view.findViewById(R.id.btnClearSign);
        final TextInputEditText inputRemark = view.findViewById(R.id.inputRemark);

        if (!enableRemark){
            inputRemark.setVisibility(View.GONE);
        }

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() {

            }

            @Override
            public void onSigned() {
                btnConfirmSign.setEnabled(true);
                //btnConfirmSign.setBackground(getActivity().getDrawable(R.drawable.side_nav_bar));
                btnClearSign.setEnabled(true);
                //btnClearSign.setBackground(getActivity().getDrawable(R.drawable.side_nav_bar));
            }

            @Override
            public void onClear() {
                btnConfirmSign.setEnabled(false);
                //btnConfirmSign.setBackgroundColor(getContext().getResources().getColor(R.color.colorDisable));
                btnClearSign.setEnabled(false);
                //btnClearSign.setBackgroundColor(getContext().getResources().getColor(R.color.colorDisable));
            }
        });

        btnClearSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signaturePad.clear();
            }
        });

        btnConfirmSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (enableRemark && TextUtils.isEmpty(inputRemark.getText().toString())){
                    Toast.makeText(getContext(), "Please write remarks.", Toast.LENGTH_SHORT).show();
                } else{
                    signDialogListener.onFinishSign(signaturePad.getTransparentSignatureBitmap(), inputRemark.getText().toString());
                    dismiss();
                }
            }
        });
    }
}
