package com.ruoyi.activiti.controller;

import com.github.pagehelper.Page;
import com.ruoyi.activiti.common.MVConstructor;
import com.ruoyi.activiti.common.TokenCookieHandler;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.PageDomain;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.core.page.TableSupport;
import com.ruoyi.activiti.domain.ActIdGroup;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.util.ShiroUtils;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 流程用户组Controller
 *
 * @author Xianlu Tech
 * @date 2019-10-02
 */
@Controller
@RequestMapping("/group")
public class ActIdGroupController extends BaseController
{
    private String prefix = "group";

    @Autowired
    private IdentityService identityService;

    @GetMapping()
    public ModelAndView group(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("currentUser", ShiroUtils.getSysUser());
        modelAndView.setViewName(prefix+"/group");
        return modelAndView;
    }

    /**
     * 查询流程用户组列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(ActIdGroup query)
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();

        GroupQuery groupQuery = identityService.createGroupQuery();
        if (StringUtils.isNotBlank(query.getId())) {
            groupQuery.groupId(query.getId());
        }
        if (StringUtils.isNotBlank(query.getName())) {
            groupQuery.groupNameLike("%" + query.getName() + "%");
        }
        List<Group> groupList = groupQuery.listPage((pageNum - 1) * pageSize, pageSize);
        Page<ActIdGroup> list = new Page<>();
        list.setTotal(groupQuery.count());
        list.setPageNum(pageNum);
        list.setPageSize(pageSize);
        for (Group group: groupList) {
            ActIdGroup idGroup = new ActIdGroup();
            idGroup.setId(group.getId());
            idGroup.setName(group.getName());
            list.add(idGroup);
        }
        return getDataTable(list);
    }

}
