package edu.byu.cs.tweeter.client.model.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.FollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.IsFollowerTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.UnfollowTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.CountTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.IsFollowerHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.BooleanObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.SimpleObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowService extends TaskExecutor {


    public void getFollowees(User user, int pageSize, User lastFollowee, PagedTaskObserver<User> observer) {
        GetFollowingTask getFollowingTask = new GetFollowingTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollowee, new PagedTaskHandler<>(observer));
        executeTask(getFollowingTask);
    }


    public void getFollowers(User user, int pageSize, User lastFollower, PagedTaskObserver<User> getFollowerObserver) {
        GetFollowersTask getFollowersTask = new GetFollowersTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastFollower, new PagedTaskHandler<>(getFollowerObserver));
        executeTask(getFollowersTask);
    }


    public void checkIsFollower(User user, BooleanObserver observer){
        IsFollowerTask isFollowerTask = new IsFollowerTask(Cache.getInstance().getCurrUserAuthToken(),
                Cache.getInstance().getCurrUser(), user, new IsFollowerHandler(observer));
        executeTask(isFollowerTask);
    }

    public void unfollowUser(User user, SimpleObserver observer){
        UnfollowTask unfollowTask = new UnfollowTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new SimpleTaskHandler(observer));
        executeTask(unfollowTask);
    }


    public void followUser(User user, SimpleObserver observer){
        FollowTask followTask = new FollowTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new SimpleTaskHandler(observer));
        executeTask(followTask);
    }


    public void getFollowerCount(User user, CountObserver observer) {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowersCountTask followersCountTask = new GetFollowersCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new CountTaskHandler(observer));
        executor.execute(followersCountTask);
    }


    public void getFollowingCount(User user, CountObserver observer){
        ExecutorService executor = Executors.newFixedThreadPool(2);
        GetFollowingCountTask followingCountTask = new GetFollowingCountTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new CountTaskHandler(observer));
        executor.execute(followingCountTask);
    }

}
