package com.springboot.security.service;

import com.springboot.security.domain.User;
import com.springboot.security.domain.dto.UserDto;
import com.springboot.security.domain.dto.UserJoinRequest;
import com.springboot.security.exception.ErrorCode;
import com.springboot.security.exception.HospitalReviewException;
import com.springboot.security.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

//username, password 받아서 회원가입 처리
@NoArgsConstructor
@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder encoder;

    public UserDto join(UserJoinRequest request) {
        //중복 Check
        userRepository.findByUserName(request.getUserName())
                .ifPresent(user -> {
                throw new HospitalReviewException(
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
    }
