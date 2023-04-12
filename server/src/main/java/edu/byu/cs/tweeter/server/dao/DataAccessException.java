package edu.byu.cs.tweeter.server.dao;

public class DataAccessException extends Exception{
    public DataAccessException(String message){
        super(message);
    }
}
