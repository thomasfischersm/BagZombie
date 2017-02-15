package com.playposse.heavybagzombie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.playposse.heavybagzombie.R;

/**
 * An {@link android.app.Activity} that is shown to the user after requesting the microphone
 * permission failed.
 */
public class MicrophonePermissionNeededActivity extends ParentActivity {

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_microphone_permission_needed;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
