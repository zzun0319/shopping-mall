package com.shoppingmall.interceptors;

import com.shoppingmall.domain.members.dtos.MemberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class SalesAvailableCheckInterceptor implements HandlerInterceptor {

    /**
     * 판매권한이 있는지 체크하는 인터셉터
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

        if(!loginMember.getSaleAvailable()){

            log.info("판매 권한이 없는 회원 요청");
            response.sendRedirect("/");

            return false;
        }

        return true;
    }
}
