package com.example.demo.jwt;

import com.example.demo.dto.CustomUserDetails;
import com.example.demo.entity.UserEntity;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// jwt 검증 필터
// 요청에 대해서 한번만 동작하는 OncePerRequestFilter 상속받아서 만들기
public class JWTFilter extends OncePerRequestFilter {

    // 검증을 위한 주입
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //request에서 Authorization 헤더를 찾음
        String authorization= request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) { //토큰이 없거나 접두사에 이상이 있을 경우

            System.out.println("token null");
            filterChain.doFilter(request, response); // 필터는 체인 방식으로 엮여있기 때문에 request와 response를 다음 필터로 넘겨주는 작업

            //메소드 종료 (필수)
            return;
        }

        System.out.println("authorization now");
        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        //토큰 소멸 시간 검증
        if (jwtUtil.isExpired(token)) {

            System.out.println("token expired");
            filterChain.doFilter(request, response);

            //true가 되면 토큰이 만료된 것이기 때문에 메소드 종료 (필수)
            return;
        }

        // 이제 여기까지 온 토큰들은 토큰 존재하고, 접두사에 이상없고, 만료되지 않은 것들임
        // 토큰을 기반으로 일시적인 세션 SecurityContextHolder 생성해서 user 일시적으로 저장시켜주면,
        // admin 경로 등 user 정보를 요구하는 경로의 요청을 진행할 수 있게 됨.

        //토큰에서 username과 role 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        //userEntity를 생성하여 값 set
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword("temppassword"); //password의 경우 토큰에 담겨있지 않았지만 같이 초기화 진행
        // password에 정확한 값 넣으면 요청 필요할 때마다 비번 조회하는 상황 만들어지기 때문에 사용자가 아무 값이나 만들어서 강제로 넣어주기
        userEntity.setRole(role);

        //UserDetails에 회원 정보 객체 담기
        CustomUserDetails customUserDetails = new CustomUserDetails(userEntity);

        //만든 UserDetails 객체로 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 메서드 종료되었으므로 그 다음필터로 request와 response 전달
        filterChain.doFilter(request, response);
    }
}
