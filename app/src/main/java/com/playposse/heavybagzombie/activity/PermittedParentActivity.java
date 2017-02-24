package com.playposse.heavybagzombie.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * A {@link android.app.Activity} that requests permission to access the microphone if it hasn't
 * been granted yet.
 */
public abstract class PermittedParentActivity extends ParentActivity {

    private static final String LOG_CAT = PermittedParentActivity.class.getSimpleName();

    private static final int PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onResume() {
        super.onResume();

        checkMicrophonePermission();
    }

    protected boolean checkMicrophonePermission() {
        int permissionCheck =
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Log.i(LOG_CAT, "Got microphone permissions.");
            return true;
        }

        // Need to request permissions.
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                PERMISSION_REQUEST_CODE);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        if ((requestCode == PERMISSION_REQUEST_CODE)
            && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            onMicrophonePermissionHasBeenGranted();
        } else if ((requestCode == PERMISSION_REQUEST_CODE)
                && (grantResults.length > 0)
                && (grantResults[0] == PackageManager.PERMISSION_DENIED)) {
            startActivity(new Intent(this, MicrophonePermissionNeededActivity.class));
        }
    }

    protected abstract void onMicrophonePermissionHasBeenGranted();
}
