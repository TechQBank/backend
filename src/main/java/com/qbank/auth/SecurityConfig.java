package com.qbank.auth;

import com.qbank.auth.oauth2.CustomOAuth2UserService;
import com.qbank.auth.oauth2.OAuth2FailureHandler;
import com.qbank.auth.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;
    private final OAuth2FailureHandler failureHandler;
    private final JwtFilter jwtFilter;

    @Value("${frontend.url}")
    private String frontendUrl;

    private final String[] WHITE_LIST = {
            "/api/questions", "/api/questions/{id}",
            "/api/questions/{id}/public-answers",
            "/api/tags", "/api/tags/popular",
            "/api/questions/study", "/api/questions/study/random",
            "/login/**", "/oauth2/**",
            "/api/users/search",
            "/api/users/{id}/groups"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 공개 조회 엔드포인트
                        .requestMatchers(HttpMethod.GET, WHITE_LIST).permitAll()
                        // Swagger, Actuator
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/actuator/**").permitAll()
                        // 나머지 전부 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(u -> u.userService(oAuth2UserService))
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> {
                            res.setContentType("application/json;charset=UTF-8");
                            res.setStatus(401);
                            res.getWriter().write("{\"code\":\"A001\",\"message\":\"인증이 필요합니다.\"}");
                        })
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(frontendUrl));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
