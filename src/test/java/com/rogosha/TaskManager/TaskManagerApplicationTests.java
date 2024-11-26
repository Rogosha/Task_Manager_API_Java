package com.rogosha.TaskManager;

import com.rogosha.TaskManager.Models.DTOs.TaskDTO;
import com.rogosha.TaskManager.Models.Task;
import com.rogosha.TaskManager.Models.User;
import com.rogosha.TaskManager.Other.Priority;
import com.rogosha.TaskManager.Other.Status;
import com.rogosha.TaskManager.Repositories.TaskRepository;
import com.rogosha.TaskManager.Repositories.UserRepository;
import com.rogosha.TaskManager.Security.Roles;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.junit.jupiter.api.Assertions.*;
import java.security.Principal;
import java.util.Optional;


@SpringBootTest
class TaskManagerApplicationTests {

	@Autowired
	private TaskService taskService;

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	AuthenticationManager authenticationManager;

	private User adminUser;
	private User normalUser;

	@BeforeEach
	void setUp() {
		// Очистка базы перед каждым тестом
		taskRepository.deleteAll();
		userRepository.deleteAll();

		// Добавление пользователей
		adminUser = new User("admin@example.com");
		adminUser.setRole(Roles.ROLE_ADMIN);
		adminUser.setPassword(passwordEncoder.encode("password"));
		normalUser = new User("user@example.com");
		normalUser.setRole(Roles.ROLE_USER);
		normalUser.setPassword(passwordEncoder.encode("password"));

		userRepository.save(adminUser);
		userRepository.save(normalUser);
	}

	@Transactional
	@Test
	void testCreateTask_Success() {
		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setTitle("Test Task");
		taskDTO.setDescription("This is a test task");
		taskDTO.setStatus("ON_HOLD");
		taskDTO.setPriority("HIGH");
		taskDTO.setExecutor(normalUser.getId());

		Principal principal = () -> adminUser.getId();

		ResponseEntity<?> response = taskService.createTask(taskDTO, principal);

		assertEquals(200, response.getStatusCodeValue());
		Optional<Task> task = taskRepository.findById("Test Task");
		assertTrue(task.isPresent());
		assertEquals("This is a test task", task.get().getDescription());
		assertEquals(Status.ON_HOLD, task.get().getStatus());
		assertEquals(Priority.HIGH, task.get().getPriority());
		assertEquals(normalUser, task.get().getExecutor());
	}

	@Test
	void testCreateTask_ExistingTitle() {
		Task existingTask = new Task("Existing Task");
		existingTask.setAuthor(adminUser);
		taskRepository.save(existingTask);

		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setTitle("Existing Task");
		taskDTO.setDescription("Trying to create a task with an existing title");
		taskDTO.setStatus("ON_HOLD");
		taskDTO.setPriority("HIGH");
		taskDTO.setExecutor(normalUser.getId());

		Principal principal = () -> adminUser.getId();

		ResponseEntity<?> response = taskService.createTask(taskDTO, principal);

		assertEquals(403, response.getStatusCodeValue());
		assertEquals("TASK WITH THIS TITLE ALREADY EXISTS", response.getBody());
	}

	@Test
	void testEditTask_Success() {
		Task task = new Task("Task to Edit");
		task.setAuthor(adminUser);
		task.setExecutor(normalUser);
		task.setStatus(Status.ON_HOLD);
		taskRepository.save(task);

		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setTitle("Task to Edit");
		taskDTO.setDescription("Updated description");
		taskDTO.setStatus("COMPLETED");

		ResponseEntity<?> response = taskService.editTask(taskDTO);

		assertEquals(200, response.getStatusCodeValue());
		Optional<Task> updatedTask = taskRepository.findById("Task to Edit");
		assertTrue(updatedTask.isPresent());
		assertEquals("Updated description", updatedTask.get().getDescription());
		assertEquals(Status.COMPLETED, updatedTask.get().getStatus());
	}

	@Test
	void testDeleteTask_Success() {
		Task task = new Task("Task to Delete");
		task.setAuthor(adminUser);
		task.setExecutor(normalUser);
		taskRepository.save(task);

		TaskDTO taskDTO = new TaskDTO();
		taskDTO.setTitle("Task to Delete");

		Principal principal = () -> adminUser.getId();

		ResponseEntity<?> response = taskService.deleteTask(taskDTO, principal);

		assertEquals(200, response.getStatusCodeValue());
		assertFalse(taskRepository.existsById("Task to Delete"));
	}

	@Transactional
	@Test
	void testViewTasks() {
		Task task1 = new Task("Task 1");
		task1.setAuthor(adminUser);
		task1.setExecutor(normalUser);
		taskRepository.save(task1);

		Task task2 = new Task("Task 2");
		task2.setAuthor(adminUser);
		task2.setExecutor(normalUser);
		taskRepository.save(task2);

		ResponseEntity<?> response = taskService.viewTasks();

		assertEquals(200, response.getStatusCodeValue());
		assertTrue(response.getBody().toString().contains("Task 1"));
		assertTrue(response.getBody().toString().contains("Task 2"));
	}

}
