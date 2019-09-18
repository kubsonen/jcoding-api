package pl.jcoding.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.jcoding.entity.User;

import java.util.Date;
import java.util.Optional;

@Component
public class TokenUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expirationTime;

    public String generateToken(User user) {

        if (user == null || user.getId() == null || user.getId() == 0)
            return "";

        Long userId = user.getId();
        Date now = new Date();

        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

    }

    public Optional<Long> getUserIdFromToken(String token) {

        try {
            String userId =
                    Jwts.parser()
                            .setSigningKey(secret)
                            .parseClaimsJws(token)
                            .getBody()
                            .getSubject();

            return Optional.of(Long.valueOf(userId));

        } catch (SignatureException |
                MalformedJwtException |
                ExpiredJwtException |
                UnsupportedJwtException |
                IllegalArgumentException ex) {

            ex.printStackTrace();
            return Optional.empty();

        }

    }


}
