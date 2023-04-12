package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.beans.AuthTokenBean;
import edu.byu.cs.tweeter.server.dao.interfaces.DAOInterface;

public class AuthTokenDAO extends BaseDAO implements DAOInterface<AuthTokenBean> {

    private static final String TableName = "authtoken";

    @Override
    public void put(AuthTokenBean bean) {

    }

    @Override
    public AuthTokenBean get(String key) {
        return null;
    }
}
