package com.javacode.learn.configuration;

import com.javacode.learn.service.SocialAppService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final SocialAppService socialAppService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/app/", "/app/login", "/app/error", "/app/webjars/**")
                        .permitAll() // Разрешаем доступ к этим маршрутам всем
                        .requestMatchers("/app/h2-console/*").permitAll()
                        .requestMatchers("/app/admin/**").hasRole("ADMIN") // Только для администраторов
                        .anyRequest().authenticated() // Все остальные запросы требуют аутентификации
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new HttpStatusEntryPoint(
                                HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response,
                                              accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value()); // Обработка 403
                            response.getWriter().write("Access Denied: You don't have permission to access this resource.");// Обработка ошибок аутентификации
                        })
                )
                .oauth2Login(oauth2Login -> oauth2Login
                        .loginPage("/app/") // Указываем страницу для входа
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(socialAppService))
                        .defaultSuccessUrl("/app/user")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL для выхода
                        .logoutSuccessHandler(oidcLogoutSuccessHandler()) // Обработчик для логаута
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public OidcLogoutSuccessHandler oidcLogoutSuccessHandler() {
        return new OidcLogoutSuccessHandler();
    }
}
