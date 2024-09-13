package com.plp.user_service.service;

import com.plp.user_service.model.User;
import com.plp.user_service.storage.AccountType;
import com.plp.user_service.storage.UserEntity;
import com.plp.user_service.storage.UserRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class UserService {

    private static final String AUTHORIZATION_HEADER_PREFIX = "Basic ";

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUser(Long userId)
    {
        return userRepository.findById(userId)
                .map(User::fromUserEntity)
                .orElseThrow();
    };

    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(User user) {
        String apiKey = UUID.randomUUID().toString().replace("-", "");

        var userEntity = new UserEntity(
                null,
                apiKey,
                user.username(),
                user.accountType()
        );

        return User.fromUserEntity(userRepository.save(userEntity));
    }

    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void updateSubscriptionType(Long userId, AccountType newSubscriptionType) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        userEntity.setAccountType(newSubscriptionType);
        userRepository.save(userEntity);
    }

    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoSuchElementException("User not found");
        }
        userRepository.deleteById(userId);
    }

    public Boolean validateAccountType(String authHeader, AccountType accountType) {
        return extractApiKey(authHeader)
                .flatMap(this::findUserByApiKey)
                .map(user -> user.accountType().ordinal() >= accountType.ordinal())
                .orElse(false);
    }

    private Optional<User> findUserByApiKey(String apiKey) {
        return Optional.of(userRepository.findByApiKey(apiKey)
                .map(User::fromUserEntity)
                .orElseThrow());
    }

    // TODO: refactor
    private Optional<String> extractApiKey(String authorizationHeaderValue) {
        String base64Credentials = authorizationHeaderValue.substring("Basic ".length()).trim();

        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String decodedCredentials = new String(decodedBytes);

        String[] credentials = decodedCredentials.split(":", 2);
        if (credentials.length == 2) {
            String username = credentials[0];
            String password = credentials[1];
            System.out.println("Username: " + username);
            System.out.println("Password: " + password);
        } else {
            System.out.println("Invalid credentials format.");
        }
        return Optional.ofNullable(credentials[1]);
    }
}
