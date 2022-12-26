package com.springboot.security.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.security.domain.dto.UserDto;
import com.springboot.security.domain.dto.UserJoinRequest;
import com.springboot.security.domain.dto.UserLoginRequest;
import com.springboot.security.exception.ErrorCode;
import com.springboot.security.exception.AppException;
import com.springboot.security.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest
public class UserControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;
    //ObjectMapper : JSON <-> JAVA 간 변환 가능 (직렬화, 역직렬화). 파싱에 이용

    //회원가입
    @Test
    @DisplayName("회원가입 성공")
    void join_success() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("lee")
                .password("0000")
                .email("dhlee@naver.com")
                .build();

        when(userService.join(any())).thenReturn(mock(UserDto.class));

        mockMvc.perform(post("/api/v1/users/join")
                .with(csrf()) //token 인증 통과 (401/403에러 방지)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userJoinRequest))) //JAVA -> JSON
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패")
    void join_failure() throws Exception {
        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("lee")
                .password("0000")
                .email("dhlee@naver.com")
                .build();

        when(userService.join(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf()) //token 인증 통과 (401/403에러 방지)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest))) //JAVA -> JSON
                .andDo(print())
                .andExpect(status().isConflict());
    }


    //로그인
    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void loginSuccess() throws Exception {
        String userName = "dhlee";
        String password = "0000";

        //userService이 login이 호출되면 String 반환
        given(userService.login(any(), any()))
                .willReturn("token");

        //POST로 해당 URL에 요청이 들어온다면
        mockMvc.perform(post("api/v1/users/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 실패 1. USERNAME 없음")
    @WithMockUser //인증된 사용자의 정보를 어노테이션 통해 간단히 주입
    void loginFailByUserName() throws Exception {
        String userName = "dhlee";
        String password = "0000";

        //userService의 login이 호출되면 NOT_FOUND 발생
        given(userService.login(any(), any()))
                .willThrow(new AppException(ErrorCode.NOT_FOUND, ""));

        //POST로 해당 URL에 요청이 들어온다면
        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 성공 2. PASSWORD 불일치")
    @WithMockUser
    void loginFailByPassword() throws Exception {
        String userName = "오형상";
        String password = "ohy1023";

        // userService의 login이 호출되면 INVALID_PASSWORD 발생
        given(userService.login(any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_PASSWORD, ""));

        // POST로 해당 URL에 요청이 들어 온다면
        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password))))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
