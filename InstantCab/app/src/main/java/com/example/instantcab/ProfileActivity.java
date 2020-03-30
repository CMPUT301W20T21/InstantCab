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
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * This activity is for profile, display different profile UI for diver and user
 * can edit the email and phone number on profile
 * @author hgou
 */

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private int viewType;
    private EditDialog editDialog;
    private OKCancelDialog okCancelDialog;

    private ImageView user;
    private TextView username;
    private TextView num;
    private ImageView num_edit;
    private TextView pr_email;
    private LinearLayout rating;
    private TextView thumb_up;
    private TextView thumb_down;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private int good;
    private int bad;
    private int view;
    private String phone;
    private String email;
    private String driverEmail;
    private String driverPhone;
    private String driverName;
    private String name;
    private String type;
    private ImageView iv_call;
    private ImageView iv_email;
    private String TAG = "Email Password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_user);


        driverEmail = getIntent().getStringExtra("DRIVER");
        if (driverEmail != null) {
            view = 1;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            email = user.getEmail();
        }

        DocumentReference dbDoc = db.collection("Users").document(email);
        dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Profile profile = documentSnapshot.toObject(Profile.class);
                assert profile != null;
                phone = profile.getPhone();
                email = profile.getEmail();
                name = profile.getUsername();
                type = profile.getType();

                init();

            }
        });

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar_activity_info);
        //     setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        user= findViewById(R.id.user);
        username = findViewById(R.id.username);
        num = findViewById(R.id.num);
        num_edit = findViewById(R.id.num_edit);
        pr_email = findViewById(R.id.pr_email);
        rating = findViewById(R.id.rating);
        thumb_up = findViewById(R.id.thumb_up);
        thumb_down = findViewById(R.id.thumb_down);
        iv_email = findViewById(R.id.iv_email);
        iv_call = findViewById(R.id.iv_call);
        if (view != 1) {
            if (type == "Driver") {
                num_edit.setVisibility(View.VISIBLE);
                rating.setVisibility(View.VISIBLE);

                DocumentReference dbDoc = db.collection("Users").document(email);
                db.collection("Rating").document(email);
                dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Rating rating = documentSnapshot.toObject(Rating.class);
                        Log.i("rating", "we here");
                        assert rating != null;
                        good = rating.getGood();
                        bad = rating.getBad();
                        thumb_up.setText(String.valueOf(good));
                        thumb_down.setText(String.valueOf(bad));
                        Log.i("rating", "has rating");
                    }
                });
            } else {
                num_edit.setVisibility(View.VISIBLE);
                rating.setVisibility(View.GONE);
                thumb_up.setVisibility(View.GONE);
                thumb_down.setVisibility(View.GONE);
            }
            num.setText(phone);
            pr_email.setText(email);
            username.setText(name);

            num_edit.setOnClickListener(this);

        } else{
            DocumentReference dbDoc = db.collection("Users").document(driverEmail);
            dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Profile profile = documentSnapshot.toObject(Profile.class);
                    Log.i("users", "we here");
                    assert profile != null;
                    driverPhone = profile.getPhone();
                    driverEmail = profile.getEmail();
                    driverName = profile.getUsername();
                    type = profile.getType();
                    Log.i("users", "has users");
                }
            });

            DocumentReference Doc = db.collection("Users").document(driverEmail);
            db.collection("Rating").document(driverEmail);
            Doc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Rating rating = documentSnapshot.toObject(Rating.class);
                    Log.i("rating", "we here");
                    assert rating != null;
                    good = rating.getGood();
                    bad = rating.getBad();
                    thumb_up.setText(String.valueOf(good));
                    thumb_down.setText(String.valueOf(bad));
                    Log.i("rating", "has rating");
                }
            });
            rating.setVisibility(View.VISIBLE);
            iv_call.setVisibility(View.VISIBLE);
            iv_email.setVisibility(View.VISIBLE);

            num.setText(driverPhone);
            pr_email.setText(driverEmail);
            username.setText(driverName);

            iv_call.setOnClickListener(this);
            iv_email.setOnClickListener(this);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*edit user phone number
             * */
            case R.id.num_edit:
                editDialog = new EditDialog(this);
                editDialog.setCancelable(false);
                editDialog.setOkClickListener(new BaseDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        phone = editDialog.getEditTextString();
                        Profile newProfile = new Profile(email,name,phone,type);
                        db.collection("Users").document(email).set(newProfile);
                        num.setText(editDialog.getEditTextString());
                    }
                });
                editDialog.setOnCancelClickListener(new BaseDialog.OnCancelClickListener() {
                    @Override
                    public void cancel() {
                        editDialog.dismiss();
                    }
                });
                editDialog.show(phone);
                break;

            /*make a call
             * */

            case R.id.iv_call:
                okCancelDialog = new OKCancelDialog(this);
                okCancelDialog.setCancelable(false);
                okCancelDialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        CallUtils.callPhone(ProfileActivity.this,phone);
                        okCancelDialog.dismiss();
                    }
                });
                okCancelDialog.setOnCancelClickListener(new OKCancelDialog.OnCancelClickListener() {
                    @Override
                    public void cancel() {
                        okCancelDialog.dismiss();
                    }
                });

                okCancelDialog.show();
                okCancelDialog.setOKCancel(R.string.ok,R.string.cancel);
                okCancelDialog.setTvTitle("Call "+phone);
                break;
            /*send a email
             * */
            case R.id.iv_email:
                CallUtils.sendMail(this,email);
                break;
        }
    }
}
