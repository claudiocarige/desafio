package br.com.claudiocarige.desafio.configuration;

import br.com.claudiocarige.desafio.adapter.in.web.controllers.security.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${app.security.user.name}")
    private String userName;

    @Value("${app.security.user.password}")
    private String userPassword;

    @Value("${app.security.admin.name}")
    private String adminName;

    @Value("${app.security.admin.password}")
    private String adminPassword;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    public SecurityConfig(CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        var user = User.withUsername(userName)
                .password(encoder.encode(userPassword))
                .roles("USER")
                .build();

        var admin = User.withUsername(adminName)
                .password(encoder.encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/customers/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/customers/update/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/customers/delete/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/customers/**", "/customers").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(basic -> {
                });

        return http.build();
    }
}
