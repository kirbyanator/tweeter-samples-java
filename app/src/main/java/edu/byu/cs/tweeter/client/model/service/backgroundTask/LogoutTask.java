package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.net.request.LogoutRequest;
import edu.byu.cs.tweeter.model.net.response.LogoutResponse;

/**
 * Background task that logs out a user (i.e., ends a session).
 */
public class LogoutTask extends AuthenticatedTask {

    private static final String LOG_TAG = "LogoutTask";
    private static final String URL_PATH = "/logout";

    public LogoutTask(AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
    }

    @Override
    protected void runTask() {
        // We could do this from the presenter, without a task and handler, but we will
        // eventually remove the auth token from  the DB and will need this then.
        try{
            System.out.println("pinging logout api");
            LogoutRequest request = new LogoutRequest(authToken);
            LogoutResponse response = getServerFacade().logout(request, URL_PATH);
            if(response.isSuccess()){
                sendSuccessMessage();
            }
            else{
                sendFailedMessage(response.getMessage());
            }
        }
        catch (Exception ex){
            sendExceptionMessage(ex);
        }
        // Call sendSuccessMessage if successful
        // or call sendFailedMessage if not successful
        // sendFailedMessage()
    }
}
