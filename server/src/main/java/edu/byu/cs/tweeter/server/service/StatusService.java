package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService extends Service {
    public FeedResponse getFeed(FeedRequest request){
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        }
        if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStatusDAO().getFeed(request);
    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        }
        if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        return getStatusDAO().getStory(request);
    }

    public PostStatusResponse postStatus(PostStatusRequest input) {
        if(input.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }
        return new PostStatusResponse();
    }
}
