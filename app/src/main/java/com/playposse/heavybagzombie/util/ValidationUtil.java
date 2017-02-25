package com.playposse.heavybagzombie.util;

import android.widget.EditText;

import com.playposse.heavybagzombie.R;

/**
 * A utility class for user input validation.
 */
public class ValidationUtil {

    public static boolean validateRequired(EditText editText) {
        String str = editText.getText().toString().trim();
        if (str.isEmpty()) {
            String requiredMessage =
                    editText.getContext().getString(R.string.required_validation_message);
            editText.setError(requiredMessage);
            return false;
        } else {
            return true;
        }
    }
}
