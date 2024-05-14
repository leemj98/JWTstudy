package com.example.demo.service;

import com.example.demo.dto.JoinDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    // 주입
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 주입 값을 생성자 방식으로 초기화
    public JoinService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {

        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        Boolean isExist = userRepository.existsByUsername(username);

        if (isExist) {

            return; //이미 회원이 존재한다면 종료
        }

        // 회원이 존재하지 않는다면 회원 가입 진행
        // 회원 가입 : userEntity에 값 넣어주기
        UserEntity data = new UserEntity();

        // 괄호 안이 초기화시켜줄 값임
        data.setUsername(username);
        data.setPassword(bCryptPasswordEncoder.encode(password)); //pw는 반드시 암호화된 형태로 넣어야 한다
        data.setRole("ROLE_ADMIN"); // 스프링 이름 규칙 : 앞단 접두사, 뒷단 역할 값

        userRepository.save(data); // userRepository에 userEntity 값을 저장 (즉, DB에 데이터 저장)
    }
}
