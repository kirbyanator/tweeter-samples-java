package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.CountObserver;

public class CountTaskHandler extends BackgroundTaskHandler<CountObserver>{
    public CountTaskHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(CountObserver observer, Bundle data) {
        int count = data.getInt(GetFollowingCountTask.COUNT_KEY);
        observer.handleSuccess(count);
    }
}
