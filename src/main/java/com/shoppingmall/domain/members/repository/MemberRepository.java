package com.shoppingmall.domain.members.repository;

import com.shoppingmall.domain.enums.Grade;
import com.shoppingmall.domain.members.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 로그인 아이디로 회원 조회
     * @param loginId
     * @return
     */
    Optional<Member> findByLoginId(String loginId);

    /**
     * 회원 등급 별 조회
     * @param grade
     * @return
     */
    List<Member> findByGrade(@Param("grade") Grade grade);

    /**
     * 판매가 가능한지 여부에 따른 조회
     * @param available
     * @return
     */
    List<Member> findBySaleAvailable(@Param("available") Boolean available);

    /**
     * 첨부파일이 있는 멤버만 조회
     * @param pageable
     * @return
     */
    Page<Member> findMembersByFileNotNull(Pageable pageable);

    /**
     * id로 회원 조회. 첨부파일까지 한번에 가져오기
     * @param id
     * @return
     */
    @EntityGraph(attributePaths = {"file"})
    Optional<Member> findMemberById(Long id);
}
