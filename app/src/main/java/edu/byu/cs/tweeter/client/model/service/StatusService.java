package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedTaskHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.SimpleTaskHandler;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.SimpleObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService extends TaskExecutor {


    public void getFeed(User user, int pageSize, Status lastStatus, PagedTaskObserver<Status> observer){
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedTaskHandler<>(observer));
        executeTask(getFeedTask);
    }


    public void getStory(User user, int pageSize, Status lastStatus, PagedTaskObserver<Status> observer) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, pageSize, lastStatus, new PagedTaskHandler<>(observer));
        executeTask(getStoryTask);
    }


    public void postStatus(Status status, SimpleObserver observer){
        PostStatusTask statusTask = new PostStatusTask(Cache.getInstance().getCurrUserAuthToken(),
                status, new SimpleTaskHandler(observer));
        executeTask(statusTask);
    }


}
