package com.shoppingmall.domain.members.controller;

import com.shoppingmall.domain.members.AttachedFile;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.PermitApiDto;
import com.shoppingmall.domain.members.dtos.PermitDto;
import com.shoppingmall.domain.members.forms.ChangePasswordForm;
import com.shoppingmall.domain.members.repository.AttachedFileRepository;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.members.service.MemberService;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.forms.MemberJoinForm;
import com.shoppingmall.domain.members.forms.MemberLoginForm;
import com.shoppingmall.domain.utils.FileStoreUtil;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberRepository memberRepository;
    private final MemberService memberService;

    private final AttachedFileRepository fileRepository;

    private final FileStoreUtil fileStoreUtil;

    /**
     * 회원가입 페이지로 이동
     */
    @GetMapping
    public String joinForm(Model model){
        model.addAttribute("joinForm", new MemberJoinForm());
        return "member/join-form";
    }

    /**
     * 회원 가입 처리
     * @param joinForm
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String join(@Validated @ModelAttribute("joinForm") MemberJoinForm joinForm, BindingResult bindingResult) throws IOException {

        if(memberService.checkDuplicateId(joinForm.getLoginId())){
            bindingResult.rejectValue("loginId", "duplicate.loginId");
        }

        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        memberService.join(joinForm);
        return "redirect:/";
    }

    /**
     * 로그인 페이지로 이동
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("loginForm", new MemberLoginForm());
        return "member/login-form";
    }

    /**
     * 로그인 처리
     * @param loginForm
     * @param bindingResult
     * @param request
     * @param redirectURL
     * @return
     */
    @PostMapping("/login")
    public String login(@Validated @ModelAttribute("loginForm") MemberLoginForm loginForm, BindingResult bindingResult,
                        HttpServletRequest request, @RequestParam(defaultValue = "/") String redirectURL) {

        Member member = memberService.login(loginForm);

        if(member == null){
            bindingResult.reject("fail.login");
        }

        if(bindingResult.hasErrors()){
            return "member/login-form";
        }

        MemberDto memberDto = new MemberDto(member);

        HttpSession session = request.getSession();
        session.setAttribute("loginMember", memberDto);
        session.setMaxInactiveInterval(1800); // 30분간 아무 요청 없으면 세션만료

        return "redirect:" + redirectURL;
    }

    /**
     * 로그아웃
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false); // 있으면 가져오고, 없다고 새로 만들지 말고.

        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }


    /**
     * 판매 허가를 위해 파일을 첨부한 사람들의 리스트 보기
     * @param model
     * @param pageable
     * @return
     */
    @GetMapping("/permit")
    public String permitSalesPage(Model model, Pageable pageable) {
        Page<Member> members = memberRepository.findMembersByFileNotNull(pageable);
        Page<MemberDto> memberDtos = members.map(MemberDto::new);
        model.addAttribute("page", memberDtos);
        return "member/permit";
    }

    /**
     * 첨부파일을 볼 수 있는 멤버 상세보기 페이지
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/permit/{memberId}")
    public String downloadAttachedFilePage(@PathVariable("memberId") Long id, Model model) {
        Optional<Member> om = memberRepository.findMemberById(id);
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원"));
        PermitDto permitDto = new PermitDto(member);
        model.addAttribute("member", permitDto);
        return "member/permitOne";
    }

    /**
     * 뷰로 판매 승인할 수 있는 메서드.
     * 뷰로 만들어야할 것 같아서 만들지만.. api방식으로만 하는 게 맞지 않을까
     * @param permitDto
     * @return
     */
    @PostMapping("/permit/{memberId}")
    public String permit(@ModelAttribute("member") PermitDto permitDto) {
        memberService.updatePermission(permitDto);
        return "redirect:/members/permit";
    }

    /**
     * api로 승인 메서드
     * 일단 만들어두고 api 구현시 옮기기
     * @return
     */
    @ResponseBody
    @PatchMapping("/permit")
    public String permitApi(@RequestBody PermitApiDto permitApiDto) {
        memberService.changePermissionWithApi(permitApiDto);
        return "Permission Change Success"; // 이 부분도 Swagger 써서 여러 다음 행동 진행 가능하도록 바꾸기
    }

    /**
     * 첨부 파일 다운로드
     * @param id
     * @return
     * @throws MalformedURLException
     * @throws NoSuchFileException
     */
    @GetMapping("/permit/download/{fileId}")
    public ResponseEntity<Resource> downloadAttachedFile(@PathVariable("fileId") Long id) throws MalformedURLException, NoSuchFileException {

        Optional<AttachedFile> oa = fileRepository.findById(id);
        AttachedFile attachedFile = oa.orElseThrow(() -> new NoSuchFileException("해당 파일은 존재하지 않습니다."));
        String originalFileName = attachedFile.getOriginalFileName();
        String storeFileName = attachedFile.getStoreFileName();

        UrlResource resource = new UrlResource("file:" + fileStoreUtil.getAttachedFullPath(storeFileName));

        String encodedUploadFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\""; // 인코딩한 파일 이름.

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/update/{memberId}")
    public String updatePasswordPage(@PathVariable("memberId") Long memberId, Model model){
        model.addAttribute("form", new ChangePasswordForm());
        return "member/password-change-form";
    }

    @PostMapping("/update/{memberId}")
    public String passwordUpdate(@PathVariable("memberId") Member member, @Validated @ModelAttribute("form") ChangePasswordForm form, BindingResult bindingResult) {

        if(!member.getPassword().equals(form.getOldPassword())){
            bindingResult.rejectValue("oldPassword", "wrongPwd");
        }

        if(!form.getNewPassword().equals(form.getNewPasswordCheck())){
            bindingResult.rejectValue("newPasswordCheck", "wrongPwd");
        }

        if(bindingResult.hasErrors()){
            return "member/password-change-form";
        }

        memberService.passwordChange(member, form);

        return "redirect:/";
    }
}
