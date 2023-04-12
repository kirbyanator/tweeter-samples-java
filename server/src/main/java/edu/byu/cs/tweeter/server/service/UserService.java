package edu.byu.cs.tweeter.server.service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.beans.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import edu.byu.cs.tweeter.util.FakeData;

public class UserService {

    private final DAOFactory factory = new DAOFactory();
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    public AuthenticationResponse login(LoginRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Missing a username");
        } else if(request.getPassword() == null) {
            throw new RuntimeException("[Bad Request] Missing a password");
        }

        // TODO: Generates dummy data. Replace with a real implementation.

        try{
            UserDAO userDAO = factory.getUserDAO();
            AuthTokenDAO authTokenDAO = factory.getAuthTokenDAO();

            //check password
            UserBean retrievedUser = userDAO.get(request.getUsername());
            if(retrievedUser == null){
                System.out.println("AAAAAAAAAAH I FARTED");
                return new AuthenticationResponse("Invalid user/incorrect password");
            }
            String correctPassword = retrievedUser.getPassword();
            String enteredPassword = hashPassword(request.getPassword());

            System.out.println("comparing " + enteredPassword + " to " + correctPassword);

            if(correctPassword.equals(enteredPassword)){
                System.out.println("yay! password correct");
                User user = new User(retrievedUser.getFirstName(), retrievedUser.getLastName(), retrievedUser.getAlias(), retrievedUser.getImageUrl());
                AuthToken authToken = new AuthToken(generateNewToken(),System.currentTimeMillis());
                AuthTokenBean tokenEntry = new AuthTokenBean(request.getUsername(),authToken.token ,authToken.getTimestamp());
                authTokenDAO.put(tokenEntry);

                System.out.println("success response");
                return new AuthenticationResponse(user, authToken);
            }
            else{
                System.out.println("bad password response");
                return new AuthenticationResponse("Invalid user/incorrect password");
            }

        }
        catch(Exception e){
            System.out.println("exception response");
            System.out.println(e);
            return new AuthenticationResponse(e.getMessage());
        }

//        User user = getDummyUser();
//        AuthToken authToken = getDummyAuthToken();

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

    public LogoutResponse logout(LogoutRequest request) {
        return new LogoutResponse();
    }

    public AuthenticationResponse register(RegisterRequest request) {
        if(request.getUsername() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a username");
        }
        if(request.getPassword() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a password");
        }
        if(request.getFirstName() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a first name");
        }
        if(request.getLastName() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a last name");
        }
        if(request.getImage() == null){
            throw new RuntimeException("[Bad Request] Request needs to have an image");
        }

        UserDAO userDAO = factory.getUserDAO();
        AuthTokenDAO authDAO = factory.getAuthTokenDAO();

        String hashedPassword = hashPassword(request.getPassword());


        try{
            String imageLink = userDAO.uploadImage(request.getImage(), request.getUsername());

            UserBean userEntry = new UserBean(request.getFirstName(), request.getLastName(), request.getUsername(), imageLink, hashedPassword, 0, 0);
            User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageLink);
            AuthToken authToken = new AuthToken(generateNewToken(),System.currentTimeMillis());

            userDAO.put(userEntry);
            AuthTokenBean tokenEntry = new AuthTokenBean(request.getUsername(),authToken.token ,authToken.getTimestamp());
            authDAO.put(tokenEntry);
            return new AuthenticationResponse(registeredUser, authToken);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return new AuthenticationResponse(e.getMessage());
        }

        //User registeredUser = getFakeData().getFirstUser();
        //AuthToken authToken = getFakeData().getAuthToken();

    }

    private static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }
}
