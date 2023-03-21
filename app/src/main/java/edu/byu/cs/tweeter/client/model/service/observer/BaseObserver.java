package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.model.service.observer.interfaces.ServiceObserver;
import edu.byu.cs.tweeter.client.presenter.Presenter;
import edu.byu.cs.tweeter.client.presenter.view.View;

public abstract class BaseObserver<T extends Presenter<?>> implements ServiceObserver{


    protected T presenter;
    // protected Presenter<View> presenter;

    public BaseObserver(T presenter){
        this.presenter = presenter;
    }

    public void handleFailure(String message) {
        presenter.getView().displayMessage("Failed to " + getErrorMessage() + ": " + message);
        onFinish();
    }

    public void handleException(Exception exception) {
        presenter.getView().displayMessage("Failed to " + getErrorMessage() + " because of exception " + exception.getMessage());
        onFinish();
    }

    public void onFinish(){

    }


    public abstract String getErrorMessage();
}
