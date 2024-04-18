package com.boots.service.impl;

import com.boots.entity.Privileges;
import com.boots.entity.User;
import com.boots.repository.PrivilegeRepository;
import com.boots.repository.UserRepository;
import com.boots.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    //    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(@Lazy UserRepository userRepository,
//                           @Lazy RoleRepository roleRepository,
                           @Lazy BCryptPasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;

    }

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User registeredUser = userRepository.save(user);
        return registeredUser;
    }

//    @Override
//    public List<User> getAll() {
//        List<User> result = userRepository.findAll();
//
//        return result;
//    }


    @Override
    public User findByUsername(String username) {
        User result = userRepository.findByUsername(username);
        return result;
    }

    @Override
    public Long findUserIdByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user.getId();
        } else {
            return null; // исключение, если пользователь не найден
        }
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public void addPrivilegeToUser(Long userId, Long privilegeId) {
        User user = userRepository.findById(userId).orElse(null);
        Privileges privilege = privilegeRepository.findById(privilegeId).orElse(null);

        if (user != null && privilege != null) {
            user.addPrivilege(privilege);
            userRepository.save(user);
        }
    }

    public void deleteUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        userRepository.delete(user);
    }

    public Long findIdPrivilegeByName(String name) {
        Long id = privilegeRepository.findIdPrivilegeByName(name);
        return id;
    }

    public User updateUserName(Long userId, String newName) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(newName);
        return userRepository.save(user);
    }
}