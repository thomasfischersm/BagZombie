package playposse.com.heavybagzombie.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import playposse.com.heavybagzombie.R;

/**
 * An abstract {@link android.app.Activity} that contains the boilerplate to instantiate the support
 * toolbar.
 */
public abstract class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
    }

    protected abstract int getLayoutResId();
}
