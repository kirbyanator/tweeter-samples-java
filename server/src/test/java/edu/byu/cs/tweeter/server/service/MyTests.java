package edu.byu.cs.tweeter.server.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

public class MyTests {
    @Test
    public void GetUserTest(){
        UserService testService = new UserService();
        User user = new User("sdf", "sdf", "@sdf", "https://davisdatabucket.s3.us-east-2.amazonaws.com/@sdf");
        AuthToken token = new AuthToken("9-dVZdfOC-q7y4r7TkRk8k02Ll4f08L6", System.currentTimeMillis());
        UserRequest req = new UserRequest(token, user.getAlias());

        UserResponse res = testService.getUser(req);
        Assertions.assertTrue(res.isSuccess());
    }
}
