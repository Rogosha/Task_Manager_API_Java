package com.rogosha.TaskManager.Models.DTOs;

import com.rogosha.TaskManager.Other.Priority;
import com.rogosha.TaskManager.Other.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Task entity")
@Data
public class TaskDTO {

    @Schema(description = "Task title", example = "task1")
    private String title;

    @Schema(description = "Task description", example = "cool task")
    private String description;

    @Schema(description = "Task status", example = "ON_HOLD, IN_PROCESS, COMPLETE")
    private String status;

    @Schema(description = "Task title", example = "LOW, MEDIUM, HIGH")
    private String priority;

    @Schema(description = "Author's email", example = "admin@email.ru")
    private String author;

    @Schema(description = "Executor's email", example = "executor@email.ru")
    private String executor;

    @Schema(description = "Comment for task", example = "good job")
    private String comment;
}
