package com.plp.user_service.service;

import com.plp.user_service.model.User;
import com.plp.user_service.storage.AccountType;
import com.plp.user_service.storage.UserEntity;
import com.plp.user_service.storage.UserRepository;
import org.springframework.data.util.Pair;
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
        Pair<String, String> userCredentials = extractCredentials(authHeader);
        User user = validateUserCredentials(userCredentials.getFirst(), userCredentials.getSecond());

        return user.accountType().ordinal() >= accountType.ordinal();
    }

    private Pair<String, String> extractCredentials(String authorizationHeaderValue) {
        String base64Credentials = authorizationHeaderValue.substring("Basic ".length()).trim();

        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
        String decodedCredentials = new String(decodedBytes);

        String[] credentials = decodedCredentials.split(":", 2);
        if (credentials.length != 2) {
            throw new IllegalArgumentException("Invalid credentials format.");
        }

        return Pair.of(credentials[0], credentials[1]);
    }

    private User validateUserCredentials(String username, String apiKey) {
        User user = userRepository.findByUsername(username)
                .map(User::fromUserEntity)
                .orElseThrow(() -> new NoSuchElementException("User with username " + username + " not found"));

        if (!user.apiKey().equals(apiKey)) {
            throw new SecurityException("Invalid API key: " + apiKey);
        }

        return user;
    }

//    public Boolean validateAccountType(String authHeader, AccountType accountType) {
//        return extractApiKey(authHeader)
//                .flatMap(this::findUserByApiKey)
//                .map(user -> user.accountType().ordinal() >= accountType.ordinal())
//                .orElse(false);
//    }
//
//    private Optional<User> findUserByApiKey(String apiKey) {
//        return Optional.of(userRepository.findByApiKey(apiKey)
//                .map(User::fromUserEntity)
//                .orElseThrow());
//    }

//    private Optional<String> extractApiKey(String authorizationHeaderValue) {
//        String base64Credentials = authorizationHeaderValue.substring("Basic ".length()).trim();
//
//        byte[] decodedBytes = Base64.getDecoder().decode(base64Credentials);
//        String decodedCredentials = new String(decodedBytes);
//
//        String[] credentials = decodedCredentials.split(":", 2);
//
//        if (credentials.length != 2) {
//            throw new IllegalArgumentException("Invalid credentials format.");
//        }
//
//        return Optional.ofNullable(credentials[1]);
//    }
}
