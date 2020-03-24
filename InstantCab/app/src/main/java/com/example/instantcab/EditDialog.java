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
import android.content.Context;
import android.os.Bundle;
import android.text.Selection;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

/**
 * This edit dialog box used for edit email and phone number
 * @author hgou
 */

public class EditDialog extends BaseDialog implements View.OnClickListener {
    private Context mContext;
    private EditText mEditText;
    private Button btn_cancel;
    private Button btn_save;

    public EditDialog(Context context) {
        super(context, R.style.EditDialog);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = View.inflate(mContext, R.layout.layout_edit, null);
        setContentView(root);
        initView();
        listener();
    }

    private void listener() {
        btn_cancel.setOnClickListener(this);
        btn_save.setOnClickListener(this);
    }

    private void initView() {
        mEditText = findViewById(R.id.edit_file_name);
        btn_cancel = findViewById(R.id.btn_cancel);
        btn_save = findViewById(R.id.btn_ok);
    }


    public String getEditTextString() {
        return mEditText.getText().toString();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_cancel:
                mOnCancelListener.cancel();
                break;
            case R.id.btn_ok:
                mOKListener.Ok();
                break;
        }

    }

    public void show(String fileName) {
        show();
        mEditText.setText(fileName);
        mEditText.setFocusable(true);
        mEditText.setFocusableInTouchMode(true);
        mEditText.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Selection.selectAll(mEditText.getText());
    }
}
