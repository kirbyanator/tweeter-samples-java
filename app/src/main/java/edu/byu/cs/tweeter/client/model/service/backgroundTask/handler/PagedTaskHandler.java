package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PagedTask;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.PagedTaskObserver;

public class PagedTaskHandler<T> extends BackgroundTaskHandler<PagedTaskObserver<T>> {
    public PagedTaskHandler(PagedTaskObserver<T> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedTaskObserver<T> observer, Bundle data) {
        List<T> items = (List<T>) data.getSerializable(GetFollowingTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(PagedTask.MORE_PAGES_KEY);
        observer.handleSuccess(items, hasMorePages);
    }
}
