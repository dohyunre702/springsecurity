package com.springboot.security.domain.dto;

import com.springboot.security.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserJoinRequest {
    //id는 token을 부여할 예정(자동생성)
    private String userName;
    private String password;
    private String email;

    public User toEntity(String password) { //password 매개변수 지정
        return User.builder()
                .userName(this.userName)
                .password(password) //db에 저장시 인코딩 위함
                .email(this.email)
                .build();
    }

}
