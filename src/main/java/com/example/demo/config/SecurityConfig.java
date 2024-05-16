package com.example.demo.config;

import com.example.demo.jwt.JWTUtil;
import com.example.demo.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 스프링에서 Configuration 클래스로 관리되기 위해서
@EnableWebSecurity //Security를 위한 Config이기 때문에
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion, JWTUtil 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;


    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    // LoginFilter에서 AuthenticationManager를 주입받았기 때문에 SecurityConfig에서도 주입하고,
    // LOginFilter에서 동작할 수 있도록 5번의 new LoginFilter()의 인자로 넣어준다
    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // bCryptPasswordEncoder : 비밀번호 캐시 암호화 메서드
    // 회원가입, 정보 저장, 검증 등을 수행할 때는 항상 비밀번호 캐시 암호화 후 진행함
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // filterChain 메서드
    // 각종 세션 설정을 진행해주는 SecurityFilterChain 리턴
    // 인자로 HttpSecurity 타입을 받는다
    // 받은 인자를 build()로 리턴한다
    // throws Exception으로 예외처리를 해준다
    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception {

        // 1. csrf disable
        // 세션 방식에서는 세션이 항상 고정되어 있기 때문에 csrf 공격을 방어해주는 작업이 필요하지만,
        // JWT에서는 세션은 Stateless로 관리하기 때문에 csrf 공격을 방어할 필요가 없다.
        http.csrf((auth) -> auth.disable());

        // 2. Form 로그인 방식 disable
        // JWT 방식으로 로그인 진행할거라 필요 없다
        http.formLogin((auth) -> auth.disable());

        // 3. http basic 인증 방식 disable
        // JWT 방식으로 로그인 진행할거라 필요 없다
        http.httpBasic((auth)-> auth.disable());

        // 4. 경로별 권한 인가 작업 (authorizeHttpRequests : 인가 담당 메서드)
        http.authorizeHttpRequests((auth)-> auth
                .requestMatchers("/login","/","/join").permitAll() // login, root, join  경로에 대해서는 모든 권한 허용
                .requestMatchers("/admin").hasRole("ADMIN") // admin 경로는 ADMIN이라는 권한을 가진 사용자만 접근 가능
                .anyRequest().authenticated()); // 그 외 요청(anyRequest)은 로그인된 사용자만 접근 가능(authenticated)

        // 5. UsernamePasswordAuthenticationFilter를 커스텀한 LoginFilter 등록
        // UsernamePasswordAuthenticationFilter를 대채해서 등록하는 것이기 때문에 addFilterAt 선택
        // addFilterBefore는 지정한 필터 전에 등록하는 것, addFilterAfter는 지정한 필터 후에 등록하는 것
        http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil), UsernamePasswordAuthenticationFilter.class);

        // 6. 세션 설정
        // JWT 방식에서는 세션을 stateless 방식으로 관리하므로 stateless 상태로 설정해주어야 한다
        http.sessionManagement((session)->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}