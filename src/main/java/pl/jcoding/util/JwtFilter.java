package pl.jcoding.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.jcoding.service.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String BEARER_AUTHORIZATION = "Bearer";
    private static final String BEARER_SPACE = " ";

    @Autowired
    private UserService userService;

    @Autowired
    private TokenUtil tokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        final String tokenHeader = httpServletRequest.getHeader(HEADER_AUTHORIZATION);

        if (tokenHeader != null && tokenHeader.startsWith(BEARER_AUTHORIZATION + BEARER_SPACE)) {

            final String jwtToken = tokenHeader.substring((BEARER_AUTHORIZATION + BEARER_SPACE).length());
            Optional<Long> userIdOptional = tokenUtil.getUserIdFromToken(jwtToken);
            userIdOptional.ifPresent(id -> {

                if (SecurityContextHolder.getContext().getAuthentication() == null) {
                    this.userService.getById(id).ifPresent(user -> {

                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

                    });
                }

            });

        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);

    }

}
