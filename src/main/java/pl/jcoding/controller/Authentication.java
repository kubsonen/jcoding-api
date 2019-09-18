package pl.jcoding.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.jcoding.entity.User;
import pl.jcoding.model.TokenRequest;
import pl.jcoding.model.TokenResponse;
import pl.jcoding.service.UserService;
import pl.jcoding.util.TokenUtil;

@RestController
@RequestMapping("/auth-api")
public class Authentication {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @CrossOrigin("*")
    @PostMapping("/authenticate")
    public TokenResponse authenticate(@RequestBody TokenRequest tokenRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(tokenRequest.getUsername(), tokenRequest.getPassword()));
            User user = userService.loadUserByUsername(tokenRequest.getUsername());
            return new TokenResponse(tokenUtil.generateToken(user));
        } catch (Throwable t) {
            t.printStackTrace();
            throw t;
        }
    }

}
