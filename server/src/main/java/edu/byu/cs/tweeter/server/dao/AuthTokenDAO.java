package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.beans.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOInterface;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class AuthTokenDAO extends BaseDAO implements DAOInterface<AuthTokenBean> {

    private static final String TableName = "authtoken";

    @Override
    public void put(AuthTokenBean item) {
        DynamoDbTable<AuthTokenBean> table = getEnhancedClient().table(TableName, TableSchema.fromBean(AuthTokenBean.class));
        table.putItem(item);
    }

    @Override
    public AuthTokenBean get(String key) {
        DynamoDbTable<AuthTokenBean> table = getEnhancedClient().table(TableName, TableSchema.fromBean(AuthTokenBean.class));
        Key tablekey = Key.builder()
                .partitionValue(key)
                .build();
        AuthTokenBean entry = table.getItem(tablekey);
        return entry;
    }
}
