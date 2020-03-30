package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ScanQR extends AppCompatActivity {

    public static TextView scanresult;
    private  Button qrbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_activity);

        scanresult = (TextView) findViewById(R.id.scanresult);

        qrbtn = (Button) findViewById(R.id.qrbtn);

        qrbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScanQR.this, ScanActivity.class);
                startActivity(intent);
            }
        });

    }
}