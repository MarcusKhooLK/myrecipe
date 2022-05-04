package edu.nus.iss.sg.myrecipe.repository;

public interface SQL {
    public static final String CREATE_USER = "insert into user (username, password) values (?, sha1(?));";
    public static final String SELECT_USER_ID_BY_USERNAME = "select user_id from user where username = ?;";
    public static final String AUTHORIZE_USER = "select username from user where username = ? and password = sha1(?);";
}
