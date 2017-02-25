package com.playposse.heavybagzombie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.heavybagzombie.R;

/**
 * An informative {@link android.app.Activity} that tells the user about the app.
 */
public class AboutActivity extends ParentActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
