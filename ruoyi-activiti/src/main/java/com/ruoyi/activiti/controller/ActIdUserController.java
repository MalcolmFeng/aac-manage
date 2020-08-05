package com.ruoyi.activiti.controller;

import com.github.pagehelper.Page;
import com.ruoyi.activiti.common.MVConstructor;
import com.ruoyi.activiti.common.TokenCookieHandler;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.activiti.domain.ActIdUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import com.ruoyi.system.service.ISysUserService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流程用户Controller
 *
 * @author Xianlu Tech
 * @date 2019-10-02
 */
@Controller
@RequestMapping("/user")
public class ActIdUserController extends BaseController {
    private String prefix = "user";

    @Autowired
    private IdentityService identityService;
    @Autowired
    private ISysUserService userService;

    @GetMapping()
    public ModelAndView user(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix+"/user");
        return modelAndView;
    }

    /**
     * 查询流程用户列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ActIdUser query)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        UserQuery userQuery = identityService.createUserQuery();
        if (StringUtils.isNotBlank(query.getId())) {
            userQuery.userId(query.getId());
        }
        if (StringUtils.isNotBlank(query.getFirst())) {
            userQuery.userFirstNameLike("%" + query.getFirst() + "%");
        }
        if (StringUtils.isNotBlank(query.getEmail())) {
            userQuery.userEmailLike("%" + query.getEmail() + "%");
        }
        List<User> userList = userQuery.listPage((pageNum - 1) * pageSize, pageSize);
        Page<ActIdUser> list = new Page<>();
        list.setTotal(userQuery.count());
        list.setPageNum(pageNum);
        list.setPageSize(pageSize);
        for (User user: userList) {
            ActIdUser idUser = new ActIdUser();
            idUser.setId(user.getId());
            idUser.setFirst(user.getFirstName());
            idUser.setEmail(user.getEmail());
            list.add(idUser);
        }
        return getDataTable(list);
    }

    /**
     * 选择系统用户
     */
    @GetMapping("/authUser/selectUser")
    public ModelAndView selectUser(String taskId, HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("currentUser", ShiroUtils.getSysUser());
        modelAndView.addObject("taskId", taskId);
        modelAndView.setViewName(prefix+"/selectUser");
        return modelAndView;
    }

    @PostMapping("/systemUserList")
    @ResponseBody
    public TableDataInfo systemUserList(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

}
