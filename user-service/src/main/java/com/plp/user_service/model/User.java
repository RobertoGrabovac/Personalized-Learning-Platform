package com.plp.user_service.model;

import com.plp.user_service.storage.AccountType;
import com.plp.user_service.storage.UserEntity;

public record User(
        Long id,
        String apiKey,
        String username,
        AccountType accountType
) {
    public static User fromUserEntity(UserEntity userEntity){
        return new User(
                userEntity.getId(),
                userEntity.getApiKey(),
                userEntity.getUsername(),
                userEntity.getAccountType()
        );
    }
}
