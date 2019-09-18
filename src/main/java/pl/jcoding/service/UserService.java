package pl.jcoding.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.jcoding.entity.User;
import pl.jcoding.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private static final String ROLE_SEPARATOR = ",";

    @Value("${spring.security.user.name}")
    private String defaultUsername;

    @Value("${spring.security.user.password}")
    private String defaultPassword;

    @Value("${spring.security.user.roles}")
    private String defaultRoles;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        this.createUserIfNotExists();
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    private void createUserIfNotExists() {
        userRepository.deleteAll();
        userRepository.findByUsername(defaultUsername).orElseGet(() -> {
            User user = new User();
            user.setUsername(defaultUsername);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.addAuthority(defaultRoles.split(ROLE_SEPARATOR));
            return userRepository.save(user);
        });

    }

}
