package com.boots.dto;

import com.boots.entity.User;

import java.util.Random;

public class RegistrationRequestDto {

    private String username;
    private String password;
    private String email;
    private String name;
    private Boolean confirm_status = false;
    private String confirm_code;

    public User toUser() {
        User user = new User();
        user.setUserName(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setName(name);
        user.setConfirmStatus(confirm_status);
        user.setConfirmCode(generateConfirmCode());
        return user;
    }

    private String generateConfirmCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
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

    public Boolean getConfirmStatus() {
        return confirm_status;
    }

    public void setConfirmStatus(Boolean confirmStatus) {
        this.confirm_status = confirmStatus;
    }

    public String getConfirmCode() {
        return confirm_code;
    }

    public void setConfirmCode(String confirmCode) {
        this.confirm_code = confirmCode;
    }
}
