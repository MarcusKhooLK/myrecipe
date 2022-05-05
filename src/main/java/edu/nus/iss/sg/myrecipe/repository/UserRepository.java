package edu.nus.iss.sg.myrecipe.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate template;

    public Boolean createAccount(final String username, final String password) {

        Integer result = findUserIdByUsername(username);

        // user exists
        if(result > 0) {
            return false;
        }

        result = template.update(SQL.CREATE_USER, username, password);
        return result > 0;
    }

    public Boolean authAccount(final String username, final String password) {
        final SqlRowSet result = template.queryForRowSet(SQL.AUTHORIZE_USER, username, password);

        return result.next();
    }

    public Integer findUserIdByUsername(final String username) {
        final SqlRowSet result = template.queryForRowSet(SQL.SELECT_USER_ID_BY_USERNAME, username);

        if(result.next()) {
            return result.getInt("user_id");
        }

        return -1;
    }
}
