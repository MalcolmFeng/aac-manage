package com.ruoyi.web.controller.monitor;

import com.ruoyi.web.controller.tool.MVConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.framework.web.domain.Server;
import org.springframework.web.servlet.ModelAndView;

/**
 * 服务器监控
 * 
 * @author ruoyi
 */
@Controller
@RequestMapping("/monitor/server")
public class ServerController extends BaseController
{
    private String prefix = "monitor/server";

//    @RequiresPermissions("monitor:server:view")
    @GetMapping()
    public ModelAndView server(ModelMap mmap) throws Exception
    {
        Server server = new Server();
        server.copyTo();
        mmap.put("server", server);
        ModelAndView modelAndView = MVConstructor.MVConstruct();
        modelAndView.setViewName(prefix+"/server");
        return modelAndView;
//        return prefix + "/server";
    }
}
