package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.server.dao.interfaces.DAOFactoryInterface;

public class DAOFactory implements DAOFactoryInterface {

    public UserDAO getUserDAO(){
        return new UserDAO();
    }

    public AuthTokenDAO getAuthTokenDAO(){
        return new AuthTokenDAO();
    }

    public StatusDAO getStatusDAO(){
        return new StatusDAO();
    }

    public FollowDAO getFollowDAO(){
        return new FollowDAO();
    }

}
