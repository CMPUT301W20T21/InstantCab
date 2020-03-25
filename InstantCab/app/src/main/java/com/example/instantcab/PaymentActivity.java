/**Copyright 2020 CMPUT301W20T21
 *
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

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
/**
 * This class is an activity used for the rider to tip the driver and also calculate
 * the total amount of money the rider needs to pay. Then it leads to
 * another payment activity which generates QR code.
 *
 * @author lijiangn
 */
public class PaymentActivity extends AppCompatActivity {
    private Button ButtonPay;
    private Button ButtonTip;
    private TextView totalCost;
    private TextView showFare;
    private EditText tipAmount;
    private String fare;
    private String driverEmail;
    private float tip;
    private float total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        fare = getIntent().getStringExtra("FARE");
        driverEmail = getIntent().getStringExtra("DRIVER");
        if (fare == null) fare = "10";

        ButtonPay = findViewById(R.id.pay);
        ButtonTip = findViewById(R.id.tip);
        totalCost = findViewById(R.id.total_cost);
        tipAmount = findViewById(R.id.tip_amount);
        // show the fare
        showFare = findViewById(R.id.fare);
        showFare.setText(fare);
        fare = fare.replace("$",""); // get rid of the dollar sign
        totalCost.setText(String.format(getString(R.string.total), Float.parseFloat(fare)));

        ButtonTip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click to tip
                if (!tipAmount.getText().toString().equals("")) {
                    tip = Float.parseFloat((tipAmount.getText().toString()));
                    total = tip + Float.parseFloat(fare);
                    totalCost.setText(String.format(getString(R.string.total), total));
                }
            }
        });

        ButtonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Click to pay
                Intent intent = new Intent(PaymentActivity.this, PayQRAct.class);
                intent.putExtra("FARE", total);
                intent.putExtra("Driver", driverEmail);
                startActivity(intent);
            }
        });
    }
}