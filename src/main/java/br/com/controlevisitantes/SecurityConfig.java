package br.com.controlevisitantes;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Autowired
    private CaptchaFilter captchaFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Registra o filtro CAPTCHA antes do filtro de autenticação padrão
            .addFilterBefore(captchaFilter, UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")

                // Sucesso: limpa contador e vai para /menu
                .successHandler((request, response, authentication) -> {
                    HttpSession session = request.getSession(false);
                    if (session != null) loginAttemptService.loginSucceeded(session);
                    response.sendRedirect("/menu");
                })

                // Falha: incrementa contador e volta para /login?erro=true
                .failureHandler((request, response, exception) -> {
                    HttpSession session = request.getSession(true);
                    loginAttemptService.loginFailed(session);
                    response.sendRedirect("/login?erro=true");
                })

                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}
