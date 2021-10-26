package com.shoppingmall.api.domains.member.dtos;

import com.shoppingmall.domain.members.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponse {

    private Long memberId;
    private String loginId;
    private String memberName;
    private String pwd;

    public MemberResponse(Member member) {
        memberId = member.getId();
        loginId = member.getLoginId();
        memberName = member.getName();
        String password = member.getPassword();
        String replaceAst = "";
        for(int i = 0; i < password.length() - 1; i++) replaceAst += "*";
        String substring = password.substring(0, 1);
        pwd = substring + replaceAst;
    }
}
