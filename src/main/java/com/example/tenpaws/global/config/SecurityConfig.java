package com.example.tenpaws.global.config;

import com.example.tenpaws.global.advice.OAuth2SuccessHandler;
import com.example.tenpaws.global.security.jwt.JwtFilter;
import com.example.tenpaws.global.security.jwt.JwtUtil;
import com.example.tenpaws.global.security.jwt.LoginFilter;
import com.example.tenpaws.global.security.jwt.CustomLogoutFilter;
import com.example.tenpaws.global.security.repository.RefreshRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final DefaultOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        // rest api, stateless 방식 사용하므로 비활성화
        httpSecurity
                .formLogin(AbstractHttpConfigurer::disable);
        httpSecurity
                .httpBasic(AbstractHttpConfigurer::disable);
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable);

        // CORS 설정
        httpSecurity
                .cors((cors) -> cors
                        .configurationSource(new CorsConfigurationSource() {
                            @Override
                            public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                                CorsConfiguration configuration = new CorsConfiguration();

                                configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                                configuration.setAllowedMethods(Collections.singletonList("*"));
                                configuration.setAllowCredentials(true);
                                configuration.setAllowedHeaders(Collections.singletonList("*"));
                                configuration.setMaxAge(3600L);

                                configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                                return configuration;
                            }
                        }));

        // stateless 상태 활성화
        httpSecurity
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 모든 기능 완성되면 그 때 엔드포인트 보고 접근 권한 수정!
        httpSecurity
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/", "/join","/oauth2/**").permitAll()
                        .anyRequest().permitAll());

        httpSecurity
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, refreshRepository), UsernamePasswordAuthenticationFilter.class);

        httpSecurity
                .addFilterBefore(new JwtFilter(jwtUtil), LoginFilter.class);

        httpSecurity
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshRepository), LogoutFilter.class);

        // oauth2
        httpSecurity
                .oauth2Login((oauth2) -> oauth2
                        .authorizationEndpoint((endpoint) -> endpoint.baseUri("/api/v1/auth/oauth2"))
                        .redirectionEndpoint((endpoint) -> endpoint
                                .baseUri("/oauth2/callback/*"))
                        .userInfoEndpoint((endpoint) -> endpoint.userService(oAuth2UserService))
                        .successHandler(oAuth2SuccessHandler)
                );

        return httpSecurity.build();
    }
}

