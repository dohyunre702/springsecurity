package com.springboot.security.service;

import com.springboot.security.domain.User;
import com.springboot.security.domain.dto.UserDto;
import com.springboot.security.domain.dto.UserJoinRequest;
import com.springboot.security.exception.ErrorCode;
import com.springboot.security.exception.AppException;
import com.springboot.security.repository.UserRepository;
import com.springboot.security.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//username, password 받아서 회원가입 처리
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserDto join(UserJoinRequest request) {
        //중복 Check
        userRepository.findByUserName(request.getUserName())
                .ifPresent(user -> {
                throw new AppException(
                        ErrorCode.DUPLICATED_USER_NAME,
                        String.format("Username : %s", request.getUserName()));
        });

        //pw 암호화해서 toEntity의 매개변수로 넘겨주기
        User savedUser = userRepository.save(request.toEntity(encoder.encode(request.getPassword())));

        return UserDto.builder()
                .id(savedUser.getId())
                .userName(savedUser.getUserName())
                .email(savedUser.getEmail())
                .build();
        }

    @Value("%{jwt.token.secret}")
    private String secretKey;
    private final long expiredTimeMs = 1000 * 60 * 60l; //1시간

    //로그인
    public String login(String userName, String password) {
        //failure1. UserName 중복체크
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND, userName + "이 없습니다."));

        //failure2. 비밀번호 틀림
        if (!encoder.matches(password, selectedUser.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD, "password가 일치하지 않습니다");
        }

        //success. userName, secretKey, 토큰 유효기간을 받아 JWT 토큰 생성
        String token = JwtTokenUtil.createToken(selectedUser.getUserName(), secretKey, expiredTimeMs);
        return token;
    }

    //token이 없으면 접근 거부
    public User getUserByUserName(String userName) {
        User user = userRepository.findByUserName(userName).orElseThrow(()
                -> new AppException(ErrorCode.NOT_FOUND,""));
                    return user;
    }
}

}
