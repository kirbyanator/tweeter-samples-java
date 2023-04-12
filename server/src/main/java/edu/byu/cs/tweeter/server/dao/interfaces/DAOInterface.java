package edu.byu.cs.tweeter.server.dao.interfaces;

public interface DAOInterface<T> {
    void put(T bean);
    T get(String key);
}
