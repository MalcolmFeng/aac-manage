package com.ruoyi.activiti.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 将token设置到cookie
 */
public class TokenCookieHandler {

    public static void setCookieToken(HttpServletRequest request, HttpServletResponse response){
        String token = request.getParameter("token");
        if (token!=null && !token.equals("")){
            Cookie cookie = new Cookie("token", token);
            cookie.setMaxAge(120 * 60 * 1000);// 设置为30min
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }
}
