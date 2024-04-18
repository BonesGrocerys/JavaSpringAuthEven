package com.boots.controller;

import com.boots.dto.updateUserRequestDto;
import com.boots.repository.UserRepository;
import com.boots.security.JwtTokenProvider;
import com.boots.security.JwtUserDetailsService;
import com.boots.service.UserService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequestMapping("/api/v1/user/")
public class UserController {

    @Autowired

    private AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private UserRepository userRepository;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, JwtUserDetailsService jwtUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

//    @PreAuthorize("hasAuthority('user')")
    @PutMapping("UpdateUserName")
    public ResponseEntity UpdateUserName(@Valid @RequestBody updateUserRequestDto updateUserRequestDto) {
        try {
            userService.updateUserName(updateUserRequestDto.getUserId(), updateUserRequestDto.getNewName());
            return ResponseEntity.ok("Success");
        } catch (JwtException e) {
            throw new JwtException("Error");
        }
    }
}
