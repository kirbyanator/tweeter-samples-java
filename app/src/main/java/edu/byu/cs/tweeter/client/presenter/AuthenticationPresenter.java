package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.BaseObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.UserObserver;
import edu.byu.cs.tweeter.client.presenter.view.AuthenticationView;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationPresenter extends Presenter<AuthenticationView>{

    protected UserService userService = new UserService();

    public abstract String getPresenterType();

    protected class AuthenticationObserver extends BaseObserver<AuthenticationPresenter> implements UserObserver {

        public AuthenticationObserver(AuthenticationPresenter presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(User registeredUser) {
            view.authenticationSuccess(registeredUser);
        }

        @Override
        public String getErrorMessage() {
            return getPresenterType();
        }
    }

}
