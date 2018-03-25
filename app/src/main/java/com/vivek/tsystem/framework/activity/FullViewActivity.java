package com.vivek.tsystem.framework.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vivek.tsystem.R;
import com.vivek.tsystem.common.value.Constants;

/**
 * Created by vivek on 25/03/18.
 */

public class FullViewActivity extends BaseActivity {

    private ImageView imageView;
    private float x1,x2;
    private static final int MIN_DISTANCE = 150;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_full_view;
    }

    @Override
    protected void initializeViews(Bundle bundle) {
        imageView = findViewById(R.id.imageView);

        String url = bundle.getString(Constants.Bundle.URL);
        if(TextUtils.isEmpty(url)) {
            finish();
            return;
        }
        Glide.with(this).load(url).into(imageView);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;

                if (Math.abs(deltaX) > MIN_DISTANCE)
                {
                    // Left to Right swipe action
                    if (x2 > x1)
                    {
                        super.onBackPressed();
                    }

                    // Right to left swipe action
                    else
                    {
                        super.onBackPressed();
                    }

                }
                break;
        }
        return super.onTouchEvent(event);
    }
}
