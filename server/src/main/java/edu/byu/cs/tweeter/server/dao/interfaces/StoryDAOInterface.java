package edu.byu.cs.tweeter.server.dao.interfaces;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.util.Pair;

public interface StoryDAOInterface {
    Pair<List<StoryBean>,Boolean> getStory(int pageLimit, String targetUser, StoryBean lastStatus);
    void postStatus(StoryBean status);
    boolean update(StoryBean status);
}
