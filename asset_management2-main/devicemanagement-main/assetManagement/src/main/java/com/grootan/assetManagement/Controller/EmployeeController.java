package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.Employee;
import com.grootan.assetManagement.Model.EmployeeDepartment;
import com.grootan.assetManagement.Model.EmployeeDevices;
import com.grootan.assetManagement.Model.Role;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Repository.EmployeeDepartmentDao;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    EmployeeDepartmentDao employeeDepartmentDao;

    @Autowired
    EmployeeDao employeeDao;

    @GetMapping("/employee/registration")
    public String showRegistrationForm(Model model)
    {
        List<Role> roles = roleService.getAllRoles();
        model.addAttribute("ListOfRoles",roles);
        List<String> deviceList=deviceService.getAllDevicesByName();
        model.addAttribute("ListOfDevices",deviceList);
        List<EmployeeDepartment> employeeDepartmentList= employeeDepartmentDao.findAll();
        model.addAttribute("ListOfEmpDepartment",employeeDepartmentList);
        Employee employee = new Employee();
        model.addAttribute("employee",employee);
        return "Registration";
    }

    @GetMapping("/employee/list")
    public String listOfEmployee()
    {
        return "ListOfEmployees";
    }

    @GetMapping("/employee/user/device")
    public String userDevices()
    {
        return "UserDetails";
    }

    @GetMapping("/employee/devices/delete/{id}")
    public String deleteEmpDevices(@PathVariable(name="id") int id, Model model) throws ResourceNotFoundException {
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
        List<EmployeeDepartment> employeeDepartmentList= employeeDepartmentDao.findAll();
        model.addAttribute("ListOfEmpDepartment",employeeDepartmentList);
        Employee employee = employeeDao.findByEmpId(empId);
        editView.addObject("employee",employee);
        return editView;
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
        } catch (ResourceNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/employee/search")
    public String home(Employee employee, Model model, String keyword) throws ResourceNotFoundException
    {
        if(keyword!=null)
        {
            List<Employee> list = employeeService.getByKeyword(keyword);
            model.addAttribute("List_Of_Employees", list);
        }else
        {
            List<Employee> list = (List<Employee>) employeeService.getAllEmployees();
            model.addAttribute("List_Of_Employees", list);
        }
        return "ListOfEmployees";
    }

    @GetMapping("/employee/department")
    public String addDepartment(Model model)
    {
        EmployeeDepartment employeeDepartment = new EmployeeDepartment();
        model.addAttribute("employeeDepartment",employeeDepartment);
        return "AddEmployeeDepartment";
    }



}
