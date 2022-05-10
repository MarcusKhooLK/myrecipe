package edu.nus.iss.sg.myrecipe.repository;

public interface SQL {
    // user
    public static final String CREATE_USER = "insert into user (username, password) values (?, sha1(?));";
    public static final String SELECT_USER_ID_BY_USERNAME = "select user_id from user where username = ?;";
    public static final String AUTHORIZE_USER = "select username from user where username = ? and password = sha1(?);";
    public static final String DELETE_USER = "delete from user where user_id = ?;";

    // recipe
    public static final String INSERT_RECIPE = "insert into recipe (name, category, country, instructions, thumbnail, youtubeLink, user_id) values (?, ?, ?, ?, ?, ?, ?);";
    public static final String SELECT_RECIPE_BY_NAME = "select * from recipe where name like ?;";
    public static final String SELECT_ALL_RECIPE_BY_USERID = "select * from recipe where user_id = ?;";
    public static final String SELECT_RECIPE_BY_ID = "select * from recipe where recipe_id = ?;";
    public static final String DELETE_RECIPE_BY_ID = "delete from recipe where recipe_id = ?;";

    // ingredient
    public static final String INSERT_INGREDIENT = "insert into ingredient (name, measurement, recipe_id) values (?, ?, ?);";
    public static final String SELECT_INGREDIENTS_BY_RECIPEID = "select * from ingredient where recipe_id = ?;";
    public static final String DELETE_INGREDIENTS_BY_RECIPEID = "delete from ingredient where recipe_id = ?;";

    // join
    public static final String SELECT_USERNAME_BY_RECIPEID = 
    """
    select u.username
    from user as u
    join recipe as r
    on u.user_id = r.user_id
    where r.recipe_id = ?;
    """;
}
