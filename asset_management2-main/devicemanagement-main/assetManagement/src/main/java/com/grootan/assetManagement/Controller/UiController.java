package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Service.CommonService;
import com.grootan.assetManagement.Service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import javax.annotation.PostConstruct;
@Configuration
@Controller
public class UiController
{
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private CommonService service;

    @Autowired
    private EmployeeDao employeeDao;

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

    @GetMapping("/list")
    public String list(){
        return "Lists";
    }
}
