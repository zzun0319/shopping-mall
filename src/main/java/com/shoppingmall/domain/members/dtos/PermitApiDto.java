package com.shoppingmall.domain.members.dtos;

import lombok.Data;

@Data
public class PermitApiDto {

    private String loginId;
    private String permitPassword;
    private Boolean permission;
}
