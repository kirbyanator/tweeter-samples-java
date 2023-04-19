package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;

public interface DAOFactoryInterface {
    UserDAO getUserDAO();
    AuthTokenDAO getAuthTokenDAO();
    FollowDAO getFollowDAO();
    FeedDAO getFeedDAO();
    StoryDAO getStoryDAO();
}
