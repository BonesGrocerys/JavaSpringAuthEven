package com.boots.security;


import com.boots.entity.Privileges;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {


    @Value("${jwt.token.secret}")
    private String secret;

    @Value("${jwt.token.expired}")
    private long validityInMilliseconds;

    private UserDetailsService userDetailsService;

    @Autowired
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;


    }
    @Value("${jwt.secret}")
    private String jwtSecret;
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(secret.getBytes());
    }

    public String createToken(String username, List<Privileges> userPrivelege) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("privileges", getPrivelegeNames(userPrivelege));

        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }



//    public Authentication getAuthentication(String token) {
//        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUsername(token));
//        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
//    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(claims.getSubject());
        List<GrantedAuthority> authorities = getAuthoritiesFromClaims(claims);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    private List<GrantedAuthority> getAuthoritiesFromClaims(Claims claims) {
        List<String> privileges = (List<String>) claims.get("privileges");
        return privileges.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String getUsernameFromToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            String subject = claims.getBody().getSubject();
            String username = subject;
            return username;
        } catch (JwtException e) {
            e.printStackTrace();
            throw new RuntimeException("Ошибка при парсинге токена");
        }
    }


    public List<String> getPrivilegesFromToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

        Claims body = claims.getBody();
        List<String> privileges = body.get("privileges", List.class);

        return privileges;
    }

    public String getUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);

            if (claims.getBody().getExpiration().before(new Date())) {
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("JWT token is expired or invalid");
        }
    }

    private List<String> getPrivelegeNames(List<Privileges> userPrivelege) {
        List<String> result = new ArrayList<>();

        userPrivelege.forEach(privileges -> {
            result.add(privileges.getName());
        });

        return result;
    }
}
