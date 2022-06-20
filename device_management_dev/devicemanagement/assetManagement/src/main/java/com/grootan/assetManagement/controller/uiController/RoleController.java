package com.grootan.assetManagement.controller.uiController;

import com.grootan.assetManagement.exception.GeneralException;
import com.grootan.assetManagement.model.Role;
import com.grootan.assetManagement.service.employeeService.EmployeeService;
import com.grootan.assetManagement.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class RoleController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/role/registration")
    public String showRolesRegistrationForm(Model model)
    {
        Role role = new Role();
        model.addAttribute("roles",role);
        return "roles";
    }

//    @PostMapping("/role/save")
//    public String saveRoles(@ModelAttribute("roles") Role role, Model model)
//    {
//        try{
//            roleService.saveRoles(role);
//            model.addAttribute("ListOfRoles",roleService.getAllRoles());
//            return "ListOfRoles";
//        }
//        catch(GeneralException e)
//        {
//            model.addAttribute("errorMessage",e.getMessage());
//            return "roles";
//        }
//    }

    @GetMapping("/roles/list")
    public String list_of_Roles(Model model)
    {
        try
        {
            model.addAttribute("ListOfRoles",roleService.getAllRoles());
            return "ListOfRoles";
        }
        catch(GeneralException e)
        {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = dateTime.format(dateTimeFormatter);
            model.addAttribute("timestamp",formattedDate);
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            model.addAttribute("status",httpStatus);
            model.addAttribute("message",e.getMessage());
            return "Error";
        }
    }

}