package com.xagu.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xagu.himalaya.R;

/**
 * Created by XAGU on 2020/3/5
 * Email:xagu_qc@foxmail.com
 * Describe: TODO
 */
public class ConfirmCheckBoxDialog extends Dialog {

    private TextView mBtnConfirm;
    private TextView mBtnCancel;
    private OnBtnClickListener btnClicklistener = null;
    private TextView mDialogTitle;
    private CheckBox mDialogCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initEvent();
    }

    private void initEvent() {
        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mDialogCheckBox.isChecked();
                if (btnClicklistener != null) {
                    btnClicklistener.onCancelClick(isChecked);
                }
                dismiss();
            }
        });
        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = mDialogCheckBox.isChecked();
                if (btnClicklistener != null) {
                    btnClicklistener.onConfirmClick(isChecked);
                }
                dismiss();
            }
        });
    }

    private void initView() {
        mBtnConfirm = findViewById(R.id.btn_confirm);
        mBtnCancel = findViewById(R.id.btn_cancel);
        mDialogCheckBox = findViewById(R.id.dialog_checkbox);
    }

    public void setOnBtnClickListener(OnBtnClickListener listener){
        this.btnClicklistener = listener;
    }

    public interface OnBtnClickListener{
        void onConfirmClick(boolean isChecked);
        void onCancelClick(boolean isChecked);
    }
}
