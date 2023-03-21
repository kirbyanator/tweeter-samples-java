package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.observer.interfaces.SimpleObserver;

public class SimpleTaskHandler extends BackgroundTaskHandler<SimpleObserver>{

    public SimpleTaskHandler(SimpleObserver observer){
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(SimpleObserver observer, Bundle data) {
        observer.handleSuccess();
    }

}
