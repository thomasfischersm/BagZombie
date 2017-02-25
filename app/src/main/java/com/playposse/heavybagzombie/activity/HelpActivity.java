package com.playposse.heavybagzombie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.heavybagzombie.R;

/**
 * An {@link android.app.Activity} that shows the user help.
 */
public class HelpActivity extends ParentActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_help;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
