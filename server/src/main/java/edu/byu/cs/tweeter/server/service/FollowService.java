package edu.byu.cs.tweeter.server.service;

import java.util.Random;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FollowRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.request.UnfollowRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.model.net.response.FollowResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;
import edu.byu.cs.tweeter.model.net.response.UnfollowResponse;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;

/**
 * Contains the business logic for getting the users a user is following.
 */
public class FollowService extends Service{

    /**
     * Returns the users that the user specified in the request is following. Uses information in
     * the request object to limit the number of followees returned and to return the next set of
     * followees after any that were returned in a previous request. Uses the {@link FollowDAO} to
     * get the followees.
     *
     * @param request contains the data required to fulfill the request.
     * @return the followees.
     */
    public FollowingResponse getFollowees(FollowingRequest request) {
        if(request.getFollowerAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowees(request);
    }

    public FollowResponse follow(FollowRequest request){
        if(request.getFolloweeAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        if(request.getFollowerAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }

        FollowDAO followDAO = factory.getFollowDAO();
        UserDAO userDAO = factory.getUserDAO();

        try{
            authenticateToken(request.getAuthToken());

            UserBean follower = userDAO.get(request.getFollowerAlias());
            UserBean followee = userDAO.get(request.getFolloweeAlias());

            if(followDAO.get(follower.getAlias(), followee.getAlias()) != null){
                return new FollowResponse("You are already following this user");
            }

            FollowBean newRelationship = new FollowBean(follower.getAlias(), follower.getFirstName() + " " + follower.getLastName(),
                    followee.getAlias(), followee.getFirstName() + " " + followee.getLastName());

            followDAO.put(newRelationship);

            follower.setFolloweeCount(follower.getFolloweeCount() + 1);
            followee.setFollowerCount(followee.getFollowerCount() + 1);

            userDAO.update(follower);
            userDAO.update(followee);
        }
        catch(Exception e){
            return new FollowResponse(e.getMessage());
        }

        return new FollowResponse();
    }

    /**
     * Returns an instance of {@link FollowDAO}. Allows mocking of the FollowDAO class
     * for testing purposes. All usages of FollowDAO should get their FollowDAO
     * instance from this method to allow for mocking of the instance.
     *
     * @return the instance.
     */
    FollowDAO getFollowingDAO() {
        return new FollowDAO();
    }

    public CountResponse getFollowerCount(FollowersCountRequest request) {
        if(request.getTargetUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs a user alias");
        }
        UserDAO userDAO = factory.getUserDAO();

        try {
            authenticateToken(request.getAuthToken());
            UserBean user = userDAO.get(request.getTargetUserAlias());
            return new CountResponse(user.getFollowerCount());
        }
        catch(Exception e){
            return new CountResponse(e.getMessage());
        }
    }

    public CountResponse getFollowingCount(FollowingCountRequest request) {
        if(request.getTargetUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs a user alias");
        }
        UserDAO userDAO = factory.getUserDAO();

        try {
            authenticateToken(request.getAuthToken());
            UserBean user = userDAO.get(request.getTargetUserAlias());
            return new CountResponse(user.getFollowerCount());
        }
        catch(Exception e){
            return new CountResponse(e.getMessage());
        }
    }

    public FollowersResponse getFollowers(FollowersRequest request) {
        if(request.getFolloweeAlias() == null) {
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        } else if(request.getLimit() <= 0) {
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getFollowingDAO().getFollowers(request);
    }

    public IsFollowerResponse isFollower(IsFollowerRequest request) {
        if(request.getFolloweeAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        if(request.getFollowerAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a follower alias");
        }
        return new IsFollowerResponse(new Random().nextInt() > 0);
    }

    public UnfollowResponse unfollow(UnfollowRequest request) {
        if(request.getFolloweeAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a followee alias");
        }
        FollowDAO followDAO = factory.getFollowDAO();
        UserDAO userDAO = factory.getUserDAO();

        try{
            authenticateToken(request.getAuthToken());

            UserBean follower = userDAO.get(request.getFollowerAlias());
            UserBean followee = userDAO.get(request.getFolloweeAlias());

            if(followDAO.get(follower.getAlias(), followee.getAlias()) == null){
                return new UnfollowResponse("You are already not following this user");
            }

            followDAO.remove(follower.getAlias(), followee.getAlias());

            follower.setFolloweeCount(follower.getFolloweeCount() - 1);
            followee.setFollowerCount(followee.getFollowerCount() - 1);

            userDAO.update(follower);
            userDAO.update(followee);

        }
        catch(Exception e){
            return new UnfollowResponse(e.getMessage());
        }
        return new UnfollowResponse();
    }
}
