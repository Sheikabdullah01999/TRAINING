package com.grootan.assetManagement.Controller;

import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Exception.UserAlreadyExistException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Service.AdminService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class AdminController {
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    private AdminService adminService;
    @Autowired
    private RoleService roleService;


    @GetMapping("/")
    public String home(Model model) {
        List<String> deviceList=adminService.getAllDevices();
        model.addAttribute("List_Of_Devices",deviceList);
        Authentication authentication=adminService.getCurrentUser();
        String currentUser=authentication.getName();
        model.addAttribute("user",currentUser);
        return "index";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registration_form")
    public String showRegistrationForm(Model model)
    {
        List<Role> roles = adminService.getAllRoles();
        model.addAttribute("List_Of_Roles",roles);
        List<String> deviceList=adminService.getAllDevicesByName();
        model.addAttribute("List_Of_Devices",deviceList);
        Employee employee = new Employee();
        model.addAttribute("employee",employee);
        return "Registration";
    }

    @PostMapping("/saveEmployee")
    public String saveEmployee(@ModelAttribute("employee") Employee registrationDto,Model model)
    {
        try{
            adminService.saveEmployee(registrationDto);
            return "redirect:/registration_form?success";
        }
        catch(UserAlreadyExistException e)
        {

            model.addAttribute("errorMessage",e.getMessage());
            return "Registration";
        }
    }
    @GetMapping("/List_Of_Employees")
    public String list_of_employee(Model model)
    {
        model.addAttribute("List_Of_Employees",adminService.getAllEmployees());
        return "ListOfEmployees";
    }

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
        adminService.saveRoles(role);
        model.addAttribute("List_of_Roles",adminService.getAllRoles());
        return "ListOfRoles";
    }

    @GetMapping("/List_Of_Roles")
    public String list_of_Roles(Model model)
    {
        model.addAttribute("List_of_Roles",adminService.getAllRoles());
        return "ListOfRoles";
    }


    @GetMapping("/damaged")
    public String getDamagedDeviceDetails(@RequestParam String device,@RequestParam String status, Model model)
    {
        if(status.isEmpty())
        {
            List<Device> deviceList =adminService.getDevice(device);
            try
            {
                model.addAttribute("Device_details",deviceList);
                return "DeviceDetails";
            }
            catch(ResourceNotFoundException e)
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
        else
        {
            List<Device> deviceList =adminService.getDamagedDevice(device,status);
            try
            {
                model.addAttribute("Device_details",deviceList);
                return "DeviceDetails";
            }
            catch(ResourceNotFoundException e)
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


    @GetMapping("/add_device_details")
    public String addDeviceDetails(Model model)
    {
        List<DeviceCategory> devices = adminService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        List<DeviceName> devicesList = adminService.getName();
        model.addAttribute("ListOfDeviceName",devicesList);
        model.addAttribute("device",new Device());
        return "AddDeviceDetails";
    }

    @GetMapping("/user_devices")
    public String userDevices(Model model)
    {
        List<Response> userDevices = adminService.getUserDevices();
        model.addAttribute("user_devices",userDevices);
        return "UserDetails";
    }
    @GetMapping("/history")
    public String history(Model model)
    {
        List<History> history = adminService.getHistory();
        model.addAttribute("maintain_history",history);
        return "HistoryDetails";
    }

    @PostMapping("/device_details")
    public String saveDevice(@ModelAttribute("Device") Device device,Model model)
    {
        try{
            adminService.addDeviceDetails(device);
            return "redirect:/add_device_details?success";
        }
        catch(UserAlreadyExistException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceDetails";
        }

    }

    @GetMapping("/delete/{id}")
    public String deleteDetails(@PathVariable(name="id") Integer id)
    {
        Device device=deviceDao.findById(id).orElseThrow(()-> new ResourceNotFoundException("no record found"));
        String category="redirect:/device?device="+device.getCategory();
        adminService.deleteDeviceDetails(id);
        return category;
    }


    @GetMapping("/update/{id}")
    public ModelAndView showUpdateDevicePage(@PathVariable(name="id") int id,Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateDevice");
        Device device=new Device();
        model.addAttribute("devices",device);
        List<DeviceCategory> devices = adminService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        List<DeviceName> devicesList = adminService.getName();
        model.addAttribute("ListOfDeviceName",devicesList);
        Optional<Device> allDevice = adminService.findDeviceById(id);
        editView.addObject("device",allDevice);
        return editView;
    }

    @GetMapping("/update/employee/{id}")
    public ModelAndView showUpdateEmployeePage(@PathVariable(name="id") String  empId, Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateEmployee");
        List<Role> roles = adminService.getAllRoles();
        model.addAttribute("List_Of_Roles",roles);
        List<String> deviceList=adminService.getAllDevicesByName();
        model.addAttribute("List_Of_Devices",deviceList);
        Employee employee = adminService.findEmployeeById(empId);
        editView.addObject("employee",employee);
        return editView;
    }

    @PostMapping("/updateEmployee")
    public String save(@ModelAttribute("employee") Employee registrationDto,Model model)
    {
        try{
            adminService.updateEmployee(registrationDto);
            return "redirect:/List_Of_Employees?success";
        }
        catch(UserAlreadyExistException e)
        {

            model.addAttribute("errorMessage",e.getMessage());
            return "Registration";
        }
    }

    @GetMapping("/delete/employee/{id}")
    public String deleteEmpDetails(@PathVariable(name="id") String id)
    {
        adminService.deleteEmpDetails(id);
        return "redirect:/List_Of_Employees";
    }

    @GetMapping("/List_Of_EmployeesById")
    public String list_of_employeeById(Model model)
    {
        model.addAttribute("List_Of_EmployeesById",adminService.getEmployeeById());
        return "ListOfEmployeesById";
    }

    @GetMapping("/search")
    public String home(Employee employee, Model model, String keyword) {
        if(keyword!=null) {
            List<Employee> list = adminService.getByKeyword(keyword);
            model.addAttribute("List_Of_Employees", list);
        }else {
            List<Employee> list = adminService.getAllEmployees();
            model.addAttribute("List_Of_Employees", list);}
        return "ListOfEmployees";
    }

    @PostMapping("/saveDeviceCategory")
    public String saveDeviceCategory(@ModelAttribute("deviceCategory") DeviceCategory deviceCategory,Model model)
    {
        try{
            adminService.saveDeviceCategory(deviceCategory);
            return "redirect:/category?success";
        }
        catch(UserAlreadyExistException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceCategory";
        }

    }

    @GetMapping("/category")
    public String addCategory(Model model)
    {
        DeviceCategory deviceCategory=new DeviceCategory();
        model.addAttribute("deviceCategory",deviceCategory);
        return "AddDeviceCategory";
    }

    @PostMapping("/saveDeviceName")
    public String saveDeviceName(@ModelAttribute("deviceName") DeviceName deviceName,Model model)
    {
        try
        {
            adminService.saveDeviceName(deviceName);
            return "redirect:/DeviceName?success";
        }
        catch(UserAlreadyExistException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceName";
        }
    }

    @GetMapping("/DeviceName")
    public String addName(Model model)
    {
        DeviceName deviceName=new DeviceName();
        model.addAttribute("deviceName",deviceName);
        return "AddDeviceName";
    }

}
