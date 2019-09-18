package pl.jcoding.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.jcoding.util.ConverterStringCollection;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Entity
@Table(name = "api_user")
public class User extends CommonEntity implements UserDetails {

    @Column(unique = true)
    private String username;

    private String password;

    @Convert(converter = ConverterStringCollection.class)
    private List<String> userAuthorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userAuthorities.stream().map(s -> (GrantedAuthority) () -> s).collect(Collectors.toList());
    }

    public void addAuthority(String... role) {
        if (userAuthorities == null) userAuthorities = new ArrayList<>();
        userAuthorities.addAll(Arrays.asList(role));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
