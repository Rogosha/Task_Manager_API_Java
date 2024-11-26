package com.rogosha.TaskManager.Repositories;

import com.rogosha.TaskManager.Models.Task;
import com.rogosha.TaskManager.Models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends CrudRepository<Task, String> {
    Iterable<Task> findByAuthor (User author);
    Iterable<Task> findByExecutor (User executor);
}
