package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;
import android.util.Log;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's story.
 */
public class GetStoryTask extends PagedStatusTask {

    private static final String LOG_TAG = "GetStoryTask";
    private static final String URL_PATH = "/getstory";

    public GetStoryTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                        Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected Pair<List<Status>, Boolean> getItems() {
        String targetUserAlias = getTargetUser() == null ? null : getTargetUser().getAlias();
        Status lastStatus = getLastItem() == null ? null:getLastItem();

        try {
            System.out.println("Pinging getstory api");
            StoryRequest request = new StoryRequest(authToken, targetUserAlias, getLimit(), lastStatus);
            StoryResponse response = getServerFacade().getStory(request, URL_PATH);
            if(response.isSuccess()){
                System.out.println("getstory api ping success!");
                return new Pair<>(response.getItems(),response.getHasMorePages());
            }
            else{
                sendFailedMessage(response.getMessage());
            }
        }
        catch(Exception ex){
            Log.e(LOG_TAG, "Failed to get story", ex);
            sendExceptionMessage(ex);
        }

        return null;
        //return getFakeData().getPageOfStatus(getLastItem(), getLimit());
    }
}
