package com.boots.dto;

public class AuthenticationResponseDto {
    private String token;
    private boolean confirmStatus;

    public AuthenticationResponseDto(String token, boolean confirmStatus) {
        this.token = token;
        this.confirmStatus = confirmStatus;
    }

    public String getToken() {
        return token;
    }

    public boolean isConfirmStatus() {
        return confirmStatus;
    }
}
