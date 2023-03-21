package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.UserRequest;
import edu.byu.cs.tweeter.model.net.response.UserResponse;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends AuthenticatedTask {

    private static final String LOG_TAG = "GetUserTask";
    private static final String URL_PATH = "/getuser";

    public static final String USER_KEY = "user";

    /**
     * Alias (or handle) for user whose profile is being retrieved.
     */
    private final String alias;

    private User user;

    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(authToken, messageHandler);
        this.alias = alias;
    }

    @Override
    protected void runTask() {
        try {
            System.out.println("calling getuser api");
            UserResponse response = getUser();
            if(response.isSuccess()){
                System.out.println("getuser api success!");
                user = response.getUser();
                sendSuccessMessage();
            }
            else{
                sendFailedMessage(response.getMessage());
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
        msgBundle.putSerializable(USER_KEY, user);
    }

    private UserResponse getUser() throws IOException, TweeterRemoteException {
        UserRequest request = new UserRequest(authToken, alias);
        UserResponse response = getServerFacade().getUser(request, URL_PATH);
        return response;
        //return getFakeData().findUserByAlias(alias);
    }
}
