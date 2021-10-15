package com.shoppingmall.domain.members.forms;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter @Setter
@NoArgsConstructor
public class MemberJoinForm {

    @Length(min = 6, max = 14, message = "아이디는 필수값이며 6~14자리여야 합니다.")
    private String loginId;

    @Pattern(regexp="(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{10,30}",
            message = "비밀번호는 10~30자리의 영문대소문자와 숫자, 특수문자의 조합이어야합니다.")
    private String password;

    @NotEmpty(message = "이름은 필수값입니다.")
    private String name;

    private MultipartFile salesPermissionFile;

    public MemberJoinForm(String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
}
