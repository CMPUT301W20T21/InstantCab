package com.instantcab.example.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class BaseDialog extends Dialog {

    public OnCancelClickListener mOnCancelListener;
    protected OKClickListener mOKListener;

    public BaseDialog(Context context) {
        super(context);
        setTitleView();
        setWindowBackground();
    }

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        setTitleView();
        setWindowBackground();
    }

    protected BaseDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        setCancelMode();
        setTitleView();
        setWindowBackground();
    }

    private void setCancelMode() {
        setCanceledOnTouchOutside(true);
    }

    private void setTitleView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    
    private void setWindowBackground() {
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    @Override
    public void show() {
        try {
            super.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
        } catch (Exception e) {

        }
    }

    public void setOnCancelClickListener(OnCancelClickListener mListener) {
        this.mOnCancelListener = mListener;
    }

    public void setOkClickListener(OKClickListener listener) {
        this.mOKListener = listener;
    }

    public interface OKClickListener {
        void Ok();
    }

    public interface OnCancelClickListener {
        void cancel();
    }
}
