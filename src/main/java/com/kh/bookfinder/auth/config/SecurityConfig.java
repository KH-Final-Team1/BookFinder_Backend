package com.kh.bookfinder.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.bookfinder.auth.jwt.filter.JwtAuthenticationFilter;
import com.kh.bookfinder.auth.jwt.handler.JwtForbiddenHandler;
import com.kh.bookfinder.auth.jwt.handler.JwtUnauthorizedHandler;
import com.kh.bookfinder.auth.jwt.service.JwtService;
import com.kh.bookfinder.auth.login.filter.JsonLoginFilter;
import com.kh.bookfinder.auth.login.handler.JsonLoginFailureHandler;
import com.kh.bookfinder.auth.login.handler.JsonLoginSuccessHandler;
import com.kh.bookfinder.auth.login.service.SecurityUserService;
import com.kh.bookfinder.auth.oauth2.handler.OAuth2LoginFailureHandler;
import com.kh.bookfinder.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.kh.bookfinder.auth.oauth2.service.CustomOAuth2UserService;
import com.kh.bookfinder.user.repository.UserRepository;
import jakarta.validation.Validator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtService jwtService;
  private final SecurityUserService securityUserService;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final UserRepository userRepository;
  private final JwtUnauthorizedHandler jwtUnauthorizedHandler;
  private final JwtForbiddenHandler jwtForbiddenHandler;
  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
  private final ObjectMapper objectMapper;
  private final Validator validator;


  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
    // 클라이언트와 REST 통신을 위한 설정
    httpSecurity
        .csrf(AbstractHttpConfigurer::disable)
        .cors(corsConfig -> corsConfig.configurationSource(corsConfigurationSource()))
        .httpBasic(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .sessionManagement(sessionManagementConfigurer -> sessionManagementConfigurer
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    httpSecurity.headers(frame ->
        frame.frameOptions(FrameOptionsConfig::sameOrigin));

    // Test에 대한 권한 설정
    httpSecurity
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/test/v1/anonymous").anonymous())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/test/v1/authenticate").authenticated())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/test/v1/admin").hasRole("ADMIN"))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/test/v1/user").hasAnyRole("ADMIN", "USER"))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/h2-console/**").permitAll());

    // API들에 대한 권한 설정
    httpSecurity
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/v1/login", "/api/v1/signup/**").anonymous())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/v1/books/list", "/api/v1/books/{isbn}").permitAll())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/v1/trades/**", "/api/v1/comments/**", "/api/v1/users/my-info").authenticated())
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/api/v1/oauth2/signup").hasRole("SOCIAL_GUEST"))
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());

    // OAuth2 로그인 설정
    httpSecurity.oauth2Login(oauth2 -> oauth2
        .successHandler(oAuth2LoginSuccessHandler)
        .failureHandler(oAuth2LoginFailureHandler)
        .userInfoEndpoint(endpoint -> endpoint.userService(customOAuth2UserService)));

    // Jwt Filter 설정
    httpSecurity.addFilterAfter(jsonLoginFilter(), LogoutFilter.class);
    httpSecurity.addFilterBefore(jwtAuthenticationFilter(), JsonLoginFilter.class)
        .exceptionHandling(handler -> handler
            .authenticationEntryPoint(jwtUnauthorizedHandler)
            .accessDeniedHandler(jwtForbiddenHandler));

    return httpSecurity.build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedMethods(List.of("OPTIONS", "GET", "POST", "PUT", "PATCH", "DELETE"));
    corsConfiguration.setAllowedOrigins(List.of("http://localhost:3000", "..."));
    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
    corsConfiguration.setExposedHeaders(List.of("Authorization"));
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
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

  @Bean
  public JwtAuthenticationFilter jwtAuthenticationFilter() {
    return new JwtAuthenticationFilter(jwtService, userRepository);
  }
}
