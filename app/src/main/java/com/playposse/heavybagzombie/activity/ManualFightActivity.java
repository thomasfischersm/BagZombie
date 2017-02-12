package com.playposse.heavybagzombie.activity;

import android.os.Bundle;

import com.playposse.heavybagzombie.R;

public class ManualFightActivity extends ParentActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_manual_fight;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
