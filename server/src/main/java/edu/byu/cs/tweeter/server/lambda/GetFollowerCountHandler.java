package edu.byu.cs.tweeter.server.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.response.CountResponse;
import edu.byu.cs.tweeter.server.service.FollowService;

public class GetFollowerCountHandler implements RequestHandler<FollowersCountRequest, CountResponse> {
    @Override
    public CountResponse handleRequest(FollowersCountRequest input, Context context) {
        FollowService service = new FollowService();
        return service.getFollowerCount(input);
    }
}
