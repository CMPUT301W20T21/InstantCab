package com.example.instantcab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DriverAcceptRequest extends AppCompatActivity {
    private TextView textFrom;
    private TextView textTo;
    private TextView textEmail;
    private TextView textFare;
    private String strFrom;
    private String strTo;
    private String strEmail;
    private String strFare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_accept_request);

        Intent intent = getIntent();
        strFrom = intent.getExtras().getString("from");
        strTo = intent.getExtras().getString("to");
        strEmail = intent.getExtras().getString("email");
        strFare = intent.getExtras().getString("fare");

        textFrom = findViewById(R.id.from);
        textFrom.setText(strFrom);
        textTo = findViewById(R.id.to);
        textTo.setText(strTo);
        textEmail = findViewById(R.id.driver_name);
        textEmail.setText(strEmail);
        textFare = findViewById(R.id.fare);
        textFare.setText(strFare);
    }
}
