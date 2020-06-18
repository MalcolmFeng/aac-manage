package com.ruoyi.web.controller.system;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.system.domain.Approve;
import com.ruoyi.system.service.IClientApproveService;
import com.ruoyi.system.utils.JWTUtil;
import com.ruoyi.web.controller.tool.MVConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 审批用户申请
 *
 * @author zehwei
 */
@Controller
@RequestMapping("/system/approve")
public class ApproveController extends BaseController {

    private String prefix = "system/approve";

    @Autowired
    private IClientApproveService clientApproveService;

    @GetMapping()
    public ModelAndView approve(HttpServletRequest request, HttpServletResponse response) {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        modelAndView.setViewName(prefix+"/approve");
        return modelAndView;
    }

    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list() {
        startPage();
        JSONObject jwtPayload = JWTUtil.getPayLoadJsonByJWT();
        Long userId = jwtPayload.getLong("userId");

        List<Approve> list = clientApproveService.selectApproveList(userId);
        return getDataTable(list);
    }

    @PostMapping("/exam")
    @ResponseBody
    public AjaxResult exam(Long id, int agree) {
        return toAjax(clientApproveService.updateApprove(id, agree));
    }
}
