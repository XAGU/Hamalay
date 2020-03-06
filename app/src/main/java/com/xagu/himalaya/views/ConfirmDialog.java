package com.xagu.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xagu.himalaya.R;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class ConfirmDialog extends Dialog {

    private TextView mBtnConfirm;
    private TextView mBtnCancel;
    private OnBtnClickListener btnClicklistener = null;

    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initEvent();
    }

    private void initEvent() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnClicklistener != null) {
                    btnClicklistener.onCancelClick();
                }
                dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnClicklistener != null) {
                    btnClicklistener.onConfirmClick();
                }
                dismiss();
            }
        });
    }

    private void initView() {
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnCancel = findViewById(R.id.btn_cancel);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener){
        this.btnClicklistener = listener;
    }

    public interface OnBtnClickListener{
        void onConfirmClick();
        void onCancelClick();
    }
}
