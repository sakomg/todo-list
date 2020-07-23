package com.komegunov.todo.service;

import com.komegunov.todo.persistence.entity.User;
import com.komegunov.todo.persistence.repository.UserRepository;
import com.komegunov.todo.repr.UserRepr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void create(UserRepr userRepr) {
        User user = new User();
        user.setUsername(userRepr.getUsername());
        user.setPassword(passwordEncoder.encode(userRepr.getPassword()));
        userRepository.save(user);
    }

    public Optional<Long> getCurrentUserId() {
        Optional<String> currentUser = getCurrentUser();
        return currentUser.flatMap(s -> userRepository.getUserByUsername(s)
                .map(User::getId));
    }

    public static Optional<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return Optional.of(authentication.getName());
        }
        return Optional.empty();
    }
}
