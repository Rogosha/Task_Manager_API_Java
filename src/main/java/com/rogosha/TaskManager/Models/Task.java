package com.rogosha.TaskManager.Models;

import com.rogosha.TaskManager.Other.Priority;
import com.rogosha.TaskManager.Other.Status;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.aop.target.LazyInitTargetSource;

import java.util.Deque;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Task {

    @Id
    private String title;

    @Column(name = "discription")
    private String description;

    @Column(name = "status")
    private Status status;

    @Column(name = "priority")
    private Priority priority;

    @ManyToOne
    @JoinColumn(name = "author")
    private User author;

    @ManyToOne
    @JoinColumn(name = "executor")
    private User executor;

    @Column(name = "comments")
    private List<String> comments;

    public Task(String title) {
        this.title = title;
    }
}
