package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.presenter.view.AuthenticationView;

public class LoginPresenter extends AuthenticationPresenter{


    public LoginPresenter(AuthenticationView view){
        this.view = view;
        this.userService = new UserService();
    }

    public void logIn(String aliasString, String passwordString) {
        validateLogin(aliasString, passwordString);
        view.prepAuthentication();
        userService.loginUser(aliasString, passwordString, new AuthenticationObserver(this));
    }

    public void validateLogin(String aliasString, String passwordString) {
        if (aliasString.length() > 0 && aliasString.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (aliasString.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (passwordString.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
    }

    @Override
    public String getPresenterType() {
        return "login";
    }


}
