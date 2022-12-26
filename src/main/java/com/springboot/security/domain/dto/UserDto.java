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
public class UserDto {
    private Long id;
    private String userName;
    private String password;
    private String email;

    public static UserDto fromEntity(User entity) {
        return new UserDto(entity.getId(), entity.getUserName(), entity.getPassword(), entity.getEmail());
    }


}
