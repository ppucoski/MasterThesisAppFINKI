package vp.magisterski.config;

import vp.magisterski.model.enumerations.AppRole;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;


public class AuthConfig {

    public HttpSecurity authorize(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.OPTIONS).permitAll()
                        .requestMatchers(
                                "/admin/**").hasAnyRole(
                                AppRole.PROFESSOR.name(),
                                AppRole.ADMIN.name()
                        )
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);
    }

}
