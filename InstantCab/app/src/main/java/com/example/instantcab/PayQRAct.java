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


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.annotation.Nullable;

/**
 * This Activity is reserved for the eventual addition of QR Bucks payment
 * Alongside the option for the rider to rate the driver with a thumbs up or down
 * This will be the final page that each request sees before they are removed from the database
 * @author kbojakli
 */
public class PayQRAct extends AppCompatActivity {

    private int good;
    private int bad;
    private String TAG = "Updated";
    private FirebaseFirestore db;
    private String email;

    @Override
    protected void onCreate(@Nullable Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.qr_pay_activity);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            email = user.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        DocumentReference dbDoc = db.collection("Rating").document(email);
        dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Rating rating = documentSnapshot.toObject(Rating.class);
                assert rating != null;
                good = rating.getGood();
                bad = rating.getBad();
            }
        });

        Button confirm = findViewById(R.id.paymentConfirm);

        final RadioGroup rate = findViewById(R.id.rateGroup);
        final RadioButton goodButton = findViewById(R.id.radioGood);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rate.getCheckedRadioButtonId() != -1){
                    if(goodButton.isChecked()){
                        good += 1;
                    }
                    else{
                        bad += 1;
                    }
                }
                Rating newRating = new Rating(good,bad);
                db.collection("Rating").document(email).set(newRating)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "RatingUpdated: Success");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "RatingUpdated: Failure");
                            }
                        });

            }
        });


    }

}
