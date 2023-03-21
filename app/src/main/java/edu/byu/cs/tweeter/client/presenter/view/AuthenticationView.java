package edu.byu.cs.tweeter.client.presenter.view;

import edu.byu.cs.tweeter.model.domain.User;

public interface AuthenticationView extends View{

    public void prepAuthentication();
    public void authenticationSuccess(User user);

}
