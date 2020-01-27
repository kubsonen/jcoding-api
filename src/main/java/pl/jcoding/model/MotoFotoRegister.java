package pl.jcoding.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class MotoFotoRegister {
    private String name;
    @Email
    private String email;
    @NotBlank
    @Size(min = 8)
    private String username;
    @NotBlank
    @Size(min = 5)
    private String password;
    @NotBlank
    @Size(min = 5)
    private String passwordAgain;
}
