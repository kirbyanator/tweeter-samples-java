package edu.byu.cs.tweeter.server.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import edu.byu.cs.tweeter.server.dao.interfaces.FollowDAOInterface;
import edu.byu.cs.tweeter.util.FakeData;
import edu.byu.cs.tweeter.util.Pair;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

/**
 * A DAO for accessing 'following' data from the database.
 */
public class FollowDAO extends BaseDAO implements FollowDAOInterface {

    public static final String indexName = "follows_index";
    private static final String table = "follows";


    /**
     * Gets the count of users from the database that the user specified is following. The
     * current implementation uses generated data and doesn't actually access a database.
     *
     * @param follower the User whose count of how many following is desired.
     * @return said count.
     */
    public Integer getFolloweeCount(User follower) {
        // TODO: uses the dummy data.  Replace with a real implementation.
        assert follower != null;
        return getDummyFollowees().size();
    }

    public Pair<List<FollowBean>, Boolean> getFollowees(String followerAlias, int limit, String lastFolloweeAlias) {
        // TODO: Generates dummy data. Replace with a real implementation.
        assert limit > 0;
        assert followerAlias != null;

        DataPage<FollowBean> page = getPageOfFollowees(followerAlias, limit, lastFolloweeAlias);
        boolean hasMorePages = page.isHasMorePages();
        List<FollowBean> beanfollowees = page.getValues();

        return new Pair<>(beanfollowees, hasMorePages);
    }

    public Pair<List<FollowBean>, Boolean> getFollowers(String followeeAlias, int limit, String lastFollowerAlias) {
        assert limit > 0;
        assert followeeAlias != null;

        DataPage<FollowBean> page = getPageOfFollowers(followeeAlias, limit, lastFollowerAlias);
        boolean hasMorePages = page.isHasMorePages();
        List<FollowBean> beanfollowees = page.getValues();

        return new Pair<>(beanfollowees, hasMorePages);
    }

    /**
     * Determines the index for the first followee in the specified 'allFollowees' list that should
     * be returned in the current request. This will be the index of the next followee after the
     * specified 'lastFollowee'.
     *
     * @param lastFolloweeAlias the alias of the last followee that was returned in the previous
     *                          request or null if there was no previous request.
     * @param allFollowees the generated list of followees from which we are returning paged results.
     * @return the index of the first followee to be returned.
     */
    private int getUserStartingIndex(String lastFolloweeAlias, List<User> allFollowees) {

        int followeesIndex = 0;

        if(lastFolloweeAlias != null) {
            // This is a paged request for something after the first page. Find the first item
            // we should return
            for (int i = 0; i < allFollowees.size(); i++) {
                if(lastFolloweeAlias.equals(allFollowees.get(i).getAlias())) {
                    // We found the index of the last item returned last time. Increment to get
                    // to the first one we should return
                    followeesIndex = i + 1;
                    break;
                }
            }
        }

        return followeesIndex;
    }

    /**
     * Returns the list of dummy followee data. This is written as a separate method to allow
     * mocking of the followees.
     *
     * @return the followees.
     */
    List<User> getDummyFollowees() {
        return getFakeData().getFakeUsers();
    }
    List<User> getDummyFollowers() {
        return getFakeData().getFakeUsers();
    }

    /**
     * Returns the {@link FakeData} object used to generate dummy followees.
     * This is written as a separate method to allow mocking of the {@link FakeData}.
     *
     * @return a {@link FakeData} instance.
     */
    FakeData getFakeData() {
        return FakeData.getInstance();
    }


    public DataPage<FollowBean> getPageOfFollowees(String targetUserAlias, int pageSize, String lastUserAlias){
        DynamoDbTable<FollowBean> table = getEnhancedClient().table(FollowDAO.table, TableSchema.fromBean(FollowBean.class));
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();
        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("follower_handle", AttributeValue.builder().s(targetUserAlias).build());
            startKey.put("followee_handle", AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowBean> result = new DataPage<FollowBean>();

        PageIterable<FollowBean> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(followers -> result.getValues().add(followers));
                });

        return result;
    }

    public DataPage<FollowBean> getPageOfFollowers(String targetUserAlias, int pageSize, String lastUserAlias) {
        DynamoDbIndex<FollowBean> index = getEnhancedClient().table(table, TableSchema.fromBean(FollowBean.class)).index(indexName);
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize);

        if(isNonEmptyString(lastUserAlias)) {
            // Build up the Exclusive Start Key (telling DynamoDB where you left off reading items)
            Map<String, AttributeValue> startKey = new HashMap<>();
            startKey.put("followee_handle", AttributeValue.builder().s(targetUserAlias).build());
            startKey.put("follower_handle", AttributeValue.builder().s(lastUserAlias).build());

            requestBuilder.exclusiveStartKey(startKey);
        }

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<FollowBean> result = new DataPage<FollowBean>();

        SdkIterable<Page<FollowBean>> sdkIterable = index.query(request);
        PageIterable<FollowBean> pages = PageIterable.create(sdkIterable);
        pages.stream()
                .limit(1)
                .forEach((Page<FollowBean> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(followees -> result.getValues().add(followees));
                });

        return result;
    }

    private static boolean isNonEmptyString(String value) {
        return (value != null && value.length() > 0);
    }

    @Override
    public void put(FollowBean item) {
        DynamoDbTable<FollowBean> table = getEnhancedClient().table(FollowDAO.table, TableSchema.fromBean(FollowBean.class));
        table.putItem(item);
    }

    @Override
    public FollowBean get(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = getEnhancedClient().table(FollowDAO.table, TableSchema.fromBean(FollowBean.class));
        Key tablekey = Key.builder()
                .partitionValue(follower_handle)
                .sortValue(followee_handle)
                .build();
        FollowBean entry = table.getItem(tablekey);
        return entry;
    }

    @Override
    public void remove(String follower_handle, String followee_handle) {
        DynamoDbTable<FollowBean> table = getEnhancedClient().table(FollowDAO.table, TableSchema.fromBean(FollowBean.class));
        Key tablekey = Key.builder()
                .partitionValue(follower_handle)
                .sortValue(followee_handle)
                .build();
        table.deleteItem(tablekey);
    }

    @Override
    public void update(FollowBean item) {

    }
}
