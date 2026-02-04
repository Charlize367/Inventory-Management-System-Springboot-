package org.example.Security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.Entities.Users;
import org.example.Repository.UsersRepository;
import org.example.Services.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.jwt.secret}")
    private String jwtSecret;


    private final UserService userService;


    public SecurityConfig(@Lazy UserService userService) {
        this.userService = userService;

    }

    @Bean
    CommandLineRunner init(UsersRepository usersRepository, PasswordEncoder encoder) {
        return args -> {
            Users existingAdmin = usersRepository.findByEmail("ims_admin@gmail.com");
            if (existingAdmin == null) {
                Users admin = new Users();
                admin.setUsername("Admin");
                admin.setEmail("ims_admin@gmail.com");
                admin.setPassword(encoder.encode("admin-ims-123"));
                admin.setEnabled(true);
                admin.setRole(Users.Role.valueOf("ADMIN"));
                usersRepository.save(admin);
                System.out.println("✅ Admin user created successfully.");
            } else {
                System.out.println("ℹ️ Admin user already exists. Skipping creation.");
            }
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return authenticationConverter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
     public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        return NimbusJwtDecoder.withSecretKey(key).build();
    }




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/images/**", "/actuator/**", "/api/v1/auth/login", "/api/v1/users").permitAll()
                        .requestMatchers("/api/v1/categories/**", "/api/v1/customers/**", "/api/v1/products/**", "/api/v1/purchases/**", "/api/v1/sales/**", "/api/v1/suppliers/**", "/api/v1/users/**", "/api/v1/dashboard/**", "/api/v1/brands/**", "/api/v1/variations/**", "/api/v1/sku/**").hasAnyRole( "ADMIN", "MANAGER", "STAFF")
                        .anyRequest().authenticated()

                )

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(withDefaults())
                .build();

    }
}
