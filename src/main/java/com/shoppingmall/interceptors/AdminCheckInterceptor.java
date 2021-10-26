package com.shoppingmall.interceptors;

import com.shoppingmall.enums.Grade;
import com.shoppingmall.domain.members.dtos.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class AdminCheckInterceptor implements HandlerInterceptor {

    /**
     * 관리자인지 체크하는 메서드
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        MemberDto loginMember = (MemberDto) session.getAttribute("loginMember");

        if(loginMember.getGrade() != Grade.ADMIN){

            log.info("관리자가 아닌 회원의 요청");
            response.sendRedirect("/");

            return false;
        }
        return true;
    }
}
