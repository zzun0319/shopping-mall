package com.shoppingmall.domain.members.forms;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter @Setter
public class MemberLoginForm {

    @NotEmpty(message = "아이디를 입력하세요")
    private String loginId;

    @NotEmpty(message = "비밀번호를 입력하세요")
    private String password;
}
