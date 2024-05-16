package com.kh.bookfinder.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.login.filter.JsonLoginFilter;
import com.kh.bookfinder.auth.login.handler.JsonLoginFailureHandler;
import com.kh.bookfinder.auth.login.handler.JsonLoginSuccessHandler;
import com.kh.bookfinder.auth.login.service.SecurityUserService;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtService jwtService;
  private final SecurityUserService securityUserService;
  private final ObjectMapper objectMapper;
  private final Validator validator;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().permitAll())
        .addFilterAfter(jsonLoginFilter(), LogoutFilter.class);

    return httpSecurity.build();
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder());
    provider.setUserDetailsService(securityUserService);
    return new ProviderManager(provider);
  }

  @Bean
  public JsonLoginSuccessHandler loginSuccessHandler() {
    return new JsonLoginSuccessHandler(jwtService);
  }

  @Bean
  public JsonLoginFailureHandler loginFailureHandler() {
    return new JsonLoginFailureHandler();
  }

  @Bean
  public JsonLoginFilter jsonLoginFilter() {
    JsonLoginFilter jsonLoginFilter = new JsonLoginFilter(objectMapper, validator);
    jsonLoginFilter.setAuthenticationManager(authenticationManager());
    jsonLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
    jsonLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
    return jsonLoginFilter;
  }
}
