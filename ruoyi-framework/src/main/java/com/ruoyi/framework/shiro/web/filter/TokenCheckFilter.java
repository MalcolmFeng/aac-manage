package com.ruoyi.framework.shiro.web.filter;

import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.framework.util.TokenUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.utils.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.util.Base64;
import java.util.Map;

/**
 * 认证过滤器
 */
public class TokenCheckFilter extends AccessControlFilter {

    @Autowired
    ISysUserService sysUserService;

    public static int EXPIRE_TIME = 60 * 60 * 1000;
    private String clientId = "tencent";  //客户端Id
    private String clientSecret = "123456"; // 客户端密钥
    private String checkUrl = "http://localhost:7002/oauth/check_token";  // 认证中心token校验
    private String loginUrl = "http://localhost:7002/oauth/authorize?response_type=code&client_id=tencent&scope=all&redirect_uri=http://localhost:80/handler/code?target="; // 认证中心登录页

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        if (request.getRequestURL().indexOf("handler/code") != -1) {
            return true;
        }

        String token = TokenUtils.getTokenFromHttpServletRequest();
        if (token == null){
            return false;
        }

        // 校验token
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("token", token);

        HttpHeaders headers = new HttpHeaders();
        String authorization = "Basic " + Base64.getUrlEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", authorization);
        headers.set("Content-Type", "application/x-www-form-urlencoded");
        try {
            Map map = (Map) new RestTemplate().exchange(checkUrl, HttpMethod.POST, new HttpEntity(formData, headers), Map.class, new Object[0]).getBody();
            request.setAttribute("token",token); // 给下面的过滤器使用
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // token无效，跳转到登录页
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        wrapper.sendRedirect(loginUrl + request.getRequestURL());  // 重定向地址
        return false;
    }
}
