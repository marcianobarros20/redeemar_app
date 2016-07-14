package com.tier5.redeemar.RedeemarConsumerApp.async;

/**
 * Created by tier5 on 8/6/16.
 */
public interface TaskCompleted {
    // Define data you like to return from AysncTask
    public void onTaskComplete(String result);
}