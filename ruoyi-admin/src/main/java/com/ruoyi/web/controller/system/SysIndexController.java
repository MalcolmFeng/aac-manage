package com.ruoyi.web.controller.system;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.utils.JWTUtil;
import com.ruoyi.web.controller.tool.TokenCookieHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import com.ruoyi.common.config.Global;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysMenuService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 首页 业务处理
 * 
 * @author ruoyi
 */
@Controller
public class SysIndexController extends BaseController
{
    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    ISysUserService sysUserService;

    // 系统首页
    @GetMapping("/index")
    public String index(ModelMap mmap,HttpServletRequest request, HttpServletResponse response) throws IOException {
        SysUser user = GetUserFromJWT.getUserFromJWT();

        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        JSONArray rolesArray = JSON.parseArray(jwtPayload.getString("rolesSet"));
        if(rolesArray.size() != 0) {
            Long roleId = rolesArray.getLong(0);

            user.setRoleId(roleId);
        }

        // 根据用户id取出菜单
        List<SysMenu> menus = menuService.selectMenusByUser(user);
        mmap.put("menus", menus);
        mmap.put("user", user);
        mmap.put("sideTheme", configService.selectConfigByKey("sys.index.sideTheme"));
        mmap.put("skinName", configService.selectConfigByKey("sys.index.skinName"));
        mmap.put("copyrightYear", Global.getCopyrightYear());
        mmap.put("demoEnabled", Global.isDemoEnabled());

        TokenCookieHandler.setCookieToken(request,response);

        return "index";
    }

    // 切换主题
    @GetMapping("/system/switchSkin")
    public String switchSkin(ModelMap mmap)
    {
        return "skin";
    }

    // 系统介绍
    @GetMapping("/system/main")
    public String main(ModelMap mmap)
    {
        mmap.put("version", Global.getVersion());
        return "main";
    }
}
