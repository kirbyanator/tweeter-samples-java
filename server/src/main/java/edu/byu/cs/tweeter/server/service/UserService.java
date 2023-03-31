package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    public LoginResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.
        User user = getDummyUser();
        AuthToken authToken = getDummyAuthToken();
        return new LoginResponse(user, authToken);
    }

    /**
     * Returns the dummy user to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy user.
     *
     * @return a dummy user.
     */
    User getDummyUser() {
        return getFakeData().getFirstUser();
    }

    /**
     * Returns the dummy auth token to be returned by the login operation.
     * This is written as a separate method to allow mocking of the dummy auth token.
     *
     * @return a dummy auth token.
     */
    AuthToken getDummyAuthToken() {
        return getFakeData().getAuthToken();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy users and auth tokens.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }

    public UserResponse getUser(UserRequest request) {
        if(request.getUserAliasStr() == null){
            throw new RuntimeException("[Bad Request] Missing user alias");
        }
        User user = getFakeData().findUserByAlias(request.getUserAliasStr());
        return new UserResponse(user);
    }

    public LogoutResponse logout(LogoutRequest input) {
        return new LogoutResponse();
    }

    public RegisterResponse register(RegisterRequest input) {
        if(input.getUsername() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a username");
        }
        if(input.getPassword() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a password");
        }
        if(input.getFirstName() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a first name");
        }
        if(input.getLastName() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a last name");
        }
        if(input.getImage() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an image");
        }
        User registeredUser = getFakeData().getFirstUser();
        AuthToken authToken = getFakeData().getAuthToken();
        return new RegisterResponse(registeredUser, authToken);
    }
}
