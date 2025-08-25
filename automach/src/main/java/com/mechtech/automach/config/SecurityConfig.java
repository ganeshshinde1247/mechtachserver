package com.mechtech.automach.config;

import com.mechtech.automach.filter.JWTAuthenticationFilter;
import com.mechtech.automach.service.CustomUserDetailsService;
import com.mechtech.automach.utils.JWTUtils;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
      /*   http.csrf().disable()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/auth/login").permitAll() // Allow access to authentication endpoints
                .anyRequest().authenticated() // All other requests require authentication
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless session
            );
        // http.addFilterBefore(new JWTAuthenticationFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);
        return http.build(); */
          httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/auth/login")
                        .permitAll()
                        .requestMatchers("/templates/*", "/styles/*", "/scripts/*", "/css/*", "/images/*").permitAll()
                        .requestMatchers("/auth/validateUserLoginDetails").hasAnyAuthority("USER", "ADMIN")
                        .anyRequest().authenticated())
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    /**
     * Configures the AuthenticationProvider for DaoAuthentication.
     *
     * @return AuthenticationProvider for DaoAuthentication.
     */
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    /**
     * Configures the PasswordEncoder for securing user passwords.
     *
     * @return PasswordEncoder for securing passwords.
     */
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    /**
     * Configures the AuthenticationManager.
     *
     * @param authenticationConfiguration AuthenticationConfiguration for obtaining
     *                                    the AuthenticationManager.
     * @return AuthenticationManager.
     * @throws Exception If an exception occurs during configuration.
     */
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    /**
     * Configures the ModelMapper for object mapping.
     *
     * @return ModelMapper instance.
     */
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
