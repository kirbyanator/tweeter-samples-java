package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.util.Pair;

public interface FeedDAOInterface {
    Pair<List<FeedBean>, Boolean> getFeed(int pageLimit, String targetUser, FeedBean lastStatus);
    void addStatus(FeedBean feedRow);
}
