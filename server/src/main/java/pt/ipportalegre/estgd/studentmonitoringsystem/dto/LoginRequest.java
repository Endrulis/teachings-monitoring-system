package pt.ipportalegre.estgd.studentmonitoringsystem.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {

    @NotNull(message = "Email cannot be null")
    @NotBlank(message = "Email ID is mandatory")
    private String email;
    @NotNull(message = "Full name cannot be null")
    @NotBlank(message = "Full name is mandatory")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    //@Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_\\-!?])(?=\\S+$).*$",
          //  message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, one special character, and no whitespace")
    private String password;
}
