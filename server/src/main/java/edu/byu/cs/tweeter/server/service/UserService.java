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

public class UserService extends Service{

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
                return new AuthenticationResponse("Invalid user/incorrect password");
            }
            String correctPassword = retrievedUser.getPassword();
            String enteredPassword = hashPassword(request.getPassword());

            if(correctPassword.equals(enteredPassword)){
                User user = new User(retrievedUser.getFirstName(), retrievedUser.getLastName(), retrievedUser.getAlias(), retrievedUser.getImageUrl());
                AuthToken authToken = new AuthToken(generateNewToken(),System.currentTimeMillis());
                AuthTokenBean tokenEntry = new AuthTokenBean(request.getUsername(),authToken.token ,authToken.getTimestamp());
                authTokenDAO.put(tokenEntry);

                return new AuthenticationResponse(user, authToken);
            }
            else{
                return new AuthenticationResponse("Invalid user/incorrect password");
            }

        }
        catch(Exception e){
            return new AuthenticationResponse(e.getMessage());
        }

//        User user = getDummyUser();
//        AuthToken authToken = getDummyAuthToken();

    }


    public UserResponse getUser(UserRequest request) {
        if(request.getUserAliasStr() == null){
            throw new RuntimeException("[Bad Request] Missing user alias");
        }
        //User user = getFakeData().findUserByAlias(request.getUserAliasStr());
        UserDAO userDAO = factory.getUserDAO();

        try {
            authenticateToken(request.getAuthToken());
            UserBean userEntry = userDAO.get(request.getUserAliasStr());
            User user = new User(userEntry.getFirstName(), userEntry.getLastName(), userEntry.getAlias(), userEntry.getImageUrl());
            return new UserResponse(user);
        }
        catch(Exception e){
            return new UserResponse(e.getMessage());
        }

    }

    public LogoutResponse logout(LogoutRequest request) {
        try{
            AuthTokenDAO authTokenDAO = factory.getAuthTokenDAO();
            AuthToken oldToken = request.getAuthToken();
            authTokenDAO.remove(oldToken.getToken());
        }
        catch(Exception e){
            return new LogoutResponse(e.getMessage());
        }
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

            User registeredUser = new User(request.getFirstName(), request.getLastName(), request.getUsername(), imageLink);
            AuthToken authToken = new AuthToken(generateNewToken(),System.currentTimeMillis());

            UserBean userEntry = new UserBean(request.getFirstName(), request.getLastName(), request.getUsername(), imageLink, hashedPassword, 0, 0);
            if(userDAO.get(request.getUsername()) != null){
                return new AuthenticationResponse("User already registered!");
            }
            userDAO.put(userEntry);

            AuthTokenBean tokenEntry = new AuthTokenBean(request.getUsername(),authToken.token ,authToken.getTimestamp());
            authDAO.put(tokenEntry);

            return new AuthenticationResponse(registeredUser, authToken);
        }
        catch(Exception e){
            return new AuthenticationResponse(e.getMessage());
        }

        //User registeredUser = getFakeData().getFirstUser();
        //AuthToken authToken = getFakeData().getAuthToken();

    }

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    public static String hashPassword(String passwordToHash) {
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
