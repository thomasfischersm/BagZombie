package com.playposse.heavybagzombie.service;

/**
 * An interface to call back to the {@link FightEngineService} to remove the service from the
 * foreground when the fight has ended.
 */
public interface ForegroundService {

    void stopForeground(boolean removeNotification);
}
