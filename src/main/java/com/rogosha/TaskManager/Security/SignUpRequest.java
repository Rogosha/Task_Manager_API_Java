package com.rogosha.TaskManager.Security;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Sign up request")
public class SignUpRequest {

    @Schema(description = "User's email", example = "user@email.ru")
    private String email;

    @Schema(description = "User's password", example = "passpass123")
    private String password;

    @Schema(description = "User's role", example = "ROLE_USER, ROLE_ADMIN")
    private Roles role;

}
