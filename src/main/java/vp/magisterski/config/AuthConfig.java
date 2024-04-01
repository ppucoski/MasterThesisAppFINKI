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
                                AppRole.ADMIN.name(),
                                AppRole.ACADEMIC_AFFAIR_VICE_DEAN.name(),
                                AppRole.SCIENCE_AND_COOPERATION_VICE_DEAN.name(),
                                AppRole.FINANCES_VICE_DEAN.name(),
                                AppRole.STUDENT_ADMINISTRATION.name(),
                                AppRole.ADMINISTRATION_MANAGER.name()

                        )
                        .anyRequest().authenticated()
                )
                .logout(LogoutConfigurer::permitAll);
    }

}
