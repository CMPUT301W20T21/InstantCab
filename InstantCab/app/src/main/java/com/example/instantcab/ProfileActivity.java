//package com.example.instantcab;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.FirebaseFirestore;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//
//
//public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
//    private Toolbar toolbar;
//    private int viewType;
//    private EditDialog editDialog;
//
//    private ImageView user;
//    private TextView username;
//    private TextView num;
//    private ImageView num_edit;
//    private ImageView call;
//    private TextView email;
//    private ImageView email_edit;
//    private ImageView ic_email;
//    private LinearLayout rating;
//    private TextView thumb_up;
//    private TextView thumb_down;
//    private FirebaseAuth mAuth;
//    private FirebaseFirestore db;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mAuth = FirebaseAuth.getInstance();
//        db = FirebaseFirestore.getInstance();
//        setContentView(R.layout.activity_user);
//        init();
//    }
//
//    private void init() {
//        toolbar = findViewById(R.id.toolbar_activity_info);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        user= findViewById(R.id.user);
//        username = findViewById(R.id.username);
//        num = findViewById(R.id.num);
//        num_edit = findViewById(R.id.num_edit);
//        call = findViewById(R.id.call);
//        email = findViewById(R.id.email);
//        email_edit = findViewById(R.id.email_edit);
//        ic_email = findViewById(R.id.ic_email);
//        rating = findViewById(R.id.rating);
//        thumb_up = findViewById(R.id.thumb_up);
//        thumb_down = findViewById(R.id.thumb_down);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        call.setVisibility(View.GONE);
//        email.setVisibility(View.GONE);
//        num_edit.setVisibility(View.VISIBLE);
//        email_edit.setVisibility(View.VISIBLE);
//
//        num.setText(user.getPhone());
//        email.setText(user.getEmail());
//        rating.setVisibility(View.VISIBLE);
//
//        thumb_up.setText(String.valueOf(user.getThumb_up()));
//        thumb_down.setText(String.valueOf(user.getThumb_down()));
//        num_edit.setOnClickListener(this);
//        email_edit.setOnClickListener(this);
//        call.setOnClickListener(this);
//        ic_email.setOnClickListener(this);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            finish();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            /*edit user phone number
//             * */
//            case R.id.num_edit:
//                editDialog = new EditDialog(this);
//                editDialog.setCancelable(false);
//                editDialog.setOkClickListener(new BaseDialog.OKClickListener() {
//                    @Override
//                    public void Ok() {
//                        user.updatePhoneNumber(editDialog.getEditTextString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "User phone number updated.");
//                                }
//                            }
//                        });
//                        editDialog.dismiss();
//                        num.setText(editDialog.getEditTextString());
//                    }
//                });
//                editDialog.setOnCancelClickListener(new BaseDialog.OnCancelClickListener() {
//                    @Override
//                    public void cancel() {
//                        editDialog.dismiss();
//                    }
//                });
//                editDialog.show(user.getPhone());
//                break;
//            /*edit user email
//            * */
//            case R.id.email_edit:
//                editDialog = new EditDialog(this);
//                editDialog.setCancelable(false);
//                editDialog.setOkClickListener(new BaseDialog.OKClickListener() {
//                    @Override
//                    public void Ok() {
//                        user.updateEmail(editDialog.getEditTextString()).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "User email address updated.");
//                                }
//                            }
//                        });
//                        editDialog.dismiss();
//                        email.setText(editDialog.getEditTextString());
//                    }
//                });
//                editDialog.setOnCancelClickListener(new BaseDialog.OnCancelClickListener() {
//                    @Override
//                    public void cancel() {
//                        editDialog.dismiss();
//                    }
//                });
//                editDialog.show(user.getEmail());
//                break;
//
//        }
//    }
//}
