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
import org.springframework.beans.factory.annotation.Value;
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

//    private String authServer = "http://172.19.221.76:7002";
    private String authServer = "http://localhost:7002";
//    private String authServer = "http://172.26.212.224:7002";
    private String clientId = "ruoyoiSystem";
    private String clientSecret = "123456";

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        // 如果没有携带token，拒绝访问
        String token = TokenUtils.getTokenFromRequest(request);

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
            Map map = (Map) new RestTemplate().exchange(authServer+"/oauth/check_token", HttpMethod.POST, new HttpEntity(formData, headers), Map.class, new Object[0]).getBody();

            // 给后边的过滤器使用
            request.setAttribute("token",token);

            // 校验token成功，允许访问
            return true;
        } catch (Exception e) {
            // 校验token无效，拒绝访问
            return false;
        }
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        // token无效，跳转到登录页
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);

        // 获取请求前缀
        String url = request.getRequestURL().toString();
        String[] urls = url.split("\\/");
        String prefix = urls[0] + "//" + urls[2] ;
        try{
            prefix = prefix + "/" + urls[3];
        }catch (Exception e){
            // 并且访问的url没有context-url
        }

        // 构造认证中心登录地址
        String loginUrl = authServer + "/oauth/authorize?response_type=code&client_id="+
                clientId + "&scope=all&redirect_uri="+ prefix +
                "/handler/code?target="+ request.getRequestURL();

        // 重定向地址到认证中心登陆页面
        wrapper.sendRedirect(loginUrl);

        return false;
    }
}
