package com.playposse.heavybagzombie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.heavybagzombie.R;

public class SettingsActivity extends ParentActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
