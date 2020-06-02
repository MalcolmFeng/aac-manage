package com.ruoyi.web.controller.tool;

import com.ruoyi.common.utils.spring.SpringUtils;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;

/**
 * 构造 modelandview，并赋予权限
 * 管理员赋值  *:*:*
 */
public class MVConstructor {

    public static ModelAndView MVConstruct(){

        ISysMenuService sysMenuService = SpringUtils.getBean(ISysMenuService.class);
        SysUser user = GetUserFromJWT.getUserFromJWT();
        Set<String> perms = sysMenuService.selectPermsByUserId(user.getUserId());
        Boolean adminFlag = false;
        for (SysRole role : user.getRoles()){
            if (role.getRoleId() == 1){
                adminFlag = true;
                break;
            }
        }
        if (adminFlag){
            perms.add("*:*:*");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("perms",perms);
        return modelAndView;
    }
}
