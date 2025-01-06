package com.personal.expensetracker.expensetracker;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUser(String email){
        return userRepository.findById(email);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User changePassword(User user, String password) {
        user.setPassword(password);
        return userRepository.save(user);
    }

    public boolean deleteUser(String email){
        var isdeleted = true;
        if(userRepository.existsById(email)) userRepository.deleteById(email);
        else isdeleted = false;
        return isdeleted;
    }

    public boolean userExists(String email){
        return userRepository.existsById(email);
    }

}
