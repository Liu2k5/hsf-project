package com.hsf.hsf_project.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).{8,}$",
            message = "Password must have at least 8 characters, one uppercase letter, and one number"
    )
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    // ✅ Custom helper
    public boolean passwordsMatch() {
        return password != null && password.equals(confirmPassword);
    }
}
