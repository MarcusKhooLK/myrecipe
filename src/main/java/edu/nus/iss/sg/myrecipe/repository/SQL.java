package edu.nus.iss.sg.myrecipe.repository;

public interface SQL {
    public static final String CREATE_USER = "insert into user (username, password) values (?, sha1(?));";
    public static final String SELECT_USER_ID_BY_USERNAME = "select user_id from user where username = ?;";
    public static final String AUTHORIZE_USER = "select username from user where username = ? and password = sha1(?);";
    public static final String CREATE_RECIPE = 
    """
        insert into recipe (name, category, country, instructions, thumbnail, youtubeLink, 
        ingredient0,
        ingredient1,
        ingredient2,
        ingredient3,
        ingredient4,
        ingredient5,
        ingredient6,
        ingredient7,
        ingredient8,
        ingredient9,
        measurement0,
        measurement1,
        measurement2,
        measurement3,
        measurement4,
        measurement5,
        measurement6,
        measurement7,
        measurement8,
        measurement9,
        user_id) values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
        ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
        ?, ?, ?, ?, ?, ?, ?);
            """;
}
