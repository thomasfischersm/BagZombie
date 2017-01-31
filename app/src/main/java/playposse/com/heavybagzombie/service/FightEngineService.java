package playposse.com.heavybagzombie.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * A {@link android.app.Service} that records audio to detect when the heavy bag is hit and
 * simulates the fight. It can be connected to from the app and the app status.
 */
public class FightEngineService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
