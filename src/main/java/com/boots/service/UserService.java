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
    String getConfirmCode (Long userId);
    Boolean getConfirmStatus(Long userId);
    void setConfirmStatusTrue(Long userId);
    boolean addPasswordCode(String username, String passwordCode);
    boolean updatePassword(String username, String newPassword);
    String getConfirmPasswordCode (Long userId);
}
