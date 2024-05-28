package com.boots.dto;

public class UpdatePasswordResuestDto {

    private String username;
    private String password;
    private String confirmPasswordCode;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPasswordCode() {
        return confirmPasswordCode;
    }

    public void setConfirmPasswordCode(String confirmPasswordCode) {
        this.confirmPasswordCode = confirmPasswordCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
