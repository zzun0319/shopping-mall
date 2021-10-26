package com.shoppingmall.api.domains.member.controller;

import com.shoppingmall.api.domains.member.dtos.MemberResponse;
import com.shoppingmall.api.exceptions.ExceptionResponse;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.forms.ChangePasswordForm;
import com.shoppingmall.domain.members.forms.MemberJoinForm;
import com.shoppingmall.domain.members.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;

    /**
     * API로 회원가입
     * @param form
     * @return
     */
    @PostMapping
    public ResponseEntity join(@Validated @ModelAttribute MemberJoinForm form) throws IOException {

        if(memberService.checkDuplicateId(form.getLoginId())){
            ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), "아이디 중복", "다른 아이디를 입력해주세요");
            return ResponseEntity.badRequest().body(er);
        }

        // 가입 성공
        Long savedMemberId = memberService.join(form);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedMemberId).toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * 회원 한명 조회
     * @param member
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<MemberResponse> oneMember(@PathVariable("id") Member member){

        MemberResponse memberResponse = new MemberResponse(member);

        return ResponseEntity.ok().body(memberResponse);
    }

    /**
     * 비밀번호 변경
     * @param member
     * @param form
     * @return
     */
    @PatchMapping("/{id}")
    public ResponseEntity passwordUpdate(@PathVariable("id") Member member, @RequestBody ChangePasswordForm form) {

        if(!member.getPassword().equals(form.getOldPassword())){
            ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), "비밀 번호 오류", "현재 비밀번호를 틀렸습니다.");
            return ResponseEntity.badRequest().body(er);
        }

        if(!form.getNewPassword().equals(form.getNewPasswordCheck())){
            ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), "비밀 번호 오류", "새 비밀번호와 새 비밀번호 확인이 일치하지 않습니다");
            return ResponseEntity.badRequest().body(er);
        }

        memberService.passwordChange(member, form);

        return ResponseEntity.ok().body("변경 성공");
    }



    // TODO 로그인 - 토큰.. JWT.. spring security 써야겠는데..
    // TODO 로그아웃 - 토큰 삭제
    // 첨부문서 요청은 구현하지 않겠다: 텍스트면 괜찮은데 이미지 첨부된 PDF 같은 거면 깨져
    // 첨부문서를 검토하고 판매 승인을 해야하므로 이것도 뷰에서 파일 보고 검토하고 바로 승인하는 게 자연스럽기 때문에 생략
}
