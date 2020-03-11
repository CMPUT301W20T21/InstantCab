//package com.example.instantcab;
//
//import android.content.Context;
//import android.os.Bundle;
//import android.text.Selection;
//import android.view.View;
//import android.view.WindowManager;
//import android.view.inputmethod.InputMethodManager;
//import android.widget.Button;
//import android.widget.EditText;
//
//
//
//public class EditDialog extends BaseDialog implements View.OnClickListener {
//    private Context mContext;
//    private EditText mEditText;
//    private Button btncancel;
//    private Button btnsave;
//
//    public EditDialog(Context context) {
//        super(context, R.style.EditDialog);
//        mContext = context;
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        View root = View.inflate(mContext, R.layout.layout_edit, null);
//        setContentView(root);
//        initView();
//        listener();
//    }
//
//    private void listener() {
//        btncancel.setOnClickListener(this);
//        btnsave.setOnClickListener(this);
//    }
//
//    private void initView() {
//        mEditText = findViewById(R.id.edit_file_name);
//        btncancel = findViewById(R.id.btn_cancel);
//        btnsave = findViewById(R.id.btn_ok);
//    }
//
//
//    public String getEditTextString() {
//        return mEditText.getText().toString();
//    }
//
//    @Override
//    public void onClick(View v) {
//        int id = v.getId();
//        switch (id) {
//            case R.id.btn_cancel:
//                hintKeyBoard();
//                dismiss();
//                if (mOnCancelListener != null) {
//                    mOnCancelListener.cancel();
//                }
//                break;
//            case R.id.btn_ok:
//                hintKeyBoard();
//                String name = mEditText.getText().toString();
//                name = name.trim();
//                if (mOKListener != null) {
//                    mOKListener.Ok();
//                    dismiss();
//                    }
//                }
//                break;
//        }
//    }
//
//    public void show(String fileName) {
//        show();
//        mEditText.setText(fileName);
//        mEditText.setFocusable(true);
//        mEditText.setFocusableInTouchMode(true);
//        mEditText.requestFocus();
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//
//        Selection.selectAll(mEditText.getText());
//    }
//
//    public void hintKeyBoard() {
//        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
//        if(imm.isActive()&&getCurrentFocus()!=null){
//            if (getCurrentFocus().getWindowToken()!=null) {
//                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//            }
//        }
//    }
//}
//
