package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.presenter.view.PagedView;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowingPresenter extends PagedPresenter<User>{

    private final FollowService followService;

    public FollowingPresenter(PagedView<User> view){
        super();
        this.view = view;
        this.followService = new FollowService();
    }

    @Override
    public void getItems(User user, int PAGE_SIZE, User lastItem) {
        followService.getFollowees(user, PAGE_SIZE, lastItem, new PageObserver(this));
    }

    @Override
    public String getPresenterType() {
        return "followees";
    }
}
