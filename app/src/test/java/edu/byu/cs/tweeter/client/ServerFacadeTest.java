package edu.byu.cs.tweeter.client;

import android.util.Log;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.RegisterRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.RegisterResponse;

public class ServerFacadeTest {

    private ServerFacade serverFacade;

    @BeforeEach
    public void setup(){
        serverFacade = new ServerFacade();
    }

    @Test
    public void registerTest(){
        try {
            RegisterRequest request = new RegisterRequest("@allen", "bangus", "first", "last", "animage");
            RegisterResponse response = serverFacade.register(request, "/register");
            assert response.isSuccess();
            Assertions.assertEquals(response.getUser().getAlias(), request.getUsername());
        }
        catch(Exception ex){
            assert false;
        }
    }

    @Test
    public void getFollowersTest(){
        try{
            FollowersRequest request = new FollowersRequest(null, "@allen", 10, null);
            FollowersResponse response = serverFacade.getFollowers(request,"/getfollowers");
            assert response.isSuccess();
            assert request.getLimit() == response.getFollowers().size();

        }catch (Exception ex){
            assert false;
        }
    }

    @Test
    public void followersCountTest(){
        try {
            FollowersCountRequest request = new FollowersCountRequest(null, "@allen");
            CountResponse response = serverFacade.getFollowersCount(request,"/getfollowerscount");
            assert response.isSuccess();
            assert response.getCount() == 20;

        }catch (Exception ex){
            assert false;
        }
    }

}
