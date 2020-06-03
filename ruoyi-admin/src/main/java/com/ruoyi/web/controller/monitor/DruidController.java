package com.ruoyi.web.controller.monitor;

import com.ruoyi.web.controller.tool.MVConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruoyi.common.core.controller.BaseController;
import org.springframework.web.servlet.ModelAndView;

/**
 * druid 监控
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/data")
public class DruidController extends BaseController
{
    private String prefix = "/druid";

//    @RequiresPermissions("monitor:data:view")
    @GetMapping()
    public ModelAndView index()
    {
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        modelAndView.setViewName(prefix+"/index");
        return modelAndView;
//        return redirect(prefix + "/index");
    }
}
