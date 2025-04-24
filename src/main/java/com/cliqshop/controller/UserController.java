package com.cliqshop.controller;

import com.cliqshop.entity.User;
import com.cliqshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // User Profile Endpoints
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user != null) {
            user.setPassword(null);
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateUserProfile(@RequestBody User updatedUser, Principal principal) {
        User user = userService.findByUsername(principal.getName());
        if (user != null) {
            user.setName(updatedUser.getName());
            user.setPhoneNumber(updatedUser.getPhoneNumber());
            User savedUser = userService.updateUser(user);
            savedUser.setPassword(null);
            return ResponseEntity.ok(savedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/password")
    public ResponseEntity<?> changePassword(@RequestParam String oldPassword, 
                                         @RequestParam String newPassword,
                                         Principal principal) {
        boolean success = userService.changePassword(principal.getName(), oldPassword, newPassword);
        if (success) {
            return ResponseEntity.ok().body("Password changed successfully");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid old password");
    }

    // Admin User Management Endpoints
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAllUsers();
        users.forEach(user -> user.setPassword(null));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long userId) {
        return userService.findById(userId)
                .map(user -> {
                    user.setPassword(null);
                    return ResponseEntity.ok(user);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.registerUser(user);
        createdUser.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long userId, @RequestBody User userDetails) {
        return userService.findById(userId)
                .map(user -> {
                    user.setName(userDetails.getName());
                    user.setEmail(userDetails.getEmail());
                    user.setPhoneNumber(userDetails.getPhoneNumber());
                    user.setRole(userDetails.getRole());
                    User updatedUser = userService.updateUser(user);
                    updatedUser.setPassword(null);
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long id) {
        boolean success = userService.toggleUserStatus(id);
        if (success) {
            return ResponseEntity.ok().body("User status updated successfully");
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        if (userService.findById(userId).isPresent()) {
            userService.deleteUser(userId);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}