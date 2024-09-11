package com.plp.user_service.service;

import com.plp.user_service.model.User;
import com.plp.user_service.storage.AccountType;
import com.plp.user_service.storage.UserEntity;
import com.plp.user_service.storage.UserRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Component
public class UserService {

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
//        if (user.id() != null ) {
//            throw InvalidActivityException.becauseTheIdIsProvided(user.id().toString());
//        } else if (user.apiKey() != null) {
//            throw InvalidActivityException.becauseTheApiKeyIsProvided(user.apiKey());
//        }
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

}
