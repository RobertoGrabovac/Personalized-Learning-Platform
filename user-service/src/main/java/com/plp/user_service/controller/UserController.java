package com.plp.user_service.controller;

import com.plp.user_service.model.User;
import com.plp.user_service.service.UserService;
import com.plp.user_service.storage.AccountType;
import com.plp.user_service.storage.UserEntity;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/validateAccountType")
    public ResponseEntity<Boolean> validateAccountType(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam AccountType accountType) {
        Boolean isValid = userService.validateAccountType(authHeader, accountType);
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        List<UserEntity> userEntities = userService.getAllUsers();

        return userEntities.stream()
                .map(User::fromUserEntity)
                .collect(Collectors.toList());
    }

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody @Valid User user) {
        return userService.createUser(user);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        UserEntity userEntity = userService.getUserById(userId);
        if (userEntity != null) {
            User user = User.fromUserEntity(userEntity);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<String> updateAccountType(
            @PathVariable Long userId,
            @RequestBody Map<String, String> body) {
        try {
            String accountType = body.get("accountType");
            if (accountType == null) {
                return ResponseEntity.badRequest().body("Account type is missing");
            }

            AccountType newAccountType = AccountType.valueOf(accountType.toUpperCase());

            userService.updateSubscriptionType(userId, newAccountType);
            return ResponseEntity.ok("Subscription type updated successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid subscription type");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted successfully");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/test")
    public String test() {
        return "Test";
    }

}
