package com.shoppingmall.domain.members.forms;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChangePasswordForm {

    private Long id;
    private String oldPassword;
    private String newPassword;
    private String newPasswordCheck;

    /**
     * 테스트용
     * @param id
     * @param oldPassword
     * @param newPassword
     * @param newPasswordCheck
     */
    public ChangePasswordForm(Long id, String oldPassword, String newPassword, String newPasswordCheck) {
        this.id = id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }
}
