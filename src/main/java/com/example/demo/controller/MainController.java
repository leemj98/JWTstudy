package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Iterator;

@Controller
@ResponseBody
public class MainController {

    @GetMapping("/")
    public String mainP() {

        // 현재 세션을 통과하는 사용자 이름 가져오기
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        // role 값 빼내는 작업은 iterator 반복자로 돌면서 빼내야되기 때문에 조금 복잡하다
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iter = authorities.iterator();
        GrantedAuthority auth = iter.next();
        String role = auth.getAuthority();

        // username과 role을 responsebody 형태로 리턴
        return "Main Controller : "+name + role;
    }
}