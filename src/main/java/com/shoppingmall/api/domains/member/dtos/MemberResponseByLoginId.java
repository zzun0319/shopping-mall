package com.shoppingmall.api.domains.member.dtos;

import com.shoppingmall.domain.members.AttachedFile;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.enums.Grade;
import lombok.Getter;

import javax.persistence.*;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Getter
public class MemberResponseByLoginId {

    private Long id;

    private String loginId;
    private String name;

    @Enumerated(EnumType.STRING)
    private Grade grade; // 회원 등급

    private Boolean saleAvailable;

    private Boolean hasFile;

    public MemberResponseByLoginId(Member member) {
        id = member.getId();
        loginId = member.getLoginId();
        name = member.getName();
        grade = member.getGrade();
        saleAvailable = member.getSaleAvailable();
        hasFile = (member.getFile() != null) ? true : false;
    }
}
