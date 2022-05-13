package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class RoleController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private RoleService roleService;

    @GetMapping("/registration_roles")
    public String showRolesRegistrationForm(Model model)
    {
        Role role = new Role();
        model.addAttribute("roles",role);
        return "roles";
    }

    @PostMapping("/saveRoles")
    public String saveRoles(@ModelAttribute("roles") Role role, Model model)
    {
        try{
            roleService.saveRoles(role);
            model.addAttribute("List_of_Roles",roleService.getAllRoles());
            return "ListOfRoles";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "roles";
        }
    }

    @GetMapping("/List_Of_Roles")
    public String list_of_Roles(Model model)
    {
        try
        {
            model.addAttribute("List_of_Roles",roleService.getAllRoles());
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
