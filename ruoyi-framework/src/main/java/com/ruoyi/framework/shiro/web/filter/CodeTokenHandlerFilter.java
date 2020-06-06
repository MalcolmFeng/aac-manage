package com.ruoyi.framework.shiro.web.filter;

import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;

/**
 * 鉴权过滤器
 */
public class CodeTokenHandlerFilter extends AccessControlFilter {

    private String authServer = "http://localhost:7002";
    private String clientId = "tencent";
    private String clientSecret = "123456";

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        String code = request.getParameter("code");
        String target = request.getParameter("target");

        try {
            // 获取token
            MultiValueMap<String, String> formData1 = new LinkedMultiValueMap<>();
            formData1.add("grant_type", "authorization_code");
            formData1.add("scope", "all");
            formData1.add("redirect_uri", request.getRequestURL().toString() +"?target=" + target);
            formData1.add("code", code);

            HttpHeaders headers1 = new HttpHeaders();
            headers1.set("Authorization", "Basic " + Base64.getUrlEncoder().encodeToString(( clientId+ ":" + clientSecret).getBytes()));  // client-id: tencent

            Map map = (Map) new RestTemplate().exchange( authServer + "/oauth/token", HttpMethod.POST, new HttpEntity(formData1, headers1), Map.class, new Object[0]).getBody();
            String token = (String) map.get("access_token");

            request.setAttribute("token",token);
            request.setAttribute("target",target);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String token = (String) request.getAttribute("token");
        String target = (String) request.getAttribute("target");

        // 写入cookie
        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(120 * 60 * 1000);// 设置为30min
        cookie.setPath("/");
        response.addCookie(cookie);

        // 重定向到资源地址
        response.sendRedirect(target);
        return true;
    }

}
