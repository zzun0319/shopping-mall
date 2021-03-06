package com.shoppingmall.domain.members.controller;

import com.shoppingmall.domain.members.AttachedFile;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.PermitDto;
import com.shoppingmall.domain.members.forms.ChangePasswordForm;
import com.shoppingmall.domain.members.repository.AttachedFileRepository;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.members.service.MemberService;
import com.shoppingmall.domain.members.dtos.MemberDto;
import com.shoppingmall.domain.members.forms.MemberJoinForm;
import com.shoppingmall.domain.members.forms.MemberLoginForm;
import com.shoppingmall.exceptions.NoSuchFileExceptionLinked;
import com.shoppingmall.utils.FileStoreUtil;
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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;
import java.util.Optional;

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
     * ???????????? ???????????? ??????
     */
    @GetMapping
    public String joinForm(Model model){
        model.addAttribute("joinForm", new MemberJoinForm());
        return "member/join-form";
    }

    /**
     * ?????? ?????? ??????
     * @param joinForm
     * @param bindingResult
     * @return
     */
    @PostMapping
    public String join(@Validated @ModelAttribute("joinForm") MemberJoinForm joinForm,
                       BindingResult bindingResult, HttpServletResponse response) {

        if(memberService.checkDuplicateId(joinForm.getLoginId())){
            bindingResult.rejectValue("loginId", "duplicate.loginId");
        }

        if (bindingResult.hasErrors()) {
            return "member/join-form";
        }

        try {
            memberService.join(joinForm);
        } catch (IOException e) {
            log.error("IOException ?????? : {}", e.getMessage());
            response.sendError(400, "?????? ????????? ????????? ????????????.");
        } finally {
            return "redirect:/";
        }
    }

    /**
     * ????????? ???????????? ??????
     * @param model
     * @return
     */
    @GetMapping("/login")
    public String loginForm(Model model){
        model.addAttribute("loginForm", new MemberLoginForm());
        return "member/login-form";
    }

    /**
     * ????????? ??????
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
        session.setMaxInactiveInterval(1800); // 30?????? ?????? ?????? ????????? ????????????

        return "redirect:" + redirectURL;
    }

    /**
     * ????????????
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public String logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false); // ????????? ????????????, ????????? ?????? ????????? ??????.

        if(session != null){
            session.invalidate();
        }
        return "redirect:/";
    }

    /**
     * ?????? ????????? ?????? ????????? ????????? ???????????? ????????? ??????
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
     * ??????????????? ??? ??? ?????? ?????? ???????????? ?????????
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/permit/{memberId}")
    public String downloadAttachedFilePage(@PathVariable("memberId") Long id, Model model) {
        Optional<Member> om = memberRepository.findMemberById(id);

        Member member = om.orElseThrow(() -> new NoSuchMemberException("???????????? ?????? ???????????????."));

        PermitDto permitDto = new PermitDto(member);
        model.addAttribute("member", permitDto);
        return "member/permitOne";
    }

    /**
     * ?????? ?????? ????????? ??? ?????? ?????????.
     * ?????? ??????????????? ??? ????????? ????????????.. api??????????????? ?????? ??? ?????? ?????????
     * @param permitDto
     * @return
     */
    @PostMapping("/permit/{memberId}")
    public String permit(@ModelAttribute("member") PermitDto permitDto) {
        memberService.updatePermission(permitDto);
        return "redirect:/members/permit";
    }

    /**
     * ?????? ?????? ????????????
     * @param id
     * @return
     * @throws MalformedURLException
     * @throws NoSuchFileException
     */
    @GetMapping("/permit/download/{fileId}")
    public ResponseEntity<Resource> downloadAttachedFile(@PathVariable("fileId") Long id) throws MalformedURLException {

        Optional<AttachedFile> oa = fileRepository.findById(id);
        AttachedFile attachedFile = null;
        try {
            attachedFile = oa.orElseThrow(() -> new NoSuchFileException("?????? ????????? ???????????? ????????????."));
        } catch (NoSuchFileException nfe) {
            throw new NoSuchFileExceptionLinked(nfe);
        }

        String originalFileName = attachedFile.getOriginalFileName();
        String storeFileName = attachedFile.getStoreFileName();

        UrlResource resource = new UrlResource("file:" + fileStoreUtil.getAttachedFullPath(storeFileName));

        String encodedUploadFileName = UriUtils.encode(originalFileName, StandardCharsets.UTF_8);
        String contentDisposition = "attachment; filename=\"" + encodedUploadFileName + "\""; // ???????????? ?????? ??????.

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    /**
     * ???????????? ?????? ?????????
     * @param memberId
     * @param model
     * @return
     */
    @GetMapping("/update/{memberId}")
    public String updatePasswordPage(@PathVariable("memberId") Long memberId, Model model){
        model.addAttribute("form", new ChangePasswordForm());
        return "member/password-change-form";
    }

    /**
     * ???????????? ??????
     * @param member
     * @param form
     * @param bindingResult
     * @return
     */
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
