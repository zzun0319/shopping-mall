package com.shoppingmall.domain.members.service;

import com.shoppingmall.api.domains.member.dtos.RequestJoin;
import com.shoppingmall.domain.members.AttachedFile;
import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.dtos.PermitDto;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.members.forms.ChangePasswordForm;
import com.shoppingmall.domain.members.forms.MemberJoinForm;
import com.shoppingmall.domain.members.forms.MemberLoginForm;
import com.shoppingmall.utils.FileStoreUtil;
import com.shoppingmall.exceptions.NoSuchMemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final FileStoreUtil fileStoreUtil;

    /**
     * 회원가입시 아이디 중복 체크
     * @param loginId
     * @return 이미 있는 아이디면 true 리턴
     */
    public boolean checkDuplicateId(String loginId) {
        Optional<Member> om = memberRepository.findByLoginId(loginId);
        return om.isPresent();
    }

    /**
     * 회원 가입
     * @param form
     * @return 가입 후 할당된 member_id 반환
     */
    @Transactional
    public Long join(MemberJoinForm form) throws IOException {

        Member member = Member.createMember(form.getName(), form.getLoginId(), form.getPassword());

        // 첨부파일이 있으면 저장
        if(form.getSalesPermissionFile() != null && !form.getSalesPermissionFile().isEmpty()){
            AttachedFile attachedFile = fileStoreUtil.storeAttachedFile(form.getSalesPermissionFile());
            member.setFile(attachedFile);
        }

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();
    }

    /**
     * 로그인 처리
     * @param loginForm
     * @return
     */
    public Member login(MemberLoginForm loginForm) {
        Optional<Member> om = memberRepository.findByLoginId(loginForm.getLoginId());
        return om.filter(member -> member.getPassword().equals(loginForm.getPassword())).orElse(null);
    }

    /**
     * 비밀번호 변경 메서드
     * 컨트롤러에서 bindingResult에 Validation 결과를 담아서 뷰에 응답해야할 수도 있기 때문에
     * 기존 비밀번호와 일치하는지, 그리고 새로 입력한 비밀번호와 확인이 일치하는지는 컨트롤러에서 체크한다.
     * @param form
     * @return 바뀌었다면 바뀐 멤버의 id가 리턴된다.
     */
    @Transactional
    public Long passwordChange(Member member, ChangePasswordForm form){

        member.changePassword(form.getNewPassword());
        return member.getId();
    }

    /**
     * 뷰로 판매허가 변경 메서드
     * @param permitDto
     */
    @Transactional
    public void updatePermission(PermitDto permitDto) {
        Optional<Member> om = memberRepository.findByLoginId(permitDto.getLoginId());
        Member member = om.orElseThrow(() -> new NoSuchMemberException("존재하지 않는 회원"));
        member.permitSaleChange("abc1234", permitDto.getSaleAvailable());
    }

    public void deleteMember(Member member, String currentPassword) throws Exception {

        if(!member.getPassword().equals(currentPassword)){
            throw new Exception();
        }

        memberRepository.delete(member);

    }

    public Long joinApi(RequestJoin requestJoin) {

        Member member = Member.createMember(requestJoin.getName(), requestJoin.getLoginId(), requestJoin.getPassword());

        Member savedMember = memberRepository.save(member);
        return savedMember.getId();

    }
}
