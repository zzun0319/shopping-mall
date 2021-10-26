package com.shoppingmall.domain.members.dtos;

import com.shoppingmall.enums.Grade;
import com.shoppingmall.domain.members.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberDto {

    private Long id;
    private String loginId;
    private String name;
    private Grade grade;
    private Boolean saleAvailable;

    public MemberDto(Member member) {
        id = member.getId();
        loginId = member.getLoginId();
        name = member.getName();
        grade = member.getGrade();
        saleAvailable = member.getSaleAvailable();
    }
}
