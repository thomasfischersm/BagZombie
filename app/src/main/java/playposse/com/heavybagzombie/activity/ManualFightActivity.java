package playposse.com.heavybagzombie.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import playposse.com.heavybagzombie.R;

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
