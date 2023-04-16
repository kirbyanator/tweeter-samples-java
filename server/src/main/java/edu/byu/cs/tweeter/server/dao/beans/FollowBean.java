package edu.byu.cs.tweeter.server.dao.beans;

import edu.byu.cs.tweeter.server.dao.FollowDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FollowBean {
    private String follower_handle;
    private String followerName;
    private String followee_handle;
    private String followeeName;

    public FollowBean(String follower_handle, String followerName, String followee_handle, String followeeName) {
        this.follower_handle = follower_handle;
        this.followerName = followerName;
        this.followee_handle = followee_handle;
        this.followeeName = followeeName;
    }

    public FollowBean() {
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = FollowDAO.indexName)
    public String getFollower_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = FollowDAO.indexName)
    public String getFollowee_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    public String getFolloweeName() {
        return followeeName;
    }

    public void setFolloweeName(String followeeName) {
        this.followeeName = followeeName;
    }
}
