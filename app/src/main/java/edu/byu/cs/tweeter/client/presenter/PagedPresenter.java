package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.BaseObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.PagedTaskObserver;
import edu.byu.cs.tweeter.client.model.service.observer.interfaces.UserObserver;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter<PagedView<T>> {
    protected static final int PAGE_SIZE = 10;

    protected T lastItem;
    protected boolean hasMorePages;
    protected boolean isLoading = false;

    protected UserService userService;

    public PagedPresenter(){
        this.userService = new UserService();
    }


    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }


    public void loadMoreItems(User user) {
        if (!isLoading) {
            isLoading = true;
            view.setLoadingFooter(true);
            getItems(user, PAGE_SIZE, lastItem);
        }
    }

    public abstract void getItems(User user, int PAGE_SIZE, T lastItem);

    protected class PageObserver extends BaseObserver<PagedPresenter<?>> implements PagedTaskObserver<T> {

        public PageObserver(PagedPresenter<?> presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(List<T> items, boolean hasMorePages) {
            onFinish();

            lastItem = (items.size() > 0) ? items.get(items.size() - 1) : null;
            setHasMorePages(hasMorePages);
            view.addMoreItems(items);
        }

        @Override
        public void onFinish() {
            isLoading = false;
            view.setLoadingFooter(isLoading);
        }

        @Override
        public String getErrorMessage() {
            return "get " + getPresenterType();
        }
    }

    public void getUserFromService(String aliasString) {
        userService.getUser(aliasString, new GetUserObserver(this));
    }

    protected class GetUserObserver extends BaseObserver<PagedPresenter<?>> implements UserObserver {

        public GetUserObserver(PagedPresenter<?> presenter) {
            super(presenter);
        }

        @Override
        public void handleSuccess(User user) {
            view.addUser(user);
        }

        @Override
        public String getErrorMessage() {
            return "get user's profile";
        }
    }

    public abstract String getPresenterType();

}
