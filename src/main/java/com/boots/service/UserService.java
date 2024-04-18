package com.boots.service;
import com.boots.entity.User;

public interface UserService {
    User register(User user);
    User findByUsername(String username);
    User findUserById(Long id);
    void deleteUser(Long id);
    void addPrivilegeToUser(Long userId, Long privilegeId);
    Long findUserIdByUsername(String username);
    Long findIdPrivilegeByName(String name);
    User updateUserName(Long userId, String newName);
}
