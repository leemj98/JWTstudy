package com.example.demo.repository;


import com.example.demo.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    //repository : entity를 가지고 db에 접근해서 데이터를 담아오고 집어넣는 대리인
    // Integer는 UserEntity의 Id 값의 타입

    Boolean existsByUsername(String username); // existsBy : 존재하는지 확인하는 JPA 구문 (쿼리)

    // username을 받아 DB 테이블에서 특정 user를 조회하는 ORM
    // userType에 해당하는 UserEntity 객체 반환
    // UserDetailsService에서 findByUsername 메서드(JPA 메서드) 를 통해 특정 user를 조회할 수 있음
    UserEntity findByUsername(String username);
}
