package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status>{

    private final StatusService statusService;

    public FeedPresenter(PagedView<Status> view){
        super();
        this.view = view;
        this.statusService = new StatusService();
    }

    @Override
    public void getItems(User user, int PAGE_SIZE, Status lastItem) {
        statusService.getFeed(user, PAGE_SIZE, lastItem, new PageObserver(this));
    }

    @Override
    public String getPresenterType() {
        return "feed";
    }
}
