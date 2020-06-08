package com.ruoyi.web.controller.system;

import java.util.List;
import java.util.Set;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.utils.security.BcryptUtil;
import com.ruoyi.framework.util.TokenUtils;
import com.ruoyi.system.domain.SysPost;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.service.ISysMenuService;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import com.ruoyi.system.utils.JWTUtil;
import com.ruoyi.web.controller.tool.MVConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.service.ISysPostService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户信息
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user")
public class SysUserController extends BaseController
{
    private String prefix = "system/user";

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysPostService postService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private ISysMenuService sysMenuService;

//    @RequiresPermissions("system:user:view")
    @GetMapping()
    public ModelAndView user(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        modelAndView.setViewName(prefix+"/user");
        return modelAndView;

//        return prefix + "/user";
    }

//    @RequiresPermissions("system:user:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(SysUser user)
    {
        startPage();
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");
        user.setUserId(userId);
        user.setClientId(clientId);
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    @PostMapping("/selectUserByLoginName")
    @ResponseBody
    public SysUser selectUserByLoginName(String loginName)
    {
        SysUser user = userService.selectUserByLoginName(loginName);
        return user;
    }

    @Log(title = "用户管理", businessType = BusinessType.EXPORT)
//    @RequiresPermissions("system:user:export")
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(SysUser user)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");

        user.setClientId(clientId);
        List<SysUser> list = userService.selectUserList(user);
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.exportExcel(list, "用户数据");
    }

    @Log(title = "用户管理", businessType = BusinessType.IMPORT)
//    @RequiresPermissions("system:user:import")
    @PostMapping("/importData")
    @ResponseBody
    public AjaxResult importData(MultipartFile file, boolean updateSupport, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        List<SysUser> userList = util.importExcel(file.getInputStream());
        String operName = GetUserFromJWT.getUserFromJWT().getLoginName();
        String message = userService.importUser(userList, updateSupport, operName);
        return AjaxResult.success(message);
    }

//    @RequiresPermissions("system:user:view")
    @GetMapping("/importTemplate")
    @ResponseBody
    public AjaxResult importTemplate()
    {
        ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        return util.importTemplateExcel("用户数据");
    }

    /**
     * 新增用户
     */
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");

        SysRole role = new SysRole();
        role.setClientId(clientId);

        SysPost post = new SysPost();
        post.setClientId(clientId);

        mmap.put("roles", roleService.selectRoleList(role));
        mmap.put("posts", postService.selectPostList(post));
        return prefix + "/add";
    }

    /**
     * 新增保存用户
     */
//    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated SysUser user)
    {
        if (UserConstants.USER_NAME_NOT_UNIQUE.equals(userService.checkLoginNameUnique(user.getLoginName())))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        }
        else if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("新增用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        String salt = BCrypt.gensalt();
        user.setSalt(salt);
        user.setPassword(BcryptUtil.encode(user.getPassword(),salt));
        user.setCreateBy(ShiroUtils.getLoginName());

        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");
        user.setClientId(clientId);

        return toAjax(userService.insertUser(user));
    }

    /**
     * 修改用户
     */
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        String clientId = jwtPayload.getString("clients");

        mmap.put("user", userService.selectUserById(userId));
        mmap.put("roles", roleService.selectRolesByUserId(userId,clientId));
        mmap.put("posts", postService.selectPostsByUserId(userId,clientId));
        return prefix + "/edit";
    }

    /**
     * 修改保存用户
     */
//    @RequiresPermissions("system:user:edit")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated SysUser user)
    {
        userService.checkUserAllowed(user);
        if (UserConstants.USER_PHONE_NOT_UNIQUE.equals(userService.checkPhoneUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        else if (UserConstants.USER_EMAIL_NOT_UNIQUE.equals(userService.checkEmailUnique(user)))
        {
            return error("修改用户'" + user.getLoginName() + "'失败，邮箱账号已存在");
        }
        user.setUpdateBy(ShiroUtils.getLoginName());
        return toAjax(userService.updateUser(user));
    }

//    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @GetMapping("/resetPwd/{userId}")
    public String resetPwd(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        mmap.put("user", userService.selectUserById(userId));
        return prefix + "/resetPwd";
    }

//    @RequiresPermissions("system:user:resetPwd")
    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwdSave(SysUser user)
    {
        userService.checkUserAllowed(user);
        String salt = BCrypt.gensalt();
        user.setSalt(salt);
        user.setPassword(BcryptUtil.encode(user.getPassword(),salt));
        if (userService.resetUserPwd(user) > 0)
        {
            if (GetUserFromJWT.getUserFromJWT().getUserId().longValue() == user.getUserId().longValue())
            {
                ShiroUtils.setSysUser(userService.selectUserById(user.getUserId()));
            }
            return success();
        }
        return error();
    }

    /**
     * 进入授权角色页
     */
    @GetMapping("/authRole/{userId}")
    public String authRole(@PathVariable("userId") Long userId, ModelMap mmap)
    {
        SysUser user = userService.selectUserById(userId);
        // 获取用户所属的角色列表
        List<SysUserRole> userRoles = userService.selectUserRoleByUserId(userId);
        mmap.put("user", user);
        mmap.put("userRoles", userRoles);
        return prefix + "/authRole";
    }

    /**
     * 用户授权角色
     */
//    @RequiresPermissions("system:user:add")
    @Log(title = "用户管理", businessType = BusinessType.GRANT)
    @PostMapping("/authRole/insertAuthRole")
    @ResponseBody
    public AjaxResult insertAuthRole(Long userId, Long[] roleIds)
    {
        userService.insertUserAuth(userId, roleIds);
        return success();
    }

//    @RequiresPermissions("system:user:remove")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        try
        {
            return toAjax(userService.deleteUserByIds(ids));
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    /**
     * 校验用户名
     */
    @PostMapping("/checkLoginNameUnique")
    @ResponseBody
    public String checkLoginNameUnique(SysUser user)
    {
        return userService.checkLoginNameUnique(user.getLoginName());
    }

    /**
     * 校验手机号码
     */
    @PostMapping("/checkPhoneUnique")
    @ResponseBody
    public String checkPhoneUnique(SysUser user)
    {
        return userService.checkPhoneUnique(user);
    }

    /**
     * 校验email邮箱
     */
    @PostMapping("/checkEmailUnique")
    @ResponseBody
    public String checkEmailUnique(SysUser user)
    {
        return userService.checkEmailUnique(user);
    }

    /**
     * 用户状态修改
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
//    @RequiresPermissions("system:user:edit")
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(SysUser user)
    {
        userService.checkUserAllowed(user);
        return toAjax(userService.changeStatus(user));
    }
}