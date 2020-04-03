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

public class ViewRiderActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
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
    private String phone;
    private String email;
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


        email = getIntent().getStringExtra("RIDER");
        if (email != null) {
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

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar_activity_info);
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
        if (type.equals("Driver")) {
            DocumentReference dbDoc = db.collection("Rating").document(email);
            dbDoc.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Rating driverRating = documentSnapshot.toObject(Rating.class);
                    assert driverRating != null;
                    good = driverRating.getGood();
                    bad = driverRating.getBad();
                    thumb_up.setText(String.valueOf(good));
                    thumb_down.setText(String.valueOf(bad));
                    rating.setVisibility(View.VISIBLE);
                }
            });

        } else {
            rating.setVisibility(View.GONE);
            thumb_up.setVisibility(View.GONE);
            thumb_down.setVisibility(View.GONE);
        }
        num.setText(phone);
        pr_email.setText(email);
        username.setText(name);

        iv_call.setVisibility(View.VISIBLE);
        iv_email.setVisibility(View.VISIBLE);

        iv_call.setOnClickListener(this);
        iv_email.setOnClickListener(this);
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

            /*make a call
             * */

            case R.id.iv_call:
                okCancelDialog = new OKCancelDialog(this);
                okCancelDialog.setCancelable(false);
                okCancelDialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        CallUtils.callPhone(ViewRiderActivity.this,phone);
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
