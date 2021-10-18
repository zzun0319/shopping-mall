package com.shoppingmall.domain.members;

import com.shoppingmall.domain.commons.BaseDateInfo;
import com.shoppingmall.domain.enums.Grade;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.CascadeType.*;
import static javax.persistence.FetchType.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseDateInfo {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String loginId;
    private String password;
    private String name;

    @Enumerated(EnumType.STRING)
    private Grade grade; // 회원 등급

    private Boolean saleAvailable;

    @OneToOne(fetch = LAZY, cascade = ALL)
    @JoinColumn(name = "attached_file_id")
    private AttachedFile file;

    /**
     * 일반적인 Member생성시 사용되는 생성자
     * @param name
     * @param loginId
     * @param password
     */
    private Member(String name, String loginId, String password) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.grade = Grade.USER;
        this.saleAvailable = false;
    }

    /**
     * 테스트용, DB 초기화용 회원을 만들기 위한 생성자
     * @param name
     * @param loginId
     * @param password
     * @param grade
     * @param saleAvailable
     */
    public Member(String name, String loginId, String password, Grade grade, Boolean saleAvailable) {
        this.name = name;
        this.loginId = loginId;
        this.password = password;
        this.grade = grade;
        this.saleAvailable = saleAvailable;
    }

    /**
     * 멤버 생성 메서드
     * @param name
     * @param loginId
     * @param password
     * @return 새로 생성된 멤버 리턴.
     */
    public static Member createMember(String name, String loginId, String password) {
        return new Member(name, loginId, password);
    }

    /**
     * 판매 허가 메서드 (ADMIN만 비밀번호를 알고 바꿀 수 있도록)
     * @param permitPassword
     */
    public void permitSaleChange(String permitPassword, boolean permit){
        if(permitPassword.equals("abc1234")){
            this.saleAvailable = permit;
        }
    }
    
    //TODO: 회원 등급 변경 메서드

    /**
     * 이름 변경 메서드
     * @param name
     */
    public void changeName(String name) {
        this.name = name;
    }

    /**
     * 비밀번호 변경 메서드
     * @param password
     */
    public void changePassword(String password){ this.password = password; }

    /**
     * 파일 세팅 메서드
     * @param file
     */
    public void setFile(AttachedFile file) {
        this.file = file;
    }

}
