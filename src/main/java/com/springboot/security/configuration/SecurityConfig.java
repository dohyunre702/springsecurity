package com.springboot.security.configuration;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//최근 Spring Security는 SecurityFilterChain을 재정의하여 사용하기를 권장
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable() //http basic auth 기반으로 인증창이 뜸. 기본 인증 로그인을 사용하지 않으면 disable
                .csrf().disable()//html tag를 통한 공격
                .cors().and()  //자원이 로드될 수 있도록 헤더에 표시
                .authorizeRequests() //요청에 의한 보안검사 시작
                .antMatchers("/api/**").permitAll()//api/** 의 접근을 인증절차 없이 허용
                .antMatchers("/api/v1/users/join", "/api/v1/users/login").permitAll() //join, login은 언제나 통과가능
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //jwt 사용하는 경우. STATELESS: 인증 정보를 서버에 담아두지 않음
                .and()
                // .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
