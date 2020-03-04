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
                                    /** What does the make text go to??**/
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

    // In this case Another Activity is the rider request page
    /** need Help getting the type object out of profile **/
    public void  updateUI(FirebaseUser account, String email){
        db = FirebaseFirestore.getInstance();
        DocumentReference dbDoc = db.collection("Users").document(email);
        dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile profile = documentSnapshot.toObject(Profile.class);
                type = profile.getType();
            }
        });

        if(account != null){
            Toast.makeText(this,"You signed in successfully",Toast.LENGTH_LONG).show();
            if(type == "Driver"){
                startActivity(new Intent(LogActivity.this,DriverRequest.class));
            }
            else{
                //startActivity(new Intent(LogActivity.this,AnotherActivity.class));
            }
        }else {
            Toast.makeText(this,"You did not sign in",Toast.LENGTH_LONG).show();
        }
    }
}
