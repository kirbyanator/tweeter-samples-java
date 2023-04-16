package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;

public class MyTests {

    private User sdfUser;
    private User davisUser;
    private User momUser;
    private User dadUser;
    private User averyUser;


    private AuthToken token;

    public void populateTables(){

        sdfUser = new User("sdf", "sdf", "@sdf", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@sdf");
        davisUser = new User("davis", "forster", "@davis", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis");

        UserService userService = new UserService();
        FollowService followService = new FollowService();
        UserDAO userDAO = new UserDAO();

        String hashedPassword = UserService.hashPassword("davis");

        UserBean davisBean = new UserBean(davisUser.getFirstName(), davisUser.getLastName(), davisUser.getAlias(), "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis", hashedPassword, 0, 0);
        userDAO.put(davisBean);

        LoginRequest loginRequest = new LoginRequest(davisUser.getAlias(), "davis");
        AuthenticationResponse regres = userService.login(loginRequest);
        token = regres.getAuthToken();

        for(int i = 0; i < 20; ++i){
            UserBean user = new UserBean("userfirst" + i, "userlast" + i, "@follower" + i, "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis", "davis", 0, 0);
            userDAO.put(user);
            FollowRequest frequest = new FollowRequest(token, user.getAlias(), davisUser.getAlias());
            followService.follow(frequest);
        }

        for(int i = 0; i < 15; ++i){
            UserBean user = new UserBean("userfirst" + i, "userlast" + i, "@followee" + i, "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis", "davis", 0, 0);
            userDAO.put(user);
            FollowRequest frequest = new FollowRequest(token, davisUser.getAlias(), user.getAlias());
            followService.follow(frequest);
        }
    }

    @BeforeEach
    public void setup(){

    }

    @Test
    public void GetUserTest(){
        UserService testService = new UserService();
        User user = new User("sdf", "sdf", "@sdf", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@sdf");
        UserRequest req = new UserRequest(token, user.getAlias());

        UserResponse res = testService.getUser(req);
        Assertions.assertTrue(res.isSuccess());
    }

    @Test
    public void FollowTest(){
        FollowService service = new FollowService();
        UnfollowRequest unfollowRequest = new UnfollowRequest(token, sdfUser.getAlias(), davisUser.getAlias());
        UnfollowResponse unresponse = service.unfollow(unfollowRequest);

        FollowRequest request = new FollowRequest(token, sdfUser.getAlias(), davisUser.getAlias());
        FollowResponse response = service.follow(request);
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    public void GetFollowersTest(){
        populateTables();
        FollowService followService = new FollowService();
        FollowersRequest request = new FollowersRequest(token, "@davis", 10, null);
        FollowersResponse response = followService.getFollowers(request);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    public void GetFolloweesTest(){
        populateTables();
        FollowService followService = new FollowService();
        FollowersRequest request = new FollowersRequest(token, "@davis", 10, null);
        FollowersResponse response = followService.getFollowers(request);
        Assertions.assertTrue(response.isSuccess());
    }
}
