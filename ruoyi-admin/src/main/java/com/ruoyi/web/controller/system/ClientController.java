package com.ruoyi.web.controller.system;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.utils.security.BcryptUtil;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.framework.util.ShiroUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.service.*;
import com.ruoyi.system.serviceJWT.GetUserFromJWT;
import com.ruoyi.system.utils.JWTUtil;
import com.ruoyi.web.controller.tool.MVConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 租户信息
 * 
 * @author Malcolm
 */
@Controller
@RequestMapping("/system/client")
public class ClientController extends BaseController {

    private String prefix = "system/client";

    @Autowired
    private IClientService clientService;

    @GetMapping()
    public ModelAndView user(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        modelAndView.setViewName(prefix+"/client");
        return modelAndView;
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Client client) {
        startPage();
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");
        String clientId = jwtPayload.getString("clients");
        JSONArray rolesArray = JSON.parseArray(jwtPayload.getString("rolesSet"));
        Long roleId = rolesArray.getLong(0);

        List<Client> list = clientService.selectClientList(client);
        return getDataTable(list);
    }

    @RequestMapping("/getClientInfoByClientId")
    @ResponseBody
    public Object getClientInfoByClientId() {
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        String clientId = jwtPayload.getString("clients");
        JSONArray rolesArray = JSON.parseArray(jwtPayload.getString("rolesSet"));
        Long roleId = rolesArray.getLong(0);

        if (roleId == 106 || roleId ==1){
            Client client = new Client();
            client.setClient_id(clientId);
            List<Client> list = clientService.selectClientList(client);
            if (list!=null && list.size()>0){
                return list.get(0);
            }
        }
        return "无权限查询租户信息";
    }

    /**
     * 新增用户页面
     */
    @GetMapping("/add")
    public String add(ModelMap mmap) {
        return prefix + "/add";
    }

    /**
     * 新增租户用户
     */
    @Log(title = "租户管理", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated Client client) {
        return toAjax(clientService.insertClient(client));
    }

    /**
     * 修改租户页面
     */
    @GetMapping("/edit/{client_id}")
    public String edit(@PathVariable("client_id") String client_id, ModelMap mmap) {
        mmap.put("client", clientService.getClientInfo(client_id));
        return prefix + "/edit";
    }

    /**
     * 修改保存租户
     */
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated Client client) {
        return toAjax(clientService.updateClient(client));
    }

    @Log(title = "租户管理", businessType = BusinessType.DELETE)
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        try
        {
            return toAjax(clientService.deleteClientByIds(ids));
        }
        catch (Exception e)
        {
            return error(e.getMessage());
        }
    }

    @Log(title = "租户审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    @ResponseBody
    public AjaxResult approve(@Validated Client client) {
        return toAjax(clientService.updateClientStatus(client));
    }

}