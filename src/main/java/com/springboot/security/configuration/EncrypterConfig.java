package com.springboot.security.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//비밀번호 인코딩. 비밀번호 일치 여부 확인
@Configuration
public class EncrypterConfig {

    @Bean
    public BCryptPasswordEncoder encodPwd() {
        return new BCryptPasswordEncoder(); //비밀번호 암호화
    }
}
