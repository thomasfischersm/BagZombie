package playposse.com.heavybagzombie.util;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * A simple {@link View.OnClickListener} that opens a new {@link android.app.Activity}.
 */
public class ClickListenerToOpenActivity implements View.OnClickListener {

    private final Class<?> targetActivityClass;

    public ClickListenerToOpenActivity(Class<?> targetActivityClass) {
        this.targetActivityClass = targetActivityClass;
    }

    @Override
    public void onClick(View view) {
        Context context = view.getContext();
        Intent intent = new Intent(context, targetActivityClass);
        context.startActivity(intent);
    }
}
