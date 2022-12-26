package com.springboot.security.configuration;

import com.springboot.security.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//최근 Spring Security는 SecurityFilterChain을 재정의하여 사용하기를 권장
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserService userService;

    @Value("${jwt.token.secret}")
    private String secretKey; //파라미터
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable() //http basic auth 기반으로 인증창이 뜸. 기본 인증 로그인을 사용하지 않으면 disable
                .csrf().disable()//html tag를 통한 공격
                .cors().and()  //자원이 로드될 수 있도록 헤더에 표시
                .authorizeRequests() //요청에 의한 보안검사 시작
                .antMatchers(HttpMethod.POST,"/api/v1/**").authenticated()//api/** 경로로 post 요청 받을 시 접근 권한 확인
                .antMatchers("/api/v1/users/join", "/api/v1/users/login").permitAll() //join, login은 언제나 통과가능. 이 구문이 위로 가면 join, login 작업 접근 차단됨
                .antMatchers("/user").hasRole("USER")//Role이 User인 사람 접근
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //jwt 사용하는 경우. STATELESS: 인증 정보를 서버에 담아두지 않음
                .and()
                .addFilterBefore(new JwtTokenFilter(userService, secretKey), UsernamePasswordAuthenticationFilter.class)
                //필터 지나기 전에 JwtTokenFiler 거치게 - 지정된 필터 앞에 커스텀필터 추가 (사용자 권한에 따른 댓글 서비스 접근 인가 구현시 필요)
                .build();
    }
}
