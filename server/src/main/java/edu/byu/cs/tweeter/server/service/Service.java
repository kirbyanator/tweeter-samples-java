package edu.byu.cs.tweeter.server.service;

import java.security.SecureRandom;
import java.util.Base64;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.server.dao.AuthTokenDAO;
import edu.byu.cs.tweeter.server.dao.DAOFactory;
import edu.byu.cs.tweeter.server.dao.beans.AuthTokenBean;

public abstract class Service {
    protected final DAOFactory factory = new DAOFactory();
    protected static final SecureRandom secureRandom = new SecureRandom();
    protected static final Base64.Encoder base64Encoder = Base64.getUrlEncoder();

    protected void authenticateToken(AuthToken token){
        AuthTokenDAO tokenDAO = factory.getAuthTokenDAO();
        AuthTokenBean resultBean = tokenDAO.get(token.getToken());

        if(resultBean == null){
            throw new RuntimeException("[Bad Request] AuthToken not valid");
        }
        long timeDif = System.currentTimeMillis() - token.getTimestamp();
        if(timeDif > 800000){
            throw new RuntimeException("[Bad Request] AuthToken not valid");
        }

        resultBean.setTimestamp(System.currentTimeMillis());
        tokenDAO.update(resultBean);

    }
}
