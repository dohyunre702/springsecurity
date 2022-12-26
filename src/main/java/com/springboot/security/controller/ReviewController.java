package com.springboot.security.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews")
@Slf4j
public class ReviewController {

    @PostMapping
    public String writeReview(Authentication authentication) {
        log.info("isAuthenticated:{} name:{}", authentication.isAuthenticated(), authentication.getName());
        return "성공적으로 댓글을 달았습니다.";
    }
    //authentication.isAuthenticated(), authentication.getName()으로 인증 통과했는지, User 누구인지 받아 옴
}
