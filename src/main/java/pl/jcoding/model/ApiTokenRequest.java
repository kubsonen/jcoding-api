package pl.jcoding.model;

import lombok.Data;

@Data
public class ApiTokenRequest {
    private String username;
    private String password;
}
