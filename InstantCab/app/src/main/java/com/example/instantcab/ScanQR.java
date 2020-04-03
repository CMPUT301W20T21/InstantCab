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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

/**
 * This activity goes to the QR scanner camera on a click of a button
 * @author kbojakli
 */
/**
 * ScanQR is taken from https://demonuts.com/scan-barcode-qrcode/ and updated to suit the project
 */
public class ScanQR extends AppCompatActivity {

    public static TextView scanresult;
    public TextView walletResult;
    private  Button qrbtn;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_activity);

        scanresult = (TextView) findViewById(R.id.scanresult);
        //Up to line 71 is to set the amount owed to the scanner page to display a wallet
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String email = user.getEmail();
        assert email != null;
        final DocumentReference docRef = db.collection("Wallet").document(email);
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e != null){
                    assert documentSnapshot != null;
                    Float money = (Float) documentSnapshot.get("Money");
                    walletResult = findViewById(R.id.walletresult);
                    assert money != null;
                    walletResult.setText("You are owed a total of: " + money.toString());
                }
            }
        });

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