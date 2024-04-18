package com.boots.security;

import com.boots.entity.Privileges;
import com.boots.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JwtUserFactory {

    public JwtUserFactory() {
    }


    public static JwtUser create(User user) {
        return new JwtUser(
                user.getId(),
                user.getName(),
                user.getPassword(),
                mapToGrantedAuthorities(new ArrayList<>(user.getPrivileges()))
        );
    }

    private static List<GrantedAuthority> mapToGrantedAuthorities(List<Privileges> userRoles) {
        return userRoles.stream()
                .map(privilege ->
                        new SimpleGrantedAuthority(privilege.getName())
                ).collect(Collectors.toList());
    }
}
//    public static JwtUser create(User user) {
//        Collection<? extends GrantedAuthority> authorities = Collections.emptyList(); // Пустая коллекция ролей
//        return new JwtUser(
//                user.getId(),
//                user.getUsername(),
//                user.getPassword(),
//                authorities
//        );
//    }