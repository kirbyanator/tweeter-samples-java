package edu.byu.cs.tweeter.server.service;

import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;
import edu.byu.cs.tweeter.server.dao.FeedDAO;
import edu.byu.cs.tweeter.server.dao.FollowDAO;
import edu.byu.cs.tweeter.server.dao.StatusDAO;
import edu.byu.cs.tweeter.server.dao.StoryDAO;
import edu.byu.cs.tweeter.server.dao.UserDAO;
import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import edu.byu.cs.tweeter.util.Pair;

public class StatusService extends Service {
    public FeedResponse getFeed(FeedRequest request){
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        }
        if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        FeedDAO feedDAO = factory.getFeedDAO();
        try{
            authenticateToken(request.getAuthToken());
            Pair<List<FeedBean>,Boolean> result = feedDAO.getFeed(request.getLimit(),
                    request.getUserAlias(), statusToFeedBean(request.getLastStatus(),
                            request.getUserAlias()));
            List<Status> feed = feedBeanToList(result.getFirst());
            return new FeedResponse(feed, result.getSecond());
        }
        catch(Exception e){
            return new FeedResponse(e.getMessage());
        }

    }

    StatusDAO getStatusDAO() {
        return new StatusDAO();
    }

    public StoryResponse getStory(StoryRequest request) {
        if(request.getUserAlias() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a target alias");
        }
        if(request.getLimit() <= 0){
            throw new RuntimeException("[Bad Request] Request needs to have a positive limit");
        }
        try{
            authenticateToken(request.getAuthToken());
            StoryDAO storyDAO = factory.getStoryDAO();
            Pair<List<StoryBean>,Boolean> data = storyDAO.getStory(request.getLimit(),
                    request.getUserAlias(), statusToBean(request.getLastStatus()));
            List<Status> statuses = new ArrayList<>();

            for(StoryBean storyBean:data.getFirst()){
                statuses.add(storyBeanToStatus(storyBean));
            }

            return new StoryResponse(statuses,data.getSecond());
        }
        catch(Exception e){
            return new StoryResponse(e.getMessage());
        }
    }

    public PostStatusResponse postStatus(PostStatusRequest request) {
        if(request.getStatus() == null){
            throw new RuntimeException("[Bad Request] Request needs to have a status");
        }

        UserDAO userDAO = factory.getUserDAO();
        FollowDAO followDAO = factory.getFollowDAO();
        FeedDAO feedDAO = factory.getFeedDAO();
        StoryDAO storyDAO = factory.getStoryDAO();

        try{
            authenticateToken(request.getAuthToken());

            StoryBean storyItem = new StoryBean(request.getStatus().getUser().getAlias(), request.getStatus().getTimestamp(),
                    request.getStatus().getUrls(), request.getStatus().getMentions(), request.getStatus().getPost());

            storyDAO.postStatus(storyItem);

            // posting to story done, now update feeds if necessary

            UserBean postingUserBean = userDAO.get(storyItem.getUserAlias());
            if(postingUserBean.getFollowerCount() == 0){
                // if user in question has no followers, success
                return new PostStatusResponse();
            }

            // get followers (whose feeds we'll need to update)
            List<FollowBean> followers = followDAO.getFollowers(
                    postingUserBean.getAlias(), 1000000, null
            ).getFirst();

            List<FeedBean> feedBeanList = new ArrayList<>();
            for(FollowBean followbean:followers){
                FeedBean feedBean = storyBeanToFeedBean(storyItem,followbean.getFollower_handle());
                feedBeanList.add(feedBean);
            }
            for(FeedBean feedBean:feedBeanList){
                feedDAO.addStatus(feedBean);
            }

        }
        catch(Exception e){
            return new PostStatusResponse(e.getMessage());
        }

        return new PostStatusResponse();
    }

    public Status storyBeanToStatus(StoryBean storyBean) {
        UserDAO userDAO = factory.getUserDAO();
        User user = null;
        try{
            UserBean userBean = userDAO.get(storyBean.getUserAlias());
            user = convertUserBean(userBean);
        }catch (Exception ex){
            System.out.println(ex.getMessage());
        }

        Status status = new Status(storyBean.getPost(),user,
                storyBean.getTimestamp(),storyBean.getUrls(),storyBean.getMentions());
        return status;
    }

    public StoryBean statusToBean(Status status){
        if(status == null){
            return null;
        }
        StoryBean storyBean = new StoryBean(status.user.getAlias(),
                status.getTimestamp(),status.urls,status.mentions,status.post);
        return storyBean;
    }

    public FeedBean storyBeanToFeedBean(StoryBean storyBean, String ownerAlias){
        FeedBean feedBean = new FeedBean(ownerAlias,storyBean.getTimestamp(),storyBean.getUserAlias(),
                storyBean.getUrls(),storyBean.getMentions(),storyBean.getPost());
        return feedBean;
    }

    public List<Status> feedBeanToList(List<FeedBean> feedBeanList){
        UserDAO userDAO = factory.getUserDAO();
        List<Status> feed = new ArrayList<>();

        for(FeedBean feedBean:feedBeanList){
            try {
                UserBean userBean = userDAO.get(feedBean.getPostAlias());
                Status status = new Status(feedBean.getPost(),convertUserBean(userBean),
                        feedBean.getTimestamp(),feedBean.getUrls(),feedBean.getMentions());
                feed.add(status);
            }catch (Exception ex){
                System.out.println(ex.getMessage());
                throw new RuntimeException("[Bad Request] problem with feed");
            }
        }
        return feed;
    }

    public FeedBean statusToFeedBean(Status status, String ownerAlias){
        if(status == null){
            return null;
        }
        FeedBean feedbean = new FeedBean(ownerAlias,status.getTimestamp(),status.getUser().getAlias(),
                status.getUrls(),status.getMentions(),status.getPost());
        return feedbean;
    }
}
