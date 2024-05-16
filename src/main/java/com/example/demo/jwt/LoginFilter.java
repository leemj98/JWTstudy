package com.example.demo.jwt;

import com.example.demo.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {

        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    // 로그인 검증을 진행하기 위한 attemptAuthentication 메서드를 꼭 override시켜줘야 한다.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 username, password 추출
        // obtain메서드의 파라미터로 request를 적어주면 request에서 username과 password 추출 가능
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        System.out.println(username);

        //스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        //꺼낸 username과 password를 Authentication Manager에 전달해 검증 진행하기 위해서 바구니(UsernamePasswordAuthenticationToken)에 담아 던져주는 것 (DTO와 비슷한 개념)
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

        //token에 담은 검증을 위한 AuthenticationManager로 전달
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시 실행하는 메소드 (여기서 JWT를 발급하면 됨)
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        //UserDetails
        // authentication.getPrincipal() 메서드를 통해 User를 가져올 수 있음
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();

        // 위에서 가져온 User 정보에서 username 뽑아내 저장
        String username = customUserDetails.getUsername();

        // role 값 뽑아내는 구현 (자세한 설명없이 넘어감)
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        // 뽑아낸 role 값 저장
        String role = auth.getAuthority();

        // 뽑아낸 usename과 role 값으로 jwtUtil을 통해 토큰 생성
        String token = jwtUtil.createJwt(username, role, 60*60*10L);

        // 생성된 JWT를 헤더에 추가해 응답
        // 인수 1. 키 값 : Authorization에 담겠다
        // 인수 2. 인증방식 + token : 인증방식과 token 사이에는 반드시 띄어쓰기 해주기
        response.addHeader("Authorization", "Bearer " + token);
    }

    //로그인 실패시 실행하는 메소드
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        
        // 401 응답 보내기
        response.setStatus(401);
    }
}