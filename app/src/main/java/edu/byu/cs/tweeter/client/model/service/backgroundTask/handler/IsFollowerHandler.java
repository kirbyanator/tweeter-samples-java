package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.BooleanObserver;

public class IsFollowerHandler extends BackgroundTaskHandler<BooleanObserver> {


    public IsFollowerHandler(BooleanObserver observer) {
        super(observer);
    }


    @Override
    protected void handleSuccessMessage(BooleanObserver observer, Bundle data) {
        boolean isFollower = data.getBoolean(IsFollowerTask.IS_FOLLOWER_KEY);

        // If logged in user if a follower of the selected user, display the follow button as "following"
        observer.handleSuccess(isFollower);
    }
}
