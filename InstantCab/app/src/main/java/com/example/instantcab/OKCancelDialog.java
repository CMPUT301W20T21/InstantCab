package com.instantcab.example;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caller.example.R;

public class OKCancelDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView mTvTitle;
    private LinearLayout mLayoutContent;
    private TextView mTvContent;
    private CheckBox mCbSpm;
    private OKClickListener mListener;
    private OnCancelClickListener mOnCancelListener;
    private LinearLayout mLayoutTitle;
    private LinearLayout mLayoutOkCancel;
    private Button btncancel;
    private Button btnok;


    public OKCancelDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_ok_cancel_dialog);
        mLayoutTitle = (LinearLayout) findViewById(R.id.layout_title);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mLayoutContent = (LinearLayout) findViewById(R.id.layout_content);
        mTvContent = (TextView) findViewById(R.id.tv_content);
        mCbSpm = (CheckBox) findViewById(R.id.cb_report_call_as_spam);

        mLayoutOkCancel = (LinearLayout) findViewById(R.id.layout_ok_cancel);
        btncancel = findViewById(R.id.btn_cancel);
        btnok = findViewById(R.id.btn_ok);
        btncancel.setOnClickListener(this);
        btnok.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_ok:
                dismiss();
                if (mListener != null) {
                    mListener.Ok();
                }
                break;
            case R.id.btn_cancel:
                if (mOnCancelListener != null) {
                    mOnCancelListener.cancel();
                }
                dismiss();
                break;
        }
    }

    public void setOnCancelClickListener(OnCancelClickListener mListener) {
        this.mOnCancelListener = mListener;
    }

    public interface OKClickListener {
        void Ok();
    }

    public interface OnCancelClickListener {
        void cancel();
    }

    @Override
    public void show() {
        super.show();
        WindowManager windowManager = ((Activity) mContext).getWindowManager();
        Display d = windowManager.getDefaultDisplay();  
        WindowManager.LayoutParams p = getWindow().getAttributes();  
        p.height = (int) (d.getHeight() * 0.3);   
        p.width = (int) (d.getWidth() * 0.80);    
        getWindow().setAttributes(p); 

    }

    public void setOkClickListener(OKClickListener listener) {
        this.mListener = listener;
    }

    public void setTvTitle(String str) {
        if (mLayoutTitle != null)
            mLayoutTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(str);
    }

    public void setTvTitle(int strResId) {
        if (mLayoutTitle != null)
            mLayoutTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(strResId);
    }

    public void setTvTitle(int strResId, String name) {
        if (mLayoutTitle != null)
            mLayoutTitle.setVisibility(View.VISIBLE);
        mTvTitle.setText(Html.fromHtml(String.format(mContext.getResources().getString(strResId), name)));
    }

    public void setContent(String content, boolean isShowContent, boolean isShowCheckBox) {
        mLayoutContent.setVisibility(View.VISIBLE);
        if (!isShowContent) {
            mLayoutContent.setVisibility(View.GONE);
        } else {
            mLayoutContent.setVisibility(View.VISIBLE);
            mTvContent.setText(content);
            if (!isShowCheckBox) {
                mCbSpm.setVisibility(View.GONE);
            } else {
                mCbSpm.setVisibility(View.VISIBLE);
                mCbSpm.setChecked(true);
            }
        }
    }

    public void setOKCancel(String okStr, String cancelStr) {
        mLayoutOkCancel.setVisibility(View.VISIBLE);
        btnok.setText(okStr);
        btncancel.setText(cancelStr);
    }

    public void setOKCancel(int okResId, int cancelResId) {
        mLayoutOkCancel.setVisibility(View.VISIBLE);
        btnok.setText(okResId);
        btncancel.setText(cancelResId);
    }

    public void setCheckboxContent(String str, boolean isShowContent) {
        mCbSpm.setText(str);
        if (!isShowContent) {
            mTvContent.setVisibility(View.GONE);
        }
    }

    public void setCheckboxContent(int resId, boolean isShowContent) {
        if (!isShowContent) {
            mTvContent.setVisibility(View.GONE);
        }
        mCbSpm.setText(resId);
    }

    public boolean getCheckBoxState() {
        return mCbSpm.isChecked();
    }

}
