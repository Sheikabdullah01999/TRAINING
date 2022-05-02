package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Service.AdminService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private AdminService adminService;

    @PostMapping({"/createNewRole"})
    public Role createNewRole(@RequestBody Role role) {

        return roleService.createNewRole(role);
    }

    @PostConstruct
    public void initRoleAndUser()
    {
        adminService.initRoleAndUser();
    }
}
