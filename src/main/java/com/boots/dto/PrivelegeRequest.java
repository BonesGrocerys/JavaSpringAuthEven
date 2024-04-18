package com.boots.dto;

public class PrivelegeRequest {
    private Long userId;
    private Long privilegeId;

    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public Long getUserId() {
        return userId;
    }
    public void setPrivilegeId(Long privilegeId) {
        this.privilegeId = privilegeId;
    }
    public Long getPrivilegeId() {
        return privilegeId;
    }
}
