package com.example.instantcab;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class DriverRequest extends AppCompatActivity {
    private Button mBtnConfirm;
    private Button mBtnCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request);

        mBtnConfirm = findViewById(R.id.confirm_request);
        mBtnCancel = findViewById(R.id.cancel_request);

        mBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //confirm request
            }
        });

        mBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //cancel request
            }
        });
    }
}
