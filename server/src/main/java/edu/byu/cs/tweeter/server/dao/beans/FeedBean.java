package edu.byu.cs.tweeter.server.dao.beans;

import java.util.List;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class FeedBean {
    private String userAlias;

    private Long timestamp;

    private String postAlias;

    private List<String> urls;

    private List<String> mentions;

    private String post;

    public FeedBean(String alias, Long timestamp, String postAlias, List<String> urls, List<String> mentions, String post) {
        this.userAlias = alias;
        this.timestamp = timestamp;
        this.postAlias = postAlias;
        this.urls = urls;
        this.mentions = mentions;
        this.post = post;
    }

    public FeedBean() {
    }

    @DynamoDbPartitionKey
    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    @DynamoDbSortKey
    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostAlias() {
        return postAlias;
    }

    public void setPostAlias(String postAlias) {
        this.postAlias = postAlias;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getMentions() {
        return mentions;
    }

    public void setMentions(List<String> mentions) {
        this.mentions = mentions;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }
}
