package edu.byu.cs.tweeter.client.presenter.view;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.User;

public interface PagedView<T> extends View{
    void addUser(User user);
    void addMoreItems(List<T> items);
    void setLoadingFooter(boolean value);
}
