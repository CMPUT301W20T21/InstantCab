/**Copyright 2020 CMPUT301W20T21

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.*/

package com.example.instantcab;

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
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This ok cancel dialog box for make decision to call the phone or send a email
 * @author hgou
 */

public class OKCancelDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView mTvTitle;
    private LinearLayout mLayoutContent;
    private TextView mTvContent;
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

}
