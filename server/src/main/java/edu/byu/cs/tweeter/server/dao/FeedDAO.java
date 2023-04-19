package edu.byu.cs.tweeter.server.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.server.dao.beans.FeedBean;
import edu.byu.cs.tweeter.server.dao.interfaces.FeedDAOInterface;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public class FeedDAO extends PageDAO<FeedBean> implements FeedDAOInterface {

    private static final String TableName = "feed";

    private static final String UserAliasAttr = "userAlias";
    private static final String TimeStampAttr = "timestamp";

    @Override
    protected DynamoDbTable<FeedBean> getTable() {
        return getEnhancedClient().table(TableName, TableSchema.fromBean(FeedBean.class));
    }

    @Override
    protected void checkForPaging(FeedBean lastItem, String targetUserAlias, QueryEnhancedRequest.Builder requestBuilder) {
        if(lastItem != null) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put(UserAliasAttr, AttributeValue.builder().s(targetUserAlias).build());
            startKey.put(TimeStampAttr, AttributeValue.builder().n(Long.toString(lastItem.getTimestamp())).build());

            requestBuilder.exclusiveStartKey(startKey);
        }
    }

    @Override
    public Pair<List<FeedBean>, Boolean> getFeed(int pageLimit, String targetUser, FeedBean lastStatus) {
        DataPage<FeedBean> feedPage = getPage(targetUser,pageLimit,lastStatus);
        List<FeedBean> story = feedPage.getValues();
        boolean hasMorePages = feedPage.isHasMorePages();

        return new Pair<>(story,hasMorePages);
    }

    @Override
    public void addStatus(FeedBean feedBean) {
        DynamoDbTable<FeedBean> table = getEnhancedClient().table(TableName,
                TableSchema.fromBean(FeedBean.class));
        Key key = Key.builder()
                .partitionValue(feedBean.getUserAlias()).sortValue(feedBean.getTimestamp())
                .build();
        FeedBean status = table.getItem(key);
        if(status != null){
            table.updateItem(feedBean);
        }else{
            table.putItem(feedBean);
        }
    }
}
