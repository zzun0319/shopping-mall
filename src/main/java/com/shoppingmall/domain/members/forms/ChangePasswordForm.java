package com.shoppingmall.domain.members.forms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
public class ChangePasswordForm {

    @NotEmpty(message = "이전 비밀번호를 입력해주세요.")
    private String oldPassword;

    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{10,30}",
            message = "비밀번호는 10~30자리의 영문대소문자와 숫자, 특수문자의 조합이어야합니다.")
    private String newPassword;

    @NotEmpty(message = "위와 똑같은 비밀번호로 채워주세요.")
    private String newPasswordCheck;

    /**
     * 테스트용
     * @param oldPassword
     * @param newPassword
     * @param newPasswordCheck
     */
    public ChangePasswordForm(String oldPassword, String newPassword, String newPasswordCheck) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.newPasswordCheck = newPasswordCheck;
    }

}
