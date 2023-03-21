package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.util.Random;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.IsFollowerRequest;
import edu.byu.cs.tweeter.model.net.response.IsFollowerResponse;

/**
 * Background task that determines if one user is following another.
 */
public class IsFollowerTask extends AuthenticatedTask {

    private static final String LOG_TAG = "IsFollowerTask";
    private static final String URL_PATH = "/isfollower";

    public static final String IS_FOLLOWER_KEY = "is-follower";

    /**
     * The alleged follower.
     */
    private final User follower;

    /**
     * The alleged followee.
     */
    private final User followee;

    private boolean isFollower;

    public IsFollowerTask(AuthToken authToken, User follower, User followee, Handler messageHandler) {
        super(authToken, messageHandler);
        this.follower = follower;
        this.followee = followee;
    }

    @Override
    protected void runTask() {
        try {
            System.out.println("calling isfollower api");
            IsFollowerRequest request = new IsFollowerRequest(authToken, follower.getAlias(), followee.getAlias());
            IsFollowerResponse result = getServerFacade().isFollower(request, URL_PATH);
            if(result.isSuccess()){
                System.out.println("isfollower api success!");
                isFollower = result.getIsFollower();
                sendSuccessMessage();
            }
            else{
                sendFailedMessage(result.getMessage());
            }
        }
        catch(Exception ex){
            sendExceptionMessage(ex);
        }

        // Call sendSuccessMessage if successful
        // sendSuccessMessage();
        // or call sendFailedMessage if not successful
        // sendFailedMessage()
    }

    @Override
    protected void loadSuccessBundle(Bundle msgBundle) {
        msgBundle.putBoolean(IS_FOLLOWER_KEY, isFollower);
    }
}
