package com.shoppingmall;

import com.shoppingmall.domain.members.dtos.MemberDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

@Controller("/")
public class HomeController {

    @GetMapping
    public String home(@SessionAttribute(name = "loginMember", required = false) MemberDto loginMember, Model model){

        // 세션에 loginMember라는 이름으로 Attribute이 없으면
        if(loginMember == null){
            return "home";
        }

        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
