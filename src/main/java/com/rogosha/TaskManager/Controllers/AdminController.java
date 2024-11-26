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

@Tag(name = "Admin controller", description = "Controller for authors of examples. Performs task management")
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    TaskService taskService;

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Create a task", description = "Allows to create a task.")
    @PostMapping("/tasks")
    ResponseEntity<?> postTask(@Parameter(description = "New task with all parameters") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.createTask(taskDTO, principal);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Edit a task", description = "Allows to edit a task.")
    @PutMapping("/tasks")
    ResponseEntity<?> editTask(@Parameter(description = "Edited task with current parameters") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.editTask(taskDTO);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get all tasks", description = "Allows to get all the tasks in the system")
    @GetMapping("/tasks")
    ResponseEntity<?> getAllTasks(Principal principal) {
        return taskService.viewTasks();
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Get my tasks", description = "Allows you to get all tasks where the user is specified as the author")
    @GetMapping("/tasks/my")
    ResponseEntity<?> getMyTasks(Principal principal) {
        return taskService.viewMyTasks(principal);
    }

    @SecurityRequirement(name = "JWT")
    @Operation(summary = "Delete a task", description = "Allows to delete a task by title")
    @DeleteMapping("/tasks")
    ResponseEntity<?> deleteTask(@Parameter(description = "Tasks's title") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.deleteTask(taskDTO, principal);
    }

    @SecurityRequirement(name = "JWT")
    @PutMapping("/tasks/comment")
    @Operation(summary = "Сomment on the task", description = "Allows the admin to comment all the tasks in the system")
    ResponseEntity<?> commentTask(@Parameter(description = "Task's title and comment") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.commentOrChangeStatusTask(taskDTO, principal);
    }

    @SecurityRequirement(name = "JWT")
    @PutMapping("/tasks/status")
    @Operation(summary = "Change status on the task", description = "Allows the admin to change status all the tasks in the system")
    ResponseEntity<?> changeStatusTask(@Parameter(description = "Task's title and new status") @RequestBody TaskDTO taskDTO, Principal principal) {
        return taskService.commentOrChangeStatusTask(taskDTO, principal);
    }

}
