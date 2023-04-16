package edu.byu.cs.tweeter.server.dao.interfaces;

import edu.byu.cs.tweeter.server.dao.beans.FollowBean;

public interface FollowDAOInterface {
    void put(FollowBean item);
    FollowBean get(String follower_handle, String followee_handle);
    void remove(String follower_handle, String followee_handle);
    void update(FollowBean item);
}
