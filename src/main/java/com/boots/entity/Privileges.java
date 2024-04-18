package com.boots.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "privileges")
@Data
public class Privileges {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @ManyToMany(mappedBy = "privileges")
    private List<User> users = new ArrayList<>();

    public String getName() {
        return name;
    }
}
