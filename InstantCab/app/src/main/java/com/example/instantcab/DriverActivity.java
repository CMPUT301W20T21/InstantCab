package com.example.instantcab;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.caller.example.ApplicationEx;
import com.caller.example.R;
import com.caller.example.database.AppDatabase;
import com.caller.example.database.DataRepository;
import com.caller.example.database.entity.DriverBean;
import com.caller.example.dialog.BaseDialog;
import com.caller.example.dialog.EditDialog;
import com.caller.example.dialog.OKCancelDialog;
import com.caller.example.utils.CallUtils;
import com.caller.example.utils.GlideUtil;
import com.caller.example.utils.ToastUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.caller.example.activity.ActivityBuilder.DRIVER_DATA;
import static com.caller.example.activity.ActivityBuilder.DRIVER_VEIW_TYPE;
import static com.caller.example.activity.ActivityBuilder.TYPE_DRIVER_VIEW;

public class DriverActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar toolbar;
    private DriverBean driverBean;
    private int viewType;
    private EditDialog editDialog;
    private OKCancelDialog okCancelDialog;

    private ImageView iv_driver;
    private TextView tv_driver_name;
    private TextView tv_num;
    private ImageView iv_num_edit;
    private ImageView iv_call;
    private TextView tv_email;
    private ImageView iv_email_edit;
    private ImageView iv_email;
    private LinearLayout ll_rating;
    private TextView tv_rating_on;
    private TextView tv_rating_off;
    private DataRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver);
        init();
    }

    private void init() {
        driverBean = (DriverBean) getIntent().getSerializableExtra(DRIVER_DATA);
        if (null == driverBean) {
            ToastUtils.showToast(this, getString(R.string.data_error));
            finish();
            return;
        }
        repository = DataRepository.getInstance(AppDatabase.getDatabase(ApplicationEx.getInstance()));
        viewType = getIntent().getIntExtra(DRIVER_VEIW_TYPE, TYPE_DRIVER_VIEW);
        toolbar = findViewById(R.id.toolbar_activity_info);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        iv_driver = findViewById(R.id.iv_driver);
        tv_driver_name = findViewById(R.id.tv_driver_name);
        tv_num = findViewById(R.id.tv_num);
        iv_num_edit = findViewById(R.id.iv_num_edit);
        iv_call = findViewById(R.id.iv_call);
        tv_email = findViewById(R.id.tv_email);
        iv_email_edit = findViewById(R.id.iv_email_edit);
        iv_email = findViewById(R.id.iv_email);
        ll_rating = findViewById(R.id.ll_rating);
        tv_rating_on = findViewById(R.id.tv_rating_on);
        tv_rating_off = findViewById(R.id.tv_rating_off);

        GlideUtil.loadImg(driverBean.getDriver_pic(), false).into(iv_driver);
        tv_driver_name.setText(driverBean.getDriver_name());
        if (viewType == TYPE_DRIVER_VIEW) {
            iv_call.setVisibility(View.GONE);
            iv_email.setVisibility(View.GONE);
            iv_num_edit.setVisibility(View.VISIBLE);
            iv_email_edit.setVisibility(View.VISIBLE);
        } else {
            iv_call.setVisibility(View.VISIBLE);
            iv_email.setVisibility(View.VISIBLE);
            iv_num_edit.setVisibility(View.GONE);
            iv_email_edit.setVisibility(View.GONE);
        }
        tv_num.setText(driverBean.getDriver_number());
        tv_email.setText(driverBean.getDriver_email());
        if (driverBean.getRating_on() == 0 && driverBean.getRating_off() == 0) {
            ll_rating.setVisibility(View.GONE);
        } else {
            ll_rating.setVisibility(View.VISIBLE);
        }
        tv_rating_on.setText(String.valueOf(driverBean.getRating_on()));
        tv_rating_off.setText(String.valueOf(driverBean.getRating_off()));
        iv_num_edit.setOnClickListener(this);
        iv_email_edit.setOnClickListener(this);
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
            case R.id.iv_num_edit:
                editDialog = new EditDialog(this);
                editDialog.setCancelable(false);
                editDialog.setOkClickListener(new BaseDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        driverBean.setDriver_number(editDialog.getEditTextString());
                        repository.insertDriverBeans(driverBean);
                        editDialog.dismiss();
                        tv_num.setText(editDialog.getEditTextString());
                    }
                });
                editDialog.setOnCancelClickListener(new BaseDialog.OnCancelClickListener() {
                    @Override
                    public void cancel() {
                        editDialog.dismiss();
                    }
                });
                editDialog.show(driverBean.getDriver_number());
                break;
            case R.id.iv_email_edit:
                editDialog = new EditDialog(this);
                editDialog.setCancelable(false);
                editDialog.setOkClickListener(new BaseDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        driverBean.setDriver_email(editDialog.getEditTextString());
                        repository.insertDriverBeans(driverBean);
                        editDialog.dismiss();
                        tv_num.setText(editDialog.getEditTextString());
                    }
                });
                editDialog.setOnCancelClickListener(new BaseDialog.OnCancelClickListener() {
                    @Override
                    public void cancel() {
                        editDialog.dismiss();
                    }
                });
                editDialog.show(driverBean.getDriver_email());
                break;
            case R.id.iv_call:
                okCancelDialog = new OKCancelDialog(this);
                okCancelDialog.setCancelable(false);
                okCancelDialog.setOkClickListener(new OKCancelDialog.OKClickListener() {
                    @Override
                    public void Ok() {
                        CallUtils.callPhone(DriverActivity.this,driverBean.getDriver_number());
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
                okCancelDialog.setTvTitle("Call "+driverBean.getDriver_number());
                break;
            case R.id.iv_email:
                CallUtils.sendMail(this,driverBean.getDriver_email());
                break;
        }
    }
}

