package com.ruoyi.framework.util;

import com.ruoyi.common.utils.ServletUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * 从request获取token
 */
public class TokenUtils {

    public static String getTokenFromHttpServletRequest(){
        HttpServletRequest request = ServletUtils.getRequest();

        // 分别在 param、header、cookies中查询Token
        return getTokenFromRequest(request);
    }

    public static String getTokenFromRequest(HttpServletRequest request) {
        // 分别在 param、header、cookies中查询Token
        String token = request.getParameter("token");
        if (token == null || token.equals("")) {
            token = request.getHeader("token");
        }
        if (token == null || token.equals("")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null){
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("token")) {
                        // 在cookies中找到Token之后，结束遍历
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }
        if (token == null || token.equals("")){
            token = (String) request.getAttribute("token");
        }
        return token;
    }
}
