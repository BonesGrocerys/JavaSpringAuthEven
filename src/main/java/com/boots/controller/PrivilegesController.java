package com.boots.controller;
import com.boots.dto.PrivelegeRequest;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/privileges/")
public class PrivilegesController {
    @Autowired

    private final UserService userService;
    public PrivilegesController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAuthority('admin')")
    @PostMapping("addPrivilege")
    public ResponseEntity<String> addPrivilegeToUser(@Valid @RequestBody PrivelegeRequest privelegeRequest) {
        userService.addPrivilegeToUser(privelegeRequest.getUserId(), privelegeRequest.getPrivilegeId());
        return ResponseEntity.ok("Privilege added to user successfully");
    }

}
