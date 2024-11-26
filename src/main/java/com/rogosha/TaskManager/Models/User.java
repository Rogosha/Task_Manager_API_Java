package com.rogosha.TaskManager.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rogosha.TaskManager.Models.UserKey;
import com.rogosha.TaskManager.Security.Roles;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Column(name = "password", nullable = false)
    @JsonIgnore
    private String password;

    @Column(name = "role", nullable = false)
    private Roles role;

    @OneToMany(mappedBy = "author")
    @JsonIgnore
    private List<Task> authors;

    @OneToMany(mappedBy = "executor")
    @JsonIgnore
    private List<Task> executors;

    public User(String id) {
        this.id = id;
    }
}
