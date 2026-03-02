package itqGroupTestApp.core.DTO;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserDTO {
    private Long id;

    @NotBlank
    private String username;

    @Email
    private String email;

    private String position;
}
