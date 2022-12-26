package com.springboot.security.configuration;

import com.springboot.security.domain.User;
import com.springboot.security.service.UserService;
import com.springboot.security.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.MemberSubstitution;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final UserService userService;
    private final String secretKey;

    //과정: Token에서 Claim 꺼내기 - Valid한지 확인 - UserName 넣기

    @Override //매 요청마다 한 번만 실행되게끔 구현
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

       //custom filter setting : OncePerRequestFilter로부터 오버라이딩한 doFilterInternal() 메서드 구현
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                "", null, List.of(
                        new SimpleGrantedAuthority("USER")));
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);

        //token에 Claim으로 UserName을 넣었기 때문에 token.Claim에서 꺼낼 예정
        final String authroizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        //header꺼내기 Bearer: 인증타입의 한 종류. JWT or OAuth 토큰을 사용하는 인증 방식
        if(authroizationHeader == null ||!authroizationHeader.startsWith("Bearer")) {
            //인가 불가능한 경우 1. null "인증 헤더가 잘못되었습니다"
            filterChain.doFilter(request, response);
            return;
        }

        //인가가 불가능한 경우 2. 유효기간 만료
        String token;
        try { //header에서 token 분리
            token = authroizationHeader.split(" ")[1].trim();
            //token 유효성 체크 = token expired time이 안지났는지 확인
            JwtTokenUtil.isExpired(token, secretKey);
        } catch (Exception e) {
            //유효기간 만료되었다면 "token 추출 실패"
            filterChain.doFilter(request, response);
            return;
        }

        //만료 토큰
        if(JwtTokenUtil.isExpired(token, secretKey)) {
            filterChain.doFilter(request, response);
            return;
        }

        //인가 불가 3. 접근 권한이 적절하지 않은 경우
        //Token의 Calim에서 userName 꺼내기
        String userName = JwtTokenUtil.getUserName(token, secretKey);
        //UserDetail에서 가져오기
        User user = userService.getUserByUserName(userName);

        //Role 바인딩
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUserName(), null, List.of(
                        new SimpleGrantedAuthority(user.getRole().name())));

        //Detail 넣어주기
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken); //권한 부여 완료
        filterChain.doFilter(request, response);

    }
}
