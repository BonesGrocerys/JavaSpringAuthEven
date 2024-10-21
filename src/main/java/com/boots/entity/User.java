package com.boots.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "confirm_status")
    private Boolean confirm_status;
    @Column(name = "confirm_code")
    private String confirm_code;
    @Column(name = "password_code")
    private String password_code;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_privileges",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private List<Privileges> privileges = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DateEvent> dateEvents;

    public void addPrivilege(Privileges privilege) {
        this.privileges.add(privilege);
        privilege.getUsers().add(this);
    }

    public void removePrivilege(Privileges privilege) {
        this.privileges.remove(privilege);
        privilege.getUsers().remove(this);
    }
    public List<Privileges> getPrivileges() {
        return privileges;
    }
    // get & set
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUserName(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Boolean getConfirmStatus() {
        return confirm_status;
    }

    public void setConfirmStatus(Boolean confirm_status) {
        this.confirm_status = confirm_status;
    }

    public String getConfirmCode() {
        return confirm_code;
    }

    public void setConfirmCode(String confirm_code) {
        this.confirm_code = confirm_code;
    }

    public String getPasswordCode() {
        return password_code;
    }

    public void setPasswordCode(String password_code) {
        this.password_code = password_code;
    }
}
