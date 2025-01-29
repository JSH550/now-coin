package com.coin.now_coin.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/api/login")
     public String redirectLoginPage(){



        return "redirect:/html/login.html";




    }

}
