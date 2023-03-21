package edu.byu.cs.tweeter.client.model.service.observer.interfaces;

public interface BooleanObserver extends ServiceObserver{
    void handleSuccess(boolean result);
}
