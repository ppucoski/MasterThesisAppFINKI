package vp.magisterski.config;

import vp.magisterski.model.enumerations.AppRole;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

public class AuthConfig {

    public HttpSecurity authorize(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers("/admin/*").hasAnyRole(
                                AppRole.PROFESSOR.name(),
                                AppRole.ADMIN.name()
                        )
//                        .requestMatchers("/admin/subject-requests/{professorId}/**").access(
//                                new WebExpressionAuthorizationManager("#professorId == authentication.name or hasRole('ROLE_ADMIN')")
//                        )
//                        .requestMatchers("/engagement/{professorId}/**").access(
//                                new WebExpressionAuthorizationManager("#professorId == authentication.name or hasRole('ROLE_ADMIN')")
//                        )
//                        .requestMatchers("/admin/**", "/api/**", "/build/**", "/engagement/init").hasAnyRole(
//                                AppRole.ADMIN.name()
//                        )
//                        .requestMatchers("/active-subjects", "io.png", "/allocation/*").permitAll()
                        .anyRequest().permitAll() //testing purposes
//                        .anyRequest().authenticated() //ACTUAL
                )
                .logout((logout) -> logout
                        .logoutUrl("/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessUrl("/")
                        .permitAll());
//                .logout(LogoutConfigurer::permitAll);
    }
}
