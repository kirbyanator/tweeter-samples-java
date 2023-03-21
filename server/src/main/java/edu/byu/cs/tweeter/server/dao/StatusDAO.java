package edu.byu.cs.tweeter.server.dao;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.FakeData;

public class StatusDAO {

    public FeedResponse getFeed(FeedRequest request){
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatus = getDummyStatus();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatus != null) {
                int statusIndex = getStatusStartingIndex(request.getLastStatus(), allStatus);

                for(int limitCounter = 0; statusIndex < allStatus.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatuses.add(allStatus.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatus.size();
            }
        }

        return new FeedResponse(responseStatuses, hasMorePages);
    }

    private List<Status> getDummyStatus() {
        return getFakeData().getFakeStatuses();
    }

    private FakeData getFakeData() {
        return FakeData.getInstance();
    }

    private int getStatusStartingIndex(Status lastStatus, List<Status> allStatus) {
        int statusIndex = 0;
        if(lastStatus != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allStatus.size(); i++) {
                if(lastStatus.equals(allStatus.get(i))) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    statusIndex = i + 1;
                    break;
                }
            }
        }
        return statusIndex;
    }

    public StoryResponse getStory(StoryRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;

        List<Status> allStatus = getDummyStatus();
        List<Status> responseStatuses = new ArrayList<>(request.getLimit());

        boolean hasMorePages = false;

        if(request.getLimit() > 0) {
            if (allStatus != null) {
                int statusIndex = getStatusStartingIndex(request.getLastStatus(), allStatus);

                for(int limitCounter = 0; statusIndex < allStatus.size() && limitCounter < request.getLimit(); statusIndex++, limitCounter++) {
                    responseStatuses.add(allStatus.get(statusIndex));
                }

                hasMorePages = statusIndex < allStatus.size();
            }
        }

        return new StoryResponse(responseStatuses, hasMorePages);
    }
}
