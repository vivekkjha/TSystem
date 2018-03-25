package com.vivek.tsystem.framework.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;

import com.vivek.tsystem.R;


/**
 * Created by vivek on 06/12/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    protected abstract int getLayoutId();

    protected abstract void initializeViews(Bundle bundle);


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.base_content_frame);
        FrameLayout contentLayout =  findViewById(R.id.content_detail);
        int id = getLayoutId();
        View contentView = getLayoutInflater().inflate(id, null);
        contentLayout.addView(contentView);
        initializeViews(getIntent().getExtras());

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
    public void showProgressLoader() {
        showProgressLoader(null);
    }

    public void showProgressLoader(String text) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage(TextUtils.isEmpty(text)?getString(R.string.progress_dialog_msg):text);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideProgressLoader() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

}
