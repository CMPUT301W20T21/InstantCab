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

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Collection;
import java.util.HashMap;

/**
 * In future updates the SignUp activity will be accompanied by a necessary Check and Flag function
 * This function will check for a min 6 digit password, a proper email with @ sign,
 * and a properly formatted phone number.


 * At the moment the class signs up a user for driver or rider and creates a custom profile
 * @author kbojakli

 */

public class SignUpAct extends AppCompatActivity {

    private EditText username;
    private EditText email;
    private EditText password;
    private EditText phone;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private Profile profile;
    private String TAG = "Auth";
    private String type;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_view);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        //password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        Button signUp = findViewById(R.id.signButton);
        final TextView flag = findViewById(R.id.flagText);
        final RadioGroup userType = findViewById(R.id.radioGroup);
        final RadioButton driver = findViewById(R.id.driverBox);
        final RadioButton rider = findViewById(R.id.riderBox);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userType.getCheckedRadioButtonId() == -1){
                    flag.setVisibility(View.VISIBLE);
                }
                else {
                    username = findViewById(R.id.signUser);
                    email = findViewById(R.id.signEmail);
                    password = findViewById(R.id.signPass);
                    phone = findViewById(R.id.signPhone);
                    String user = username.getText().toString();
                    final String mailText = email.getText().toString();
                    String pass = password.getText().toString();
                    String pNum = phone.getText().toString();
                    if(driver.isChecked()){
                        type = "Driver";
                    }
                    if(rider.isChecked()){
                        type = "Rider";
                    }
                    profile = new Profile(mailText, user, pNum, type);
                    /**
                     * This Authorization was built with the assistance of https://firebase.google.com/docs/auth/android/password-auth
                     * from the "Create a Password based account" section part 4
                     */
                    mAuth.createUserWithEmailAndPassword(mailText, pass)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();

                                        updateUI(user, mailText);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(SignUpAct.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                        updateUI(null, mailText);
                                    }
                                }
                            });
                }

            }
        });



    }

    // DriverRequest is not the correct Activity but will serve as a place holder for now
    /**
     * updateUI was created with the assistance of stackoverflow question
     * https://stackoverflow.com/questions/55697262/cannot-resolve-method-updateui
     * Question By Gabriele Puia
     * https://stackoverflow.com/users/10469999/gabriele-puia
     * Answered By Tamir Abutbul
     * https://stackoverflow.com/users/8274756/tamir-abutbul
     */
    public void  updateUI(FirebaseUser account, String mailText){
        if(account != null){
            //Added profile collection
            /**
             * Database collections were created with the assistance of the firebase tutorial
             * https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
             */
            db.collection("Users").document(mailText).set(profile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "addProfileCollection: success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "addProfileCollection: failure" + e);
                        }
                    });
            Toast.makeText(this,"You signed in successfully",Toast.LENGTH_LONG).show();
            if(type == "Driver"){
                //Sets a new database for the initial rating for new drivers
                Rating rating = new Rating(0,0);
                db.collection("Rating").document(mailText).set(rating)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "addRatingCollection: success");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "addRatingCollection: failure");
                            }
                        });

                startActivity(new Intent(SignUpAct.this,DriverLocationActivity.class));
            }
            else{
                startActivity(new Intent(SignUpAct.this,RiderMapsActivity.class));            }


        }else {
            Toast.makeText(this,"You did not sign in",Toast.LENGTH_LONG).show();
        }
    }
}
