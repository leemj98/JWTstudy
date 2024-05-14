package com.example.demo.controller;

import com.example.demo.dto.JoinDTO;
import com.example.demo.service.JoinService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@ResponseBody // Api 서버를 만들 예정이므로..? 응답을 reponsebody로 진행
public class JoinController {

    // 서비스 주입
    private final JoinService joinService;

    public JoinController(JoinService joinService) {

        this.joinService = joinService;
        // autowired를 통해 주입받아도 되지만 생성자 주입 방식을 권고하고 있다고 함
    }

    @PostMapping("/join")
    public String joinProcess(JoinDTO joinDTO) {

        System.out.println(joinDTO.getUsername());
        joinService.joinProcess(joinDTO);

        return "ok";
    }
}