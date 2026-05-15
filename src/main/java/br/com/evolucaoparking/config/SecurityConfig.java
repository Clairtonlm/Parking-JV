package br.com.evolucaoparking.config;

import br.com.evolucaoparking.security.TurnoAuthenticationSuccessHandler;
import br.com.evolucaoparking.security.TurnoLogoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final TurnoAuthenticationSuccessHandler successHandler;
    private final TurnoLogoutHandler turnoLogoutHandler;

    public SecurityConfig(
            TurnoAuthenticationSuccessHandler successHandler,
            TurnoLogoutHandler turnoLogoutHandler) {
        this.successHandler = successHandler;
        this.turnoLogoutHandler = turnoLogoutHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/login", "/error").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/operacao/**", "/recibo/**").hasAnyRole("ADMIN", "FUNCIONARIO")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(successHandler)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .addLogoutHandler(turnoLogoutHandler)
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
