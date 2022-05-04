package edu.nus.iss.sg.myrecipe.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate template;

    public Boolean createAccount(final String username, final String email, final String password) {

        final SqlRowSet rowSet = template.queryForRowSet(SQL.FIND_USER_BY_EMAIL, email);

        // email exists
        if(rowSet.next()) {
            return false;
        }

        int result = template.update(SQL.CREATE_USER, email, username, password);
        return result > 0;
    }

    public Boolean authAccount(final String username, final String password) {
        final SqlRowSet result = template.queryForRowSet(SQL.AUTHORIZE_USER, username, password);

        return result.next();
    }
}
