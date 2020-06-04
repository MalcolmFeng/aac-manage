package com.ruoyi.framework.shiro.web.filter;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.framework.util.TokenUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import com.ruoyi.system.utils.JWTUtil;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 鉴权过滤器
 */
public class TokenAuthenFilter extends AccessControlFilter {

    public static int EXPIRE_TIME= 60*60*1000;
    private String clientId = "net5ijy";  //客户端Id
    private String clientSecret = "123456"; // 客户端密钥
    private String authUrl = "http://localhost:7002/auth/authByJWT";  // 认证中心token校验


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String token = (String) request.getAttribute("token");
        if (token != null){
            try{
                // 获取到token，解析jwt
                SysUser user = GetUserFromJWT.getUserFromJWT();

                // 判断是否为管理员，如果是，允许访问所有菜单
                Boolean adminFlag = false;
                for (SysRole role : user.getRoles()){
                    if (role.getRoleId() == 1){
                        adminFlag = true;
                        break;
                    }
                }
                if (adminFlag){
                    return true;
                }

                // 登陆后统一允许访问控制台以及介绍界面
                if (request.getRequestURI().equals("/") || request.getRequestURI().equals("/system/main")){
                    return true;
                }

                // 如果为非管理员，查询标识菜单权限的 perms 携带token访问认证服务器进行鉴权
                MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
                formData.add("uri",request.getRequestURI());
                formData.add("token",token);

                HttpHeaders headers = new HttpHeaders();
                String authorization = "Basic " + Base64.getUrlEncoder().encodeToString(( clientId+ ":" + clientSecret ).getBytes());
                headers.set("Authorization",authorization);
                headers.set("Content-Type","application/x-www-form-urlencoded");

                try{
                    Map map = (Map)new RestTemplate().exchange(authUrl, HttpMethod.POST, new HttpEntity(formData, headers), Map.class, new Object[0]).getBody();
                    if ((Integer)map.get("code") == 200){
                        // 鉴权有效，允许访问资源
                        return true;
                    }
                }catch (Exception e){
                    System.out.println(e.toString());
                }

//                JSONObject payLoad = JWTUtil.getPayLoadJsonByJWT(token);
//                String roleSetString = payLoad.getString("rolesSet");
//                String uri = request.getRequestURI();

            }catch (Exception e){
                System.out.println(e.toString());
            }
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;

        request.getRequestDispatcher("/common/unauth").forward(servletRequest,servletResponse);
        return false;
    }

}
