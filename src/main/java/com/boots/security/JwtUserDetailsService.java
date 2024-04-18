package com.boots.security;

import com.boots.entity.User;
import com.boots.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public JwtUserDetailsService(UserService userService) {
        this.userService = userService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userService.findByUsername(username);

        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }

        List<GrantedAuthority> authorities = user.getPrivileges().stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }
}

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        User user = userService.findByUsername(username);
//
//        if(user == null){
//            throw new UsernameNotFoundException("User not found");
//        }
//
//        List<GrantedAuthority> authorities = user.getPrivileges().stream()
//                .map(privilege -> new SimpleGrantedAuthority(privilege.getName()))
//                .collect(Collectors.toList());
//
//        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
//    }