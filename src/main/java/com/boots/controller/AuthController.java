package com.boots.controller;

import com.boots.dto.*;
import com.boots.entity.User;
import com.boots.repository.UserRepository;
import com.boots.security.JwtTokenProvider;
import com.boots.security.JwtUserDetailsService;
import com.boots.service.UserService;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.List;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/auth/")
public class AuthController {

    @Autowired
    private JavaMailSender emailSender;
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
                String email = registrationUser.getEmail(); // email
                String confirmCode = registrationUser.getConfirmCode(); // code
                sendEmail(email, confirmCode);
                userService.register(registrationUser);
                userService.addPrivilegeToUser(registrationUser.getId(), userService.findIdPrivilegeByName("user"));
                return ResponseEntity.ok(registrationUser.getId());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Пользователь уже существует"));

        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Message("Произошла ошибка"));
        }
    }

    private void sendEmail(String email, String confirmCode) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Код подтверждения");
            helper.setText(confirmCode);
            // Устанавливаем адрес отправителя (можно использовать адрес получателя)
            helper.setFrom("mrgllebb@yandex.ru");
            emailSender.send(message);
        } catch (MessagingException | MailException e) {
            System.err.println("Ошибка отправки письма: " + e.getMessage());
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


//    @PostMapping("login")
//    public ResponseEntity<String> login(@RequestBody AuthenticationRequestDto requestDto){
//        try{
//            String username = requestDto.getUsername();
//            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
//            User user = userService.findByUsername(username);
//
//            if(user == null){
//                throw new UsernameNotFoundException("User not found");
//            }
//
//            String token = jwtTokenProvider.createToken(username, user.getPrivileges());
//
//            Map<Object, Object> response = new HashMap<>();
////            response.put("userId", user.getId());
////            response.put("username", username);
//            response.put("token", token);
//
//
//            return ResponseEntity.ok(token);
//
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Произошла ошибка");
//        }
//    }

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequestDto requestDto){
        try{
            String username = requestDto.getUsername();
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, requestDto.getPassword()));
            User user = userService.findByUsername(username);

            if(user == null){
                throw new UsernameNotFoundException("User not found");
            }

            String token = jwtTokenProvider.createToken(username, user.getPrivileges());

            // Получение статуса подтверждения
            boolean confirmStatus = user.getConfirmStatus(); // Предположим, что у вас есть метод, возвращающий статус подтверждения

            // Создание объекта DTO
            AuthenticationResponseDto responseDto = new AuthenticationResponseDto(token, confirmStatus);

            // Возвращаем объект DTO вместо строки
            return ResponseEntity.ok(responseDto);


        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Произошла ошибка");
        }
    }

    @PostMapping("confirmAccount")
    public ResponseEntity confirmAccount(@RequestHeader("token") String token, @RequestBody ConfirmCodeRequest confirmCodeRequest){
        try {
            String username = jwtTokenProvider.getUsername(token);
            Long userId = userService.findUserIdByUsername(username);
            String confirmCodeUser = userService.getConfirmCode(userId);
            int codeUser = Integer.parseInt(confirmCodeUser);
            int code = Integer.parseInt(confirmCodeRequest.getConfirmCode());
            if (code == codeUser) { // Сравнение строк
                userService.setConfirmStatusTrue(userId);
                return ResponseEntity.ok("Аккаунт успешно подтверждён!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный код подтверждения");
            }

        } catch (JwtException e) {
            throw new JwtException("Error");
        }
    }

    @PostMapping("resetPassword")
    public ResponseEntity resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){
        try {

            User user = userService.findByUsername(resetPasswordRequest.getUsername());
            if ( user == null) {
                throw new JwtException("Пользователь с именем " + resetPasswordRequest.getUsername() + " не найден");
            }
            String confirmPasswordCode = generatePasswordCode(); // code
            userService.addPasswordCode(resetPasswordRequest.getUsername(), confirmPasswordCode);
            String email = user.getEmail();
            sendEmail(email, confirmPasswordCode);
                return ResponseEntity.ok("Код восстановления отправлен на почту");
        } catch (JwtException e) {
            throw new JwtException("Error");
        }
    }

    private String generatePasswordCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    @PostMapping("changePassword")
    public ResponseEntity changePassword(@RequestBody UpdatePasswordResuestDto updatePasswordResuestDto){
        try {
            Long userId = userService.findUserIdByUsername(updatePasswordResuestDto.getUsername());
            String confirmCodeUser = userService.getConfirmPasswordCode(userId);
            int codeUser = Integer.parseInt(confirmCodeUser);
            int code = Integer.parseInt(updatePasswordResuestDto.getConfirmPasswordCode());
            if (code == codeUser) { // Сравнение строк
                userService.updatePassword(updatePasswordResuestDto.getUsername(), updatePasswordResuestDto.getPassword());
                return ResponseEntity.ok("Пароль успешно изменён!");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Неверный код подтверждения");
            }

        } catch (JwtException e) {
            throw new JwtException("Error");
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