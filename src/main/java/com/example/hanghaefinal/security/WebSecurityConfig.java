package com.example.hanghaefinal.security;

import com.example.hanghaefinal.config.CorsConfig;
import com.example.hanghaefinal.security.jwt.JwtAuthenticationFilter;
import com.example.hanghaefinal.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity // 스프링 Security 지원을 가능하게 함
@EnableGlobalMethodSecurity(securedEnabled = true) // @Secured 어노테이션 활성화
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final CorsConfig corsConfig;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder encodePassword() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
// h2-console 사용에 대한 허용 (CSRF, FrameOptions 무시)
        web
                .ignoring()
                .antMatchers("/h2-console/**",
                        "/favicon.ico",
                        "/error"
                );
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .addFilter(corsConfig.corsFilter())
                .httpBasic().disable() // rest api 만을 고려하여 기본 설정은 해제하겠습니다.
                .csrf().disable()

                .exceptionHandling();

                // enable h2-console
        http
                .headers()
                .frameOptions().sameOrigin();

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
        http    .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
//                .mvcMatchers(HttpMethod.OPTIONS,"/**").permitAll()
                .antMatchers("/","/user/login","/user/signup",
                        "/user/signup/checkID","/user/signup/checkNick","/search",
                        "/mailCheck","/login/kakaoLogin","/categories","/category/posts",
                        "/posts/userPage/**","/posts/recent", "/posts/recommend","/posts/incomplete",
                        "/posts/viewMain","/posts/{postId}","/comment/{postId}","/notice","/ws-stomp/**").permitAll()
                .anyRequest().authenticated()

                //.and().cors().configurationSource(corsConfigurationSource())    // 추가

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class);
    }

}
