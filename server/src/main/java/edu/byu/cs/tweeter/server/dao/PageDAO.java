package edu.byu.cs.tweeter.server.dao;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

public abstract class PageDAO<T> extends BaseDAO{

    public DataPage<T> getPage(String targetUserAlias, int pageSize, T lastItem){
        DynamoDbTable<T> table = getTable();
        Key key = Key.builder()
                .partitionValue(targetUserAlias)
                .build();

        QueryEnhancedRequest.Builder requestBuilder = QueryEnhancedRequest.builder()
                .queryConditional(QueryConditional.keyEqualTo(key))
                .limit(pageSize).scanIndexForward(false);

        checkForPaging(lastItem,targetUserAlias,requestBuilder);

        QueryEnhancedRequest request = requestBuilder.build();

        DataPage<T> result = new DataPage<T>();

        PageIterable<T> pages = table.query(request);
        pages.stream()
                .limit(1)
                .forEach((Page<T> page) -> {
                    result.setHasMorePages(page.lastEvaluatedKey() != null);
                    page.items().forEach(item -> result.getValues().add(item));
                });
        return result;
    }

    protected abstract DynamoDbTable<T> getTable();

    protected abstract void checkForPaging(T lastItem, String targetUserAlias, QueryEnhancedRequest.Builder requestBuilder);

}
