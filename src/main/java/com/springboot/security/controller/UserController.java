package com.springboot.security.controller;

import com.springboot.security.domain.Response;
import com.springboot.security.domain.User;
import com.springboot.security.domain.dto.UserDto;
import com.springboot.security.domain.dto.UserJoinRequest;
import com.springboot.security.domain.dto.UserJoinResponse;
import com.springboot.security.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest request) {
        UserDto userDto = userService.join(request);
        UserJoinResponse response = new UserJoinResponse(userDto.getUserName(), userDto.getEmail());
        return Response.success(response);
    }
}
