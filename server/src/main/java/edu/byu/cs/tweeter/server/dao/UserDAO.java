package edu.byu.cs.tweeter.server.dao;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.Base64;

import edu.byu.cs.tweeter.server.dao.beans.UserBean;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOInterface;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

public class UserDAO extends BaseDAO implements DAOInterface<UserBean> {

    private static final String TableName = "users";

    public String uploadImage(String image, String alias){
        AmazonS3 s3 = AmazonS3ClientBuilder
                .standard()
                .withRegion("us-east-2")
                .build();

        byte[] byteArray = Base64.getDecoder().decode(image);

        ObjectMetadata data = new ObjectMetadata();

        data.setContentLength(byteArray.length);

        data.setContentType("image/jpeg");

        PutObjectRequest request = new PutObjectRequest("davisdatabucket", alias, new ByteArrayInputStream(byteArray), data).withCannedAcl(CannedAccessControlList.PublicRead);

        s3.putObject(request);

        return "https://davisdatabucket.s3.us-east-2.amazonaws.com/" + alias;
    }

    @Override
    public void put(UserBean item){
        DynamoDbTable<UserBean> table = getEnhancedClient().table(TableName, TableSchema.fromBean(UserBean.class));
        table.putItem(item);
    }

    @Override
    public UserBean get(String key) {
        DynamoDbTable<UserBean> table = getEnhancedClient().table(TableName, TableSchema.fromBean(UserBean.class));
        Key tablekey = Key.builder()
                .partitionValue(key)
                .build();
        UserBean entry = table.getItem(tablekey);
        return entry;
    }

    @Override
    public void remove(String key) {

    }

    @Override
    public void update(UserBean item) {
        if(get(item.getAlias()) == null){
            throw new RuntimeException("[Bad Request], User does not exist in database");
        }
        DynamoDbTable<UserBean> table = getEnhancedClient().table(TableName, TableSchema.fromBean(UserBean.class));
        table.putItem(item);
    }


}
