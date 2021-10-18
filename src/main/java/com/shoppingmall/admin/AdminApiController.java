package com.shoppingmall.admin;

import com.shoppingmall.domain.members.Member;
import com.shoppingmall.domain.members.repository.MemberRepository;
import com.shoppingmall.domain.members.dtos.MemberDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminApiController {

    private final MemberRepository memberRepository;

    /**
     * 전체 멤버 리스트 조회하기
     * @param pageable
     * @return
     */
    @GetMapping("/members")
    public Page<MemberDto> memberList(@PageableDefault(size = 5, sort = "id") Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        Page<MemberDto> dtoPage = page.map(MemberDto::new);
        return dtoPage;
    }

}
