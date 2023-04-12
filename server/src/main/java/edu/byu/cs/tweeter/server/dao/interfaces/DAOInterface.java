package edu.byu.cs.tweeter.server.dao.interfaces;

public interface DAOInterface<T> {
    void put(T item);
    T get(String key);
}
