package com.shoppingmall.api.domains.member.controller;

import com.shoppingmall.api.domains.member.dtos.MemberResponse;
import com.shoppingmall.api.domains.member.dtos.MemberResponseByLoginId;
import com.shoppingmall.api.domains.member.dtos.RequestJoin;
import com.shoppingmall.api.exceptions.ExceptionResponse;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.forms.ChangePasswordForm;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.members.service.MemberService;
import com.shoppingmall.enums.Grade;
import com.shoppingmall.exceptions.NoSuchMemberException;
import com.shoppingmall.exceptions.WrongStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberApiController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    /**
     * 전체 회원 조회. HATEOAS 적용
     * @param pageable
     * @return
     */
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<MemberResponse>>> allMembers(
            @PageableDefault(size = 2) Pageable pageable) {

        List<EntityModel<MemberResponse>> list = new ArrayList<>();

        Page<Member> all = memberRepository.findAll(pageable);
        Page<MemberResponse> dtoMap = all.map(MemberResponse::new);
        for (MemberResponse memberResponse : dtoMap) {
            EntityModel<MemberResponse> entityModel = EntityModel.of(memberResponse);
            WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).oneMemberByLoginId(memberResponse.getLoginId()));
            entityModel.add(linkTo.withRel("this-member (GET)"));
            list.add(entityModel);
        }

        // 현재 요청 URI
        WebMvcLinkBuilder self
                = linkTo(methodOn(this.getClass()).allMembers(PageRequest.of(pageable.getPageNumber(), pageable.getPageSize())));

        Link documentLink = Link.of("http://localhost:8080/swagger-ui/index.html");

        return ResponseEntity.ok(CollectionModel.of(list, self.withSelfRel(), documentLink.withRel("Document (Get)")));
    }

    /**
     * API로 회원가입
     * @param requestJoin
     * @return
     */
    @PostMapping
    public ResponseEntity join(@Validated @RequestBody RequestJoin requestJoin){

        if(memberService.checkDuplicateId(requestJoin.getLoginId())){
            ExceptionResponse er = new ExceptionResponse(LocalDateTime.now(), "아이디 중복", "다른 아이디를 입력해주세요");
            return ResponseEntity.badRequest().body(er);
        }

        // 가입 성공
        Long savedMemberId = memberService.joinApi(requestJoin);

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

    /**
     * 회원탈퇴
     * @param member
     * @param currentPassword
     * @return
     */
    @DeleteMapping("/{id}")
    public String deleteMember(@PathVariable("id") Member member, @RequestBody String currentPassword) {


        try {
            memberService.deleteMember(member, currentPassword);
        } catch(Exception e) {
            throw new WrongStatusException("비밀번호가 일치하지 않습니다");
        }

        return "회원 탈퇴가 정상처리되었습니다.";
    }

    /**
     * 로그인 아이디로 회원 1명 조회, HATEOAS 적용
     * @param loginId
     * @return
     */
    @GetMapping("/loginId/{loginId}")
    public ResponseEntity oneMemberByLoginId(@PathVariable("loginId") String loginId) {
        Optional<Member> om = memberRepository.findByLoginId(loginId);
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 LoginId입니다."));
        MemberResponseByLoginId memberDto = new MemberResponseByLoginId(member);

        EntityModel<MemberResponseByLoginId> entityModel = EntityModel.of(memberDto);

        WebMvcLinkBuilder linkForAllMembers = linkTo(methodOn(this.getClass()).allMembers(PageRequest.of(0, 2)));
        entityModel.add(linkForAllMembers.withRel("All-Members (GET)"));

        WebMvcLinkBuilder linkForPwdUpdate = linkTo(methodOn(this.getClass()).passwordUpdate(member, new ChangePasswordForm()));
        entityModel.add(linkForPwdUpdate.withRel("PWD-CHANGE (PATCH)"));

        Link documentLink = Link.of("http://localhost:8080/swagger-ui/index.html");
        entityModel.add(documentLink.withRel("Document (Get)"));

        return ResponseEntity.ok().body(entityModel);
    }

    /**
     * 회원 등급으로 조회
     * @param grade
     * @return
     */
    @GetMapping("/grade/{grade}")
    public ResponseEntity MembersByGrade(@PathVariable("grade") String grade) {

        Grade enumGrade = Grade.USER;

        try {
            enumGrade = Grade.valueOf(grade);
        } catch (IllegalArgumentException iae) {
            throw new WrongStatusException("GRADE는 USER, ADMIN, VIP 세 종류가 있습니다. 대소문자를 구별합니다");
        }

        List<Member> list = memberRepository.findByGrade(enumGrade);
        List<MemberResponse> collect = list.stream().map(MemberResponse::new).collect(Collectors.toList());
        return new ResponseEntity(collect, HttpStatus.OK);
    }

    /**
     * 판매 가능한 회원 조회
     * @return
     */
    @GetMapping("/salesAvailable")
    public ResponseEntity MembersBySales(){
        List<Member> members = memberRepository.findBySaleAvailable(true);
        List<MemberResponse> memberDtos = members.stream().map(MemberResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok().body(memberDtos);
    }


    // TODO 로그인 - 토큰.. JWT.. spring security 써야겠는데..
    // TODO 로그아웃 - 토큰 삭제
    // 첨부문서 요청은 구현하지 않겠다: 텍스트면 괜찮은데 이미지 첨부된 PDF 같은 거면 깨져
    // 첨부문서를 검토하고 판매 승인을 해야하므로 이것도 뷰에서 파일 보고 검토하고 바로 승인하는 게 자연스럽기 때문에 생략
}
