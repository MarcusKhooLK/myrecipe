package edu.nus.iss.sg.myrecipe.repository;

public interface SQL {
    public static final String CREATE_USER = "insert into user (email, username, password) values (?, ?, sha1(?));";
    public static final String FIND_USER_BY_EMAIL = "select email from user where email = ?;";
    public static final String AUTHORIZE_USER = "select username from user where username = ? and password = sha1(?);";
}
