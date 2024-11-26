package com.rogosha.TaskManager.Controllers;


import com.rogosha.TaskManager.Models.DTOs.TaskDTO;
import com.rogosha.TaskManager.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Tag(name = "User controller", description = "Controller for executors.")
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    TaskService taskService;

    @SecurityRequirement(name = "JWT")
    @GetMapping("/tasks")
    @Operation(summary = "Get all tasks", description = "Obtaining a list of tasks available to the user")
    ResponseEntity<?> getMyTasks(Principal principal) {
        return taskService.viewMyTasks(principal);
    }

    @SecurityRequirement(name = "JWT")
    @PutMapping("/tasks/comment")
    @Operation(summary = "Сomment on the task", description = "Allows the user to comment on an available task")
    ResponseEntity<?> commentTask(@Parameter(description = "Task's title with comment") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.commentOrChangeStatusTask(taskDTO, principal);
    }

    @SecurityRequirement(name = "JWT")
    @PutMapping("/tasks/status")
    @Operation(summary = "Change status on the task", description = "Allows the user change status on an available task")
    ResponseEntity<?> changeStatusTask(@Parameter(description = "Task's title with new status") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.commentOrChangeStatusTask(taskDTO, principal);
    }
}
