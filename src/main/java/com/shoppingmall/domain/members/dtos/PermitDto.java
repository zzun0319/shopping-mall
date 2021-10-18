package com.shoppingmall.domain.members.dtos;

import com.shoppingmall.domain.members.AttachedFile;
import com.shoppingmall.domain.members.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class PermitDto {

    private String loginId;
    private String name;
    private Boolean saleAvailable;
    private AttachedFile attachedFile;

    public PermitDto(Member member) {
        this.loginId = member.getLoginId();
        this.name = member.getName();
        this.saleAvailable = member.getSaleAvailable();
        if(member.getFile() != null){
            this.attachedFile = member.getFile();
        }
    }
}
