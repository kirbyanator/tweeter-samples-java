package edu.byu.cs.tweeter.server.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.beans.StoryBean;
import edu.byu.cs.tweeter.server.dao.interfaces.StoryDAOInterface;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class StoryDAO extends PageDAO<StoryBean> implements StoryDAOInterface {

    private static final String table = "story";

    private static final String partition = "userAlias";
    private static final String sort = "timestamp";

    @Override
    protected DynamoDbTable<StoryBean> getTable() {
        return getEnhancedClient().table(table, TableSchema.fromBean(StoryBean.class));
    }

    @Override
    protected void findStartingIndex(StoryBean lastItem, String targetUserAlias, QueryEnhancedRequest.Builder requestBuilder) {
        if(lastItem != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(partition, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(sort, AttributeValue.builder().n(Long.toString(lastItem.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }
    }

    @Override
    public Pair<List<StoryBean>, Boolean> getStory(int pageLimit, String targetUser, StoryBean lastStatus) {
        DataPage<StoryBean> storyPage = getPage(targetUser,pageLimit,lastStatus);
        List<StoryBean> story = storyPage.getValues();
        boolean hasMorePages = storyPage.isHasMorePages();

        return new Pair<>(story,hasMorePages);
    }

    @Override
    public void postStatus(StoryBean status) {
        DynamoDbTable<StoryBean> table = getEnhancedClient().table(StoryDAO.table,
                TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder()
                .partitionValue(status.getUserAlias()).sortValue(status.getTimestamp())
                .build();

        StoryBean storyBean = table.getItem(key);
        if(storyBean != null){
            table.updateItem(status);
        }else{
            table.putItem(status);
        }
    }

    @Override
    public boolean update(StoryBean status) {
        DynamoDbTable<StoryBean> table = getEnhancedClient().table(StoryDAO.table,TableSchema.fromBean(StoryBean.class));
        Key key = Key.builder()
                .partitionValue(status.getUserAlias()).sortValue(status.getTimestamp())
                .build();
        StoryBean storyBean = table.getItem(key);
        if(storyBean != null){
            table.updateItem(status);
        }else{
            return false;
        }
        return true;
    }
}
