package com.cliqshop.service;

import com.cliqshop.entity.User;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {
    User registerUser(User user);
    User findByUsername(String username);
    User findByEmail(String email);
    Optional<User> findById(Long userId);
    List<User> findAllUsers();
    User updateUser(User user);
    boolean changePassword(String username, String oldPassword, String newPassword);
    boolean toggleUserStatus(Long userId);
    boolean deleteUser(Long userId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    long getTotalUsers();
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}