package edu.nus.iss.sg.myrecipe.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.nus.iss.sg.myrecipe.repository.UserRepository;

@Service
public class LogInService {

    @Autowired
    private UserRepository userRepo;

    public Boolean createAccount(final String username, final String password) {
        return userRepo.createAccount(username, password);
    }

    public Boolean authAccount(final String username, final String password) {
        return userRepo.authAccount(username, password);
    }

    public Boolean deleteAccount(final String username) {
        return userRepo.deleteAccount(username);
    }
}
