package com.example.instantcab;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {
    private Button ButtonPay;
    private Button ButtonTip;
    private TextView totalCost;
    private TextView showFare;
    private EditText tipAmount;
    private String fare;
    private float tip;
    private float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        fare = getIntent().getStringExtra("FARE");

        ButtonPay = findViewById(R.id.pay);
        ButtonTip = findViewById(R.id.tip);
        totalCost = findViewById(R.id.total_cost);
        tipAmount = findViewById(R.id.tip_amount);
        // show the fare
        showFare = findViewById(R.id.fare);
        showFare.setText("$"+fare);
        totalCost.setText(String.format(getString(R.string.total), Float.parseFloat(fare)));

        ButtonTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click to tip
                tip = Float.parseFloat((tipAmount.getText().toString()));
                total = tip + Float.parseFloat(fare);
                totalCost.setText(String.format(getString(R.string.total), total));
            }
        });

        ButtonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click to pay
                // jump to another payment activity with QR code and ratings
                
                //Intent intent = new Intent(PaymentActivity.this, anotherPaymentActivity.class);
                //intent.putExtra("FARE", total);
                //startActivity(intent);
            }
        });
    }
}
