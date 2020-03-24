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

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

/**
 * This base dialog box for build click listener for ok and cancel choices
 * @author hgou
 */

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
