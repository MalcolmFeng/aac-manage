package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.common.MVConstructor;
import com.ruoyi.activiti.common.TokenCookieHandler;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.activiti.domain.BizTodoItem;
import com.ruoyi.activiti.service.IBizTodoItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 待办事项Controller
 *
 * @author Xianlu Tech
 * @date 2019-11-08
 */
@Controller
@RequestMapping("/todoitem")
public class BizTodoItemController extends BaseController {
    private String prefix = "todoitem";

    @Autowired
    private IBizTodoItemService bizTodoItemService;

    @GetMapping("/todoListView")
    public ModelAndView todoListView(HttpServletRequest request, HttpServletResponse response) {
        BizTodoItem todoItem = new BizTodoItem();
        todoItem.setTodoUserId(ShiroUtils.getLoginName());
        todoItem.setIsHandle("0");
        List<BizTodoItem> todoItemList = bizTodoItemService.selectBizTodoItemList(todoItem);

        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("todoItemList", todoItemList);
        modelAndView.setViewName(prefix+"/todoList");
        return modelAndView;

    }

    @GetMapping()
    public ModelAndView todoitem(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("currentUser", ShiroUtils.getSysUser());
        modelAndView.setViewName(prefix+"/todoitem");
        return modelAndView;
    }

    /**
     * 查询待办事项列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizTodoItem bizTodoItem) {
        bizTodoItem.setIsHandle("0");
//        if (!SysUser.isAdmin(ShiroUtils.getUserId())) {
        bizTodoItem.setTodoUserId(ShiroUtils.getLoginName());
//        }
        startPage();
        List<BizTodoItem> list = bizTodoItemService.selectBizTodoItemList(bizTodoItem);
        return getDataTable(list);
    }

    @GetMapping("/doneitemView")
    public ModelAndView doneitem(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix+"/doneitem");
        return modelAndView;
    }

    /**
     * 查询已办事项列表
     */
    @PostMapping("/doneList")
    @ResponseBody
    public TableDataInfo doneList(BizTodoItem bizTodoItem) {
        bizTodoItem.setIsHandle("1");
//        if (!SysUser.isAdmin(ShiroUtils.getUserId())) {
        bizTodoItem.setTodoUserId(ShiroUtils.getLoginName());
//        }
        startPage();
        List<BizTodoItem> list = bizTodoItemService.selectBizTodoItemList(bizTodoItem);
        return getDataTable(list);
    }

    /**
     * 导出待办事项列表
     */
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BizTodoItem bizTodoItem) {
        bizTodoItem.setIsHandle("0");
        bizTodoItem.setTodoUserId(ShiroUtils.getLoginName());
        List<BizTodoItem> list = bizTodoItemService.selectBizTodoItemList(bizTodoItem);
        ExcelUtil<BizTodoItem> util = new ExcelUtil<BizTodoItem>(BizTodoItem.class);
        return util.exportExcel(list, "todoitem");
    }

    /**
     * 导出已办事项列表
     */
    @PostMapping("/doneExport")
    @ResponseBody
    public AjaxResult doneExport(BizTodoItem bizTodoItem) {
        bizTodoItem.setIsHandle("1");
        List<BizTodoItem> list = bizTodoItemService.selectBizTodoItemList(bizTodoItem);
        ExcelUtil<BizTodoItem> util = new ExcelUtil<BizTodoItem>(BizTodoItem.class);
        return util.exportExcel(list, "todoitem");
    }

    /**
     * 新增待办事项
     */
    @GetMapping("/add")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix+"/add");
        return modelAndView;
    }

    /**
     * 新增保存待办事项
     */
    @Log(title = "待办事项", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BizTodoItem bizTodoItem) {
        return toAjax(bizTodoItemService.insertBizTodoItem(bizTodoItem));
    }

    /**
     * 修改待办事项
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        BizTodoItem bizTodoItem = bizTodoItemService.selectBizTodoItemById(id);

        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("bizTodoItem", bizTodoItem);
        modelAndView.setViewName(prefix+"/edit");
        return modelAndView;
    }

    /**
     * 修改保存待办事项
     */
    @Log(title = "待办事项", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BizTodoItem bizTodoItem) {
        return toAjax(bizTodoItemService.updateBizTodoItem(bizTodoItem));
    }

    /**
     * 删除待办事项
     */
    @Log(title = "待办事项", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(bizTodoItemService.deleteBizTodoItemByIds(ids));
    }
}
