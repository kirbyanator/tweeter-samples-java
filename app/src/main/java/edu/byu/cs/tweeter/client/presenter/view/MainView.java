package edu.byu.cs.tweeter.client.presenter.view;

public interface MainView extends View{
    void updateFollowButton(boolean isFollower);
    void updateSelectedUserFollowingAndFollowers();
    void enableFollowButton(boolean b);
    void logoutUser();
    void setFollowersCount(int count);
    void setFollowingCount(int count);
}
