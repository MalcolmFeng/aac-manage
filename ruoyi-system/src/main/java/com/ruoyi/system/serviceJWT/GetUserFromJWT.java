package com.ruoyi.system.serviceJWT;

import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

@Component
public class GetUserFromJWT {

    private static String loginUrl = "http://localhost:7002/oauth/authorize?response_type=code&client_id=tencent&scope=all&redirect_uri=http://localhost:80/handler/code?target="; // 认证中心登录页

    public static SysUser getUserFromJWT(){
        HttpServletRequest request = ServletUtils.getRequest();
        HttpServletResponse response = ServletUtils.getResponse();

        SysUser user = null;
        // 取身份信息
        try {
            String token = request.getParameter("token");
            if (token == null) {
                token = request.getHeader("token");
            }
            if (token == null) {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("token")) {
                            // 在cookies中找到Token之后，结束遍历
                            token = cookie.getValue();
                            break;
                        }
                    }
                }
            }
            // 如果还是查询不到，返回false；
            if (token == null) {
                HttpServletResponseWrapper wrapper = new HttpServletResponseWrapper(response);
                wrapper.sendRedirect(loginUrl + request.getRequestURL());  // 重定向地址
                return user;
            }

            // 获取到token，解析jwt
            String user_name = JWTUtil.getUserNameByJWT(token);

            // 查询数据库获取用户详情
            ISysUserService sysUserService = SpringUtils.getBean(ISysUserService.class);
            user = sysUserService.selectUserByLoginName(user_name);
        }catch (Exception e){
            System.out.println(e);
        }
         return user;
    }

}
