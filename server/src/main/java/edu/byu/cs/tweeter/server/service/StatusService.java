package edu.byu.cs.tweeter.server.service;

import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.StatusDAO;

public class StatusService {
    public FeedResponse getFeed(FeedRequest request){
        return getStatusDAO().getFeed(request);
    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    public StoryResponse getStory(StoryRequest request) {
        return getStatusDAO().getStory(request);
    }
}
