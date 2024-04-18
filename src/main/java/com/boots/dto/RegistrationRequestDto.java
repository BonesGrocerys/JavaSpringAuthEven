package com.boots.dto;

import com.boots.entity.User;

public class RegistrationRequestDto {

    private String username;
    private String password;
    private String email;
    private String name;

    public User toUser() {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    // Геттеры и сеттеры
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
