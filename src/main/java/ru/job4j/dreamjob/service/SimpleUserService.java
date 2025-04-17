package ru.job4j.dreamjob.service;

import org.springframework.stereotype.Service;
import org.sql2o.Sql2oException;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.repository.UserRepository;

import java.util.Optional;

@Service
public class SimpleUserService implements UserService {

    private final UserRepository userRepository;

    public SimpleUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<User> save(User user) {
        if (user == null
                || user.getEmail() == null
                || user.getPassword() == null) {
            return Optional.empty();
        }
        try {
            return userRepository.save(user);
        } catch (Sql2oException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        if (email == null
                || password == null) {
            return Optional.empty();
        }
        return userRepository.findByEmailAndPassword(email, password);
    }
}
