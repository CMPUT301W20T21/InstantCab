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
                                        /** What is EmailPasswordActivity **/
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
    // In this case Another Activity is the rider request page
    public void  updateUI(FirebaseUser account, String mailText){
        if(account != null){
            //Added profile collection
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
            Toast.makeText(this,"U Signed In successfully",Toast.LENGTH_LONG).show();
            if(type == "Driver"){
                startActivity(new Intent(SignUpAct.this,DriverRequest.class));
            }
            else{
                //startActivity(new Intent(SignUpAct.this,AnotherActivity.class));
            }
        }else {
            Toast.makeText(this,"U Didnt signed in",Toast.LENGTH_LONG).show();
        }
    }
}
