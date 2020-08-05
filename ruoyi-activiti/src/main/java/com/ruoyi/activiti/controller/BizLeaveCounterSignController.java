package com.ruoyi.activiti.controller;

import com.ruoyi.activiti.common.MVConstructor;
import com.ruoyi.activiti.common.TokenCookieHandler;
import com.ruoyi.activiti.domain.BizLeaveVo;
import com.ruoyi.activiti.service.IBizLeaveService;
import com.ruoyi.activiti.service.IProcessService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.SysUser;
import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 请假会签Controller
 *
 * @author Xianlu Tech
 * @date 2019-10-11
 */
@Controller
@RequestMapping("/process")
public class BizLeaveCounterSignController extends BaseController {
    private String prefix = "leaveCounterSign";

    @Autowired
    private IBizLeaveService bizLeaveService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private IProcessService processService;
    @Autowired
    private IdentityService identityService;

    @GetMapping()
    public ModelAndView leave(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("currentUser", ShiroUtils.getSysUser());
        modelAndView.setViewName(prefix+"/leave");
        return modelAndView;

    }

    /**
     * 查询请假业务列表
     */
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BizLeaveVo bizLeave) {
        if (!SysUser.isAdmin(ShiroUtils.getUserId())) {
            bizLeave.setCreateBy(ShiroUtils.getLoginName());
        }
        bizLeave.setType("process");
        startPage();
        List<BizLeaveVo> list = bizLeaveService.selectBizLeaveList(bizLeave);
        return getDataTable(list);
    }

    /**
     * 导出请假业务列表
     */
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BizLeaveVo bizLeave) {
        bizLeave.setType("process");
        List<BizLeaveVo> list = bizLeaveService.selectBizLeaveList(bizLeave);
        ExcelUtil<BizLeaveVo> util = new ExcelUtil<BizLeaveVo>(BizLeaveVo.class);
        return util.exportExcel(list, "leave");
    }

    /**
     * 新增请假业务
     */
    @GetMapping("/add")
    public ModelAndView add(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix+"/add");
        return modelAndView;
    }

    /**
     * 新增保存请假业务
     */
    @Log(title = "请假业务", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BizLeaveVo bizLeave) {
        Long userId = ShiroUtils.getUserId();
        if (SysUser.isAdmin(userId)) {
            return error("提交申请失败：不允许管理员提交申请！");
        }
        bizLeave.setType("process");
        return toAjax(bizLeaveService.insertBizLeave(bizLeave));
    }

    @GetMapping("/selectVerifyUser/{id}")
    public ModelAndView selectVerifyUser(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        List<Group> groups = identityService.createGroupQuery().list();
        Map<String, List<User>> usersByGroup = new HashMap<>(groups.size());
        for (Group group : groups) {
            List<User> users = identityService.createUserQuery().memberOfGroup(group.getId()).list();
            usersByGroup.put(group.getName(), users);
        }

        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("usersByGroup", usersByGroup);
        modelAndView.addObject("id", id);
        modelAndView.setViewName(prefix+"/selectVerifyUser");
        return modelAndView;
    }

    /**
     * 修改请假业务
     */
    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
        BizLeaveVo bizLeave = bizLeaveService.selectBizLeaveById(id);

        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("bizLeave", bizLeave);
        modelAndView.setViewName(prefix+"/edit");
        return modelAndView;
    }

    /**
     * 修改保存请假业务
     */
    @Log(title = "请假业务", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BizLeaveVo bizLeave) {
        return toAjax(bizLeaveService.updateBizLeave(bizLeave));
    }

    /**
     * 删除请假业务
     */
    @Log(title = "请假业务", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        return toAjax(bizLeaveService.deleteBizLeaveByIds(ids));
    }

    /**
     * 提交申请
     */
    @Log(title = "请假业务", businessType = BusinessType.UPDATE)
    @PostMapping( "/submitApply")
    @ResponseBody
    public AjaxResult submitApply(Long id, HttpServletRequest request, @RequestParam("users[]") String[] users) {
        BizLeaveVo leave = bizLeaveService.selectBizLeaveById(id);
        String applyUserId = ShiroUtils.getLoginName();
        Map<String, Object> variables = new HashMap<>();
        try {
            if (users != null && users.length > 0) {
                Object value =  Arrays.asList(users);
                variables.put("users", value);
            }
            bizLeaveService.submitApply(leave, applyUserId, "process", variables);
            return success();
        } catch (Exception e) {
            logger.error("error on submitApply leaveId {}, variables={}", new Object[]{leave.getId(), variables, e});
            return error("提交申请失败");
        }

    }

    @GetMapping("/leaveTodo")
    public ModelAndView todoView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix+"/leaveTodo");
        return modelAndView;
    }

    /**
     * 我的待办列表
     * @return
     */
    @PostMapping("/taskList")
    @ResponseBody
    public TableDataInfo taskList(BizLeaveVo bizLeave) {
        bizLeave.setType("process");
        List<BizLeaveVo> list = bizLeaveService.findTodoTasks(bizLeave, ShiroUtils.getLoginName());
        return getDataTable(list);
    }

    /**
     * 加载审批弹窗
     * @param taskId
     * @return
     */
    @GetMapping("/showVerifyDialog/{taskId}")
    public ModelAndView showVerifyDialog(@PathVariable("taskId") String taskId, HttpServletRequest request, HttpServletResponse response) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        BizLeaveVo bizLeave = bizLeaveService.selectBizLeaveById(new Long(processInstance.getBusinessKey()));

        String verifyName = task.getTaskDefinitionKey().substring(0, 1).toUpperCase() + task.getTaskDefinitionKey().substring(1);

        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix + "/task" + "Countersign");
        modelAndView.addObject("bizLeave", bizLeave);
        modelAndView.addObject("taskId", taskId);
        return modelAndView;
    }

    @GetMapping("/showFormDialog/{instanceId}")
    public ModelAndView showFormDialog(@PathVariable("instanceId") String instanceId, HttpServletRequest request, HttpServletResponse response) {
        String businessKey = processService.findBusinessKeyByInstanceId(instanceId);
        BizLeaveVo bizLeaveVo = bizLeaveService.selectBizLeaveById(new Long(businessKey));

        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.addObject("bizLeave", bizLeaveVo);
        modelAndView.setViewName(prefix + "/view");
        return modelAndView;
    }

    /**
     * 完成任务
     *
     * @return
     */
    @RequestMapping(value = "/complete/{taskId}", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public AjaxResult complete(@PathVariable("taskId") String taskId, @RequestParam(value = "saveEntity", required = false) String saveEntity,
                               @ModelAttribute("preloadLeave") BizLeaveVo leave, HttpServletRequest request) {
        boolean saveEntityBoolean = BooleanUtils.toBoolean(saveEntity);
        processService.complete(taskId, leave.getInstanceId(), leave.getTitle(), leave.getReason(), "process", new HashMap<String, Object>(), request);
        if (saveEntityBoolean) {
            bizLeaveService.updateBizLeave(leave);
        }
        return success("任务已完成");
    }

    /**
     * 自动绑定页面字段
     */
    @ModelAttribute("preloadLeave")
    public BizLeaveVo getLeave(@RequestParam(value = "id", required = false) Long id, HttpSession session) {
        if (id != null) {
            return bizLeaveService.selectBizLeaveById(id);
        }
        return new BizLeaveVo();
    }

    @GetMapping("/leaveDone")
    public ModelAndView doneView(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        TokenCookieHandler.setCookieToken(request,response);
        modelAndView.setViewName(prefix + "/leaveDone");
        return modelAndView;
    }

    /**
     * 我的已办列表
     * @param bizLeave
     * @return
     */
    @PostMapping("/taskDoneList")
    @ResponseBody
    public TableDataInfo taskDoneList(BizLeaveVo bizLeave) {
        bizLeave.setType("process");
        List<BizLeaveVo> list = bizLeaveService.findDoneTasks(bizLeave, ShiroUtils.getLoginName());
        return getDataTable(list);
    }

}
