package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Service.CommonService;
import com.grootan.assetManagement.Service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.PostConstruct;

@Controller
public class UiController {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private CommonService service;

    @PostConstruct
    public void initRoleAndUser()
    {
        service.initRoleAndUser();
    }

    @GetMapping("/")
    public String home(Model model)
    {
        Authentication authentication=service.getCurrentUser();
        model.addAttribute("user",authentication.getName());
        Employee employee= service.loginEmployeeDetails(authentication.getName());
        model.addAttribute("empList",employee);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/frames")
    public String frames(){
        return "frames";
    }
}
