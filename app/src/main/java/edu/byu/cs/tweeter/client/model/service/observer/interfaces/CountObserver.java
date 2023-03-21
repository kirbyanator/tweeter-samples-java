package edu.byu.cs.tweeter.client.model.service.observer.interfaces;

public interface CountObserver extends ServiceObserver{
    void handleSuccess(int count);
}
