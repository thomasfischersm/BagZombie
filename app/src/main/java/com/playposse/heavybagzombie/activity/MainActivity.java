package com.playposse.heavybagzombie.activity;

import android.os.Bundle;
import android.widget.Button;

import com.playposse.heavybagzombie.R;
import com.playposse.heavybagzombie.util.ClickListenerToOpenActivity;

public class MainActivity extends ParentActivity {

    private Button calibrateMicrophoneButton;
    private Button manualFightButton;


    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        calibrateMicrophoneButton = (Button) findViewById(R.id.calibrateMicrophoneButton);
        manualFightButton = (Button) findViewById(R.id.manualFightButton);

        calibrateMicrophoneButton.setOnClickListener(new ClickListenerToOpenActivity(
                MicrophoneCalibrationActivity.class));
        manualFightButton.setOnClickListener(new ClickListenerToOpenActivity(
                ManualFightActivity.class));
    }
}
