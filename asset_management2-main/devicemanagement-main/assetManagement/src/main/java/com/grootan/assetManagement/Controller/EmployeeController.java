package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.EmployeeDevices;
import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @Autowired
    RoleService roleService;

    @Autowired
    DeviceService deviceService;

    @GetMapping("/employee/registration")
    public String showRegistrationForm(Model model)
    {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("ListOfRoles",roles);
        List<String> deviceList=deviceService.getAllDevicesByName();
        model.addAttribute("ListOfDevices",deviceList);
        List<EmployeeDepartment> employeeDepartmentList=employeeService.getAllEmpDepartments();
        model.addAttribute("ListOfEmpDepartment",employeeDepartmentList);
        Employee employee = new Employee();
        model.addAttribute("employee",employee);
        return "Registration";
    }

    @PostMapping("/employee/save")
    public String saveEmployee(@ModelAttribute("employee") Employee registrationDto, Model model)
    {
        try{
            employeeService.saveEmployee(registrationDto);
            return "redirect:/employee/registration?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "Registration";
        }
    }

    @GetMapping("/employee/list")
    public String listOfEmployee(Model model)
    {
        model.addAttribute("List_Of_Employees",employeeService.getAllEmployees());
        return "ListOfEmployees";
    }

    @GetMapping("/employee/department/list")
    public String listOfDepartment(Model model)
    {
        model.addAttribute("ListOfDepartment",employeeService.getAllEmpDepartments());
        return "ListOfEmployees";
    }

    @GetMapping("/employee/user/device")
    public String userDevices(Model model)
    {
        List<EmployeeDevices> userDevices = employeeService.getUserDevices();
        if(!userDevices.isEmpty())
        {
            model.addAttribute("userDevices", userDevices);
            return "UserDetails";
        }
        else
        {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = dateTime.format(dateTimeFormatter);
            model.addAttribute("timestamp",formattedDate);
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            model.addAttribute("status",httpStatus);
            model.addAttribute("message","NO_RECORDS_FOUND");
            return "Error";
        }
    }

    @GetMapping("/employee/devices/delete/{id}")
    public String deleteEmpDevices(@PathVariable(name="id") int id, Model model)
    {
        employeeService.deleteEmpDevices(id);
        return "redirect:/employee/user/device";
    }

    @GetMapping("/update/employee/{id}")
    public ModelAndView showUpdateEmployeePage(@PathVariable(name="id") String  empId, Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateEmployee");
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("ListOfRoles",roles);
        List<String> deviceList=deviceService.getAllDevicesByName();
        model.addAttribute("ListOfDevices",deviceList);
        List<EmployeeDepartment> employeeDepartmentList=employeeService.getAllEmpDepartments();
        model.addAttribute("ListOfEmpDepartment",employeeDepartmentList);
        Employee employee = employeeService.findEmployeeById(empId);
        editView.addObject("employee",employee);
        return editView;
    }

    @PostMapping("/employee/update")
    public String save(@ModelAttribute("employee") Employee registrationDto,Model model)
    {
        try
        {
            employeeService.updateEmployee(registrationDto);
            return "redirect:/employee/list?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "UpdateEmployee";
        }
    }

    @GetMapping("/delete/employee/{id}")
    public String deleteEmpDetails(@PathVariable(name="id") String id,Model model) {
        try
        {
            employeeService.deleteEmpDetails(id);
            return "redirect:/employee/list";
        }
        catch(GeneralException e)
        {
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = dateTime.format(dateTimeFormatter);
            model.addAttribute("timestamp",formattedDate);
            HttpStatus httpStatus = HttpStatus.FORBIDDEN;
            model.addAttribute("status",httpStatus);
            model.addAttribute("message",e.getMessage());
            return "Error";
        }

    }

    @GetMapping("/employee/search")
    public String home(Employee employee, Model model, String keyword) {
        if(keyword!=null) {
            List<Employee> list = employeeService.getByKeyword(keyword);
            model.addAttribute("List_Of_Employees", list);
        }else {
            List<Employee> list = employeeService.getAllEmployees();
            model.addAttribute("List_Of_Employees", list);}
        return "ListOfEmployees";
    }

    @GetMapping("/employee/department")
    public String addDepartment(Model model)
    {
        EmployeeDepartment employeeDepartment=new EmployeeDepartment();
        model.addAttribute("employeeDepartment",employeeDepartment);
        return "AddEmployeeDepartment";
    }

    @PostMapping("/employee/save/department")
    public String saveEmpDepartment(@ModelAttribute("employeeDepartment") EmployeeDepartment employeeDepartment,Model model)
    {
        try{
            employeeService.saveEmpDepartment(employeeDepartment);
            return "redirect:/employee/department?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddEmployeeDepartment";
        }

    }

}
