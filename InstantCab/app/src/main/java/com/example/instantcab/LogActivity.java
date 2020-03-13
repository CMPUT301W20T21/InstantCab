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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This activity logs in the user based on email and password and then takes them to the Driver/Rider
 * home page depending on their user type
 * In the future the password will be auto hidden while typing
 * @author kbojakli
 */
public class LogActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String type;
    private String TAG = "Email Password";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_view);

        mAuth = FirebaseAuth.getInstance();

        Button login = findViewById(R.id.logButton);
        final EditText email = findViewById(R.id.logEmail);
        final EditText password = findViewById(R.id.logPass);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String mailText = email.getText().toString();
                String pass = password.getText().toString();

                /**
                 * This Authorization was built with the assistance of https://firebase.google.com/docs/auth/android/password-auth
                 * from the "Sign in a user with an email address and password" section part 3
                 */
                mAuth.signInWithEmailAndPassword(mailText,pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInAnonymously:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    updateUI(user, mailText);
                                }
                                else{
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                                    Toast.makeText(LogActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                    updateUI(null, mailText);
                                }
                            }
                        });
            }
        });
    }


    /**
     * updateUI was created with the assistance of stackoverflow question
     * https://stackoverflow.com/questions/55697262/cannot-resolve-method-updateui
     * Question By Gabriele Puia
     * https://stackoverflow.com/users/10469999/gabriele-puia
     * Answered By Tamir Abutbul
     * https://stackoverflow.com/users/8274756/tamir-abutbul
     */
    public void  updateUI(final FirebaseUser account, String email){
        /**
         * Database collections were created with the assistance of the firebase tutorial
         * https://firebase.google.com/docs/firestore/manage-data/add-data#custom_objects
         */
        db = FirebaseFirestore.getInstance();
        DocumentReference dbDoc = db.collection("Users").document(email);
        dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile profile = documentSnapshot.toObject(Profile.class);
                assert profile != null;
                type = profile.getType();

                if(account != null){
                    Toast.makeText(LogActivity.this,"You signed in successfully",Toast.LENGTH_LONG).show();
                    if(type.equals("Driver")){
                        startActivity(new Intent(LogActivity.this,DriverLocationActivity.class));
                    }
                    else{
                        startActivity(new Intent(LogActivity.this,RiderMapsActivity.class));
                    }
                }else {
                    Toast.makeText(LogActivity.this,"You did not sign in",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
