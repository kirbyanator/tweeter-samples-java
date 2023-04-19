package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.AuthenticationResponse;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
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

    @Test
    public void populateTables(){

        sdfUser = new User("sdf", "sdf", "@sdf", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@sdf");
        davisUser = new User("davis", "forster", "@davis", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis");

        UserService userService = new UserService();
        FollowService followService = new FollowService();
        UserDAO userDAO = new UserDAO();

        String hashedPassword = UserService.hashPassword("davis");

        UserBean davisBean = new UserBean(davisUser.getFirstName(), davisUser.getLastName(), davisUser.getAlias(), "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis", hashedPassword, 0, 0);
        userDAO.put(davisBean);
        UserBean sdfBean = new UserBean(sdfUser.getFirstName(), sdfUser.getLastName(), sdfUser.getAlias(), sdfUser.getImageUrl(), hashedPassword, 0, 0);
        userDAO.put(sdfBean);

        davisUser = new User("davis", "forster", "@davis", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis");
        LoginRequest loginRequest = new LoginRequest(davisUser.getAlias(), "davis");
        AuthenticationResponse regres = userService.login(loginRequest);
        token = regres.getAuthToken();


        for(int i = 0; i < 10; ++i){
            UserBean user = new UserBean("userfirst" + i, "userlast" + i, "@davisfollower" + i, "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis", hashedPassword, 0, 0);
            userDAO.put(user);
            FollowRequest frequest = new FollowRequest(token, user.getAlias(), davisUser.getAlias());
            followService.follow(frequest);
            frequest = new FollowRequest(token, user.getAlias(), sdfUser.getAlias());
            followService.follow(frequest);
        }

        for(int i = 0; i < 8; ++i){
            UserBean user = new UserBean("userfirst" + i, "userlast" + i, "@davisfollowee" + i, "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis", hashedPassword, 0, 0);
            userDAO.put(user);
            FollowRequest frequest = new FollowRequest(token, davisUser.getAlias(), user.getAlias());
            followService.follow(frequest);
            frequest = new FollowRequest(token, sdfUser.getAlias(), user.getAlias());
            followService.follow(frequest);
        }

        StatusService statusService = new StatusService();

        List<String> mentions = new ArrayList<>();
        mentions.add("@sdf");

        List<String> urls = new ArrayList<>();
        urls.add("instagram.com");

        for(int i = 0; i < 12; ++i) {
            Status myStatus = new Status("hee hoo @sdf instagram.com", davisUser, (long) 123 + (i * 100000000), urls, mentions);
            PostStatusRequest postRequest = new PostStatusRequest(token, myStatus);
            PostStatusResponse response = statusService.postStatus(postRequest);
        }

        List<String> mentions2 = new ArrayList<>();
        mentions2.add("@davis");

        List<String> urls2 = new ArrayList<>();
        urls2.add("instagram.com");

        for(int i = 0; i < 25; ++i) {
            Status myStatus = new Status("hee hoo @davis instagram.com", sdfUser, (long) (123 + (i * 100000000)) + 10000, urls2, mentions2);
            PostStatusRequest postRequest = new PostStatusRequest(token, myStatus);
            PostStatusResponse response = statusService.postStatus(postRequest);
        }

    }

    @BeforeEach
    public void setup(){
        davisUser = new User("davis", "forster", "@davis", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@davis");
        UserService userService = new UserService();
        LoginRequest loginRequest = new LoginRequest(davisUser.getAlias(), "davis");
        AuthenticationResponse regres = userService.login(loginRequest);
        token = regres.getAuthToken();
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

    @Test
    public void IsFollowerTest(){
        populateTables();
        FollowService service = new FollowService();
        IsFollowerRequest req = new IsFollowerRequest(token, "@davisfollower1", "@davis");
        IsFollowerResponse res = service.isFollower(req);
        Assertions.assertTrue(res.getIsFollower());
    }

    @Test
    public void statusTests(){
        //populateTables();

        System.out.println("test 1");
        StatusService statusService = new StatusService();
        FeedRequest feedReq = new FeedRequest(token, "@davisfollower10", 10, null);
        FeedResponse feedRes = statusService.getFeed(feedReq);
        Assertions.assertTrue(feedRes.isSuccess());

        System.out.println("test 2");

        StoryRequest storyReq = new StoryRequest(token, "@davis", 10, null);
        StoryResponse storyRes = statusService.getStory(storyReq);
        Assertions.assertTrue(storyRes.isSuccess());

        System.out.println("test 3");
        StoryRequest req2 = new StoryRequest(token, "@davis", 10, storyRes.getItems().get(9));
        storyRes = statusService.getStory(req2);
        System.out.println(storyRes.getMessage());
        Assertions.assertTrue(storyRes.isSuccess());

    }

}
