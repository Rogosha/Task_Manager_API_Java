package com.rogosha.TaskManager;

import com.rogosha.TaskManager.Models.DTOs.TaskDTO;
import com.rogosha.TaskManager.Models.Task;
import com.rogosha.TaskManager.Models.User;
import com.rogosha.TaskManager.Other.Priority;
import com.rogosha.TaskManager.Other.Status;
import com.rogosha.TaskManager.Repositories.TaskRepository;
import com.rogosha.TaskManager.Repositories.UserRepository;
import com.rogosha.TaskManager.Security.Roles;
import io.jsonwebtoken.security.Jwks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaskRepository taskRepository;

    public ResponseEntity<?> createTask(@RequestBody TaskDTO newTask, Principal principal) {

        if (newTask.getTitle() != null) {
            try {
                Task task = taskRepository.findById(newTask.getTitle()).orElseThrow();
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("TASK WITH THIS TITLE ALREADY EXISTS");
            } catch (Exception e) {
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT VALUE FOR TITLE");
        }

        Task task = new Task(newTask.getTitle());
        if (newTask.getDescription() != null) {
            task.setDescription(newTask.getDescription());
        }

        if (newTask.getStatus() != null) {
            try {
                task.setStatus(Status.valueOf(newTask.getStatus()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INCORRECT VALUE FOR STATUS");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT VALUE FOR STATUS");
        }

        if (newTask.getPriority() != null) {
            try {
                task.setPriority(Priority.valueOf(newTask.getPriority()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INCORRECT VALUE FOR PRIORITY");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT VALUE FOR PRIORITY");
        }


        if (newTask.getAuthor() != null) {
            Optional<User> author = userRepository.findById(newTask.getAuthor());
            if (author.isPresent() && author.orElseThrow().getRole().equals(Roles.ROLE_ADMIN)) {
                task.setAuthor(author.orElseThrow());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AUTHOR NOT FOUND");
            }
        } else {
            task.setAuthor(userRepository.findById(principal.getName()).orElseThrow());
        }

        if (newTask.getExecutor() != null) {
            Optional<User> executor = userRepository.findById(newTask.getExecutor());
            if (executor.isPresent() && executor.orElseThrow().getRole().equals(Roles.ROLE_USER)) {
                task.setExecutor(executor.orElseThrow());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EXECUTOR NOT FOUND");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT VALUE FOR EXECUTOR");
        }

        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body("SUCCESSFUL CREATE NEW TASK");
    }

    public ResponseEntity<?> editTask(@RequestBody TaskDTO newTask) {

        Task task = taskRepository.findById(newTask.getTitle()).orElseThrow();

        if (newTask.getDescription() != null) {
            task.setDescription(newTask.getDescription());
        }

        if (newTask.getStatus() != null) {
            try {
                task.setStatus(Status.valueOf(newTask.getStatus()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INCORRECT VALUE FOR STATUS");
            }
        }

        if (newTask.getPriority() != null) {
            try {
                task.setPriority(Priority.valueOf(newTask.getPriority()));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INCORRECT VALUE FOR PRIORITY");
            }
        }

        if (newTask.getAuthor() != null) {
            Optional<User> author = userRepository.findById(newTask.getAuthor());
            if (author.isPresent() && author.orElseThrow().getRole().equals(Roles.ROLE_ADMIN)) {
                task.setAuthor(author.orElseThrow());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("AUTHOR NOT FOUND");
            }
        }

        if (newTask.getExecutor() != null) {
            Optional<User> executor = userRepository.findById(newTask.getExecutor());
            if (executor.isPresent() && executor.orElseThrow().getRole().equals(Roles.ROLE_USER)) {
                task.setExecutor(executor.orElseThrow());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("EXECUTOR NOT FOUND");
            }
        }

        taskRepository.save(task);

        return ResponseEntity.status(HttpStatus.OK).body("SUCCESSFUL TASK EDIT");
    }

    public ResponseEntity<?> viewTasks() {
        return ResponseEntity.status(HttpStatus.OK).body(taskRepository.findAll());
    }

    public ResponseEntity<?> viewMyTasks(Principal principal) {

        User currentUser = userRepository.findById(principal.getName()).orElseThrow();

        Iterable<Task> myTasks;

        if (currentUser.getRole().equals(Roles.ROLE_ADMIN)) {
            myTasks = taskRepository.findByAuthor(currentUser);
        } else if (currentUser.getRole().equals(Roles.ROLE_USER)) {
            myTasks = taskRepository.findByExecutor(currentUser);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT FOUND CURRENT USER");
        
        return ResponseEntity.status(HttpStatus.OK).body(myTasks);
    }

    public ResponseEntity<?> deleteTask(@RequestBody TaskDTO taskDTO, Principal principal) {
        if (taskDTO.getTitle() != null) {
            Optional<Task> taskOptional = taskRepository.findById(taskDTO.getTitle());
            if (taskOptional.isPresent()) {
                taskRepository.delete(taskOptional.orElseThrow());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TASK NOT FOUND");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT VALUE FOR TITLE");
        }
        return ResponseEntity.status(HttpStatus.OK).body("TASK SUCCESSFUL DELETED");
    }

    public ResponseEntity<?> commentOrChangeStatusTask(@RequestBody TaskDTO taskDTO, Principal principal) {

        User currentUser = userRepository.findById(principal.getName()).orElseThrow();

        if (taskDTO.getTitle() != null) {
            Optional<Task> taskOptional = taskRepository.findById(taskDTO.getTitle());
            if (taskOptional.isPresent()) {
                Task task = taskOptional.orElseThrow();
                if ((currentUser.getRole() == Roles.ROLE_ADMIN) || (currentUser.getId().equals(task.getExecutor().getId()))) {
                    if (taskDTO.getComment() != null) {
                        if (task.getComments() == null){
                            task.setComments(new ArrayList<String>());
                        } else {
                            task.getComments().add(taskDTO.getComment());
                        }
                    }
                    if (taskDTO.getStatus() != null) {
                        try {
                            task.setStatus(Status.valueOf(taskDTO.getStatus()));
                        } catch (IllegalArgumentException e) {
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("INCORRECT VALUE FOR STATUS");
                        }
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("FORBIDDEN ACCESS");
                }

                taskRepository.save(task);

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("TASK NOT FOUND");
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("NOT VALUE FOR TITLE");
        }

        return ResponseEntity.status(HttpStatus.OK).body("SUCCESSFUL CHANGED");
    }


}
