package com.ruoyi.web.controller.system;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import com.ruoyi.system.utils.JWTUtil;
import com.ruoyi.web.controller.tool.MVConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.Ztree;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 菜单信息
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/menu")
public class SysMenuController extends BaseController
{
    private String prefix = "system/menu";

    @Autowired
    private ISysMenuService menuService;

//    @RequiresPermissions("system:menu:view")
    @GetMapping()
    public ModelAndView menu()
    {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        modelAndView.setViewName(prefix+"/menu");
        return modelAndView;
//        return prefix + "/menu";
    }

//    @RequiresPermissions("system:menu:list")
    @PostMapping("/list")
    @ResponseBody
    public List<SysMenu> list(SysMenu menu)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");
        List<SysMenu> menuList = menuService.selectMenuList(menu, userId, clientId);
        return menuList;
    }

    /**
     * 删除菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
//    @RequiresPermissions("system:menu:remove")
    @GetMapping("/remove/{menuId}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.selectCountMenuByParentId(menuId) > 0)
        {
            return AjaxResult.warn("存在子菜单,不允许删除");
        }
        if (menuService.selectCountRoleMenuByMenuId(menuId) > 0)
        {
            return AjaxResult.warn("菜单已分配,不允许删除");
        }
        ShiroUtils.clearCachedAuthorizationInfo();
        return toAjax(menuService.deleteMenuById(menuId));
    }

    /**
     * 新增
     */
    @GetMapping("/add/{parentId}")
    public String add(@PathVariable("parentId") Long parentId, ModelMap mmap)
    {
        SysMenu menu = null;
        if (0L != parentId)
        {
            JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
            Long userId = jwtPayload.getLong("userId");
            String clientId = jwtPayload.getString("clients");

            menu = menuService.selectMenuById(parentId,clientId);
        }
        else
        {
            menu = new SysMenu();
            menu.setMenuId(0L);
            menu.setMenuName("主目录");
        }
        mmap.put("menu", menu);
        return prefix + "/add";
    }

    /**
     * 新增保存菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
//    @RequiresPermissions("system:menu:add")
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated SysMenu menu)
    {
        if (UserConstants.MENU_NAME_NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return error("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");

        menu.setClientId(clientId);
        menu.setCreateBy(ShiroUtils.getLoginName());
        ShiroUtils.clearCachedAuthorizationInfo();
        return toAjax(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @GetMapping("/edit/{menuId}")
    public String edit(@PathVariable("menuId") Long menuId, ModelMap mmap)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");

        mmap.put("menu", menuService.selectMenuById(menuId, clientId));
        return prefix + "/edit";
    }

    /**
     * 修改保存菜单
     */
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
//    @RequiresPermissions("system:menu:edit")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated SysMenu menu)
    {
        if (UserConstants.MENU_NAME_NOT_UNIQUE.equals(menuService.checkMenuNameUnique(menu)))
        {
            return error("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        menu.setUpdateBy(ShiroUtils.getLoginName());
        ShiroUtils.clearCachedAuthorizationInfo();
        return toAjax(menuService.updateMenu(menu));
    }

    /**
     * 选择菜单图标
     */
    @GetMapping("/icon")
    public String icon()
    {
        return prefix + "/icon";
    }

    /**
     * 校验菜单名称
     */
    @PostMapping("/checkMenuNameUnique")
    @ResponseBody
    public String checkMenuNameUnique(SysMenu menu)
    {
        return menuService.checkMenuNameUnique(menu);
    }

    /**
     * 加载角色菜单列表树
     */
    @GetMapping("/roleMenuTreeData")
    @ResponseBody
    public List<Ztree> roleMenuTreeData(SysRole role)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");
        List<Ztree> ztrees = menuService.roleMenuTreeData(role, userId, clientId);
        return ztrees;
    }

    /**
     * 加载所有菜单列表树
     */
    @GetMapping("/menuTreeData")
    @ResponseBody
    public List<Ztree> menuTreeData()
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");

        List<Ztree> ztrees = menuService.menuTreeData(userId,clientId);
        return ztrees;
    }

    /**
     * 选择菜单树
     */
    @GetMapping("/selectMenuTree/{menuId}")
    public String selectMenuTree(@PathVariable("menuId") Long menuId, ModelMap mmap)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");

        mmap.put("menu", menuService.selectMenuById(menuId,clientId));
        return prefix + "/tree";
    }
}