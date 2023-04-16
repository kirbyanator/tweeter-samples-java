package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.model.net.response.UserResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;

public class MyTests {

    private User user1;
    private User user2;
    private AuthToken token;

    @BeforeEach
    public void setUp(){
        user1 = new User("sdf", "sdf", "@sdf", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@sdf");
        user2 = new User("davis", "forster", "@davis", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis");
        UserService userService = new UserService();
        LoginRequest logReq = new LoginRequest("@davis", "davis");
        AuthenticationResponse logres = userService.login(logReq);
        token = logres.getAuthToken();
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
        UnfollowRequest unfollowRequest = new UnfollowRequest(token, user1.getAlias(), user2.getAlias());
        UnfollowResponse unresponse = service.unfollow(unfollowRequest);

        FollowRequest request = new FollowRequest(token, user1.getAlias(), user2.getAlias());
        FollowResponse response = service.follow(request);
        Assertions.assertTrue(response.isSuccess());

    }

    @Test
    public void GetFollowersTest(){
        FollowService followService = new FollowService();
        FollowersRequest request = new FollowersRequest(token, "@davis", 3, null);
        FollowersResponse response = followService.getFollowers(request);
        Assertions.assertTrue(response.isSuccess());
    }

    @Test
    public void GetFolloweesTest(){
        FollowService followService = new FollowService();
        FollowersRequest request = new FollowersRequest(token, "@davis", 3, null);
        FollowersResponse response = followService.getFollowers(request);
        Assertions.assertTrue(response.isSuccess());
    }
}
