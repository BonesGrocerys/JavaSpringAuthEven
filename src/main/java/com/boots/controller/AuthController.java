package com.boots.controller;

import com.boots.dto.AuthenticationRequestDto;
import com.boots.dto.Message;
import com.boots.dto.PrivilegeNameRequest;
import com.boots.dto.RegistrationRequestDto;
import com.boots.entity.User;
import com.boots.repository.UserRepository;
import com.boots.security.JwtTokenProvider;
import com.boots.security.JwtUserDetailsService;
import com.boots.service.UserService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    @Autowired

    private AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final JwtUserDetailsService jwtUserDetailsService;
    private UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserService userService, JwtUserDetailsService jwtUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.jwtUserDetailsService = jwtUserDetailsService;
    }

    @Autowired
    private PlatformTransactionManager transactionManager;
    @PostMapping("registration")
    public ResponseEntity registration(@RequestBody RegistrationRequestDto requestDto){
        try{
            User user = userService.findByUsername(requestDto.getUsername());
            if (user == null) {
                User registrationUser = requestDto.toUser();
                userService.register(registrationUser);
                userService.addPrivilegeToUser(registrationUser.getId(), userService.findIdPrivilegeByName("user"));
                return ResponseEntity.ok(registrationUser.getId());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Пользователь уже существует"));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Произошла ошибка"));
        }
    }

    @PostMapping("registration/result")
    public ResponseEntity registrationResult(@RequestBody String result){
        if (result.equals("false")) {
            // Логика для обработки значения "false"
            return ResponseEntity.ok("Значение false успешно обработано на сервере Java");
        } else if (result.equals("true")) {
            // Логика для обработки значения "true"
            return ResponseEntity.ok(" Регистрация прошла успешно");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Некорректное значение"));
        }
    }


    @PostMapping("login")
    public ResponseEntity<String> login(@RequestBody AuthenticationRequestDto requestDto){
        try{
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if(user == null){
                throw new UsernameNotFoundException("User not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getPrivileges());

            Map<Object, Object> response = new HashMap<>();
//            response.put("userId", user.getId());
//            response.put("username", username);
            response.put("token", token);


            return ResponseEntity.ok(token);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @GetMapping("getUserId")
    public Long getUserIdFromToken(@RequestHeader("token") String token) {
        try {
            String username = jwtTokenProvider.getUsername(token);
            Long userId = userService.findUserIdByUsername(username);
            return userId;
        } catch (JwtException e) {
            throw new JwtException("JWT token is expired or invalid");
        }
    }

    @PostMapping("CheckPrivilege")
    public ResponseEntity<Boolean> checkPrivilege(@RequestHeader("token") String token,@RequestBody PrivilegeNameRequest privilegeNameRequest ) {
        try {
            String privilegeName = privilegeNameRequest.getPrivilegeName();
            List<String> privileges = jwtTokenProvider.getPrivilegesFromToken(token);
            boolean containsPrivilege = privileges.stream()
                    .anyMatch(privilege -> privilege.equals(privilegeName));

            return ResponseEntity.ok(containsPrivilege);
        }
        catch (RuntimeException e) {
            return new ResponseEntity(new Message("Произошла ошибка"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("getUsername")
    public String getUsernameFromToken(@RequestHeader("token") String token) {
        String username = jwtTokenProvider.getUsernameFromToken(token);
        return username;
    }

    @GetMapping("getNameFromToken")
    public String getNameFromToken(@RequestHeader("token") String token) {
        String username = jwtTokenProvider.getUsername(token);
        Long userId = userService.findUserIdByUsername(username);
        User user = userService.findUserById(userId);
        String name = user.getName();
        return name;
    }

    @GetMapping("validateToken")
    public boolean validateToken(@RequestHeader("token") String token) {
        try {
            boolean isValid = jwtTokenProvider.validateToken(token);
            return isValid;
        } catch (JwtException e) {
            throw new JwtException("JWT token is expired or invalid");
        }
    }

}