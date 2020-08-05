package com.ruoyi.framework.shiro.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 退出过滤器
 * 
 * @author ruoyi
 */
public class LogoutFilter extends AccessControlFilter
{
    private static final Logger log = LoggerFactory.getLogger(LogoutFilter.class);

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper((HttpServletResponse) servletResponse);
        // 重写cookie的过期时间为0，让token失效；
        Cookie cookie = new Cookie("token", "x");
        cookie.setMaxAge(0);// 设置为30min
        cookie.setPath("/");
        response.addCookie(cookie);

        Cookie cookie2 = new Cookie("JSESSIONID", "x");
        cookie2.setMaxAge(0);// 设置为30min
        cookie2.setPath("/");
        response.addCookie(cookie2);


//        wrapper.sendRedirect("http://localhost:7000");
        wrapper.sendRedirect("http://172.19.221.76:7000/admin");  // 济南
//        wrapper.sendRedirect("http://172.26.212.224:7000/admin");  // 天津
//        wrapper.sendRedirect("http://172.19.221.59:7000/admin");  // 测试

        return false;
    }
}
