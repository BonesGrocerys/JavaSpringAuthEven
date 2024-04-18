package com.boots.dto;

import javax.validation.constraints.NotBlank;

public class AuthenticationRequestDto {
    @NotBlank(message = "Логин пользователя не может быть пустым")
    private String username;
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
}
