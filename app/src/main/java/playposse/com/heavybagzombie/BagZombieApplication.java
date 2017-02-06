package playposse.com.heavybagzombie;

import android.app.Application;
import android.content.Intent;

import playposse.com.heavybagzombie.service.FightEngineService;

/**
 * Created by thoma on 1/30/2017.
 */
public class BagZombieApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        startService(new Intent(getApplicationContext(), FightEngineService.class));
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        stopService(new Intent(getApplicationContext(), FightEngineService.class));
    }
}
