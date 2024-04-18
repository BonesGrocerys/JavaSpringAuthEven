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
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_privileges",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private List<Privileges> privileges = new ArrayList<>();


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
}
