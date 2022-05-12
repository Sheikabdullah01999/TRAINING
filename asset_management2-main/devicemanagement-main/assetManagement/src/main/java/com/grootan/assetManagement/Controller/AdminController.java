package com.grootan.assetManagement.Controller;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Service.AdminService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
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
    @Autowired
    private DeviceCategoryDao deviceCategoryDao;

    @PostConstruct
    public void initRoleAndUser()
    {
        adminService.initRoleAndUser();
    }

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication=adminService.getCurrentUser();
        model.addAttribute("user",authentication.getName());
        Employee employee=adminService.loginEmployeeDetails(authentication.getName());
        model.addAttribute("empList",employee);
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "Login";
    }

    @GetMapping("/registration_form")
    public String showRegistrationForm(Model model)
    {
        List<Role> roles = adminService.getAllRoles();
        model.addAttribute("List_Of_Roles",roles);
        List<String> deviceList=adminService.getAllDevicesByName();
        model.addAttribute("List_Of_Devices",deviceList);
        List<EmployeeDepartment> employeeDepartmentList=adminService.getAllEmpDepartments();
        model.addAttribute("ListOfEmpDepartment",employeeDepartmentList);
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
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "Registration";
        }
    }

    @GetMapping("/List_Of_Employees")
    public String listOfEmployee(Model model)
    {
        model.addAttribute("List_Of_Employees",adminService.getAllEmployees());
        return "ListOfEmployees";
    }

    @GetMapping("/List_Of_Devices")
    public String list_of_devices(Model model)
    {
        try{
            model.addAttribute("Device_details",adminService.getAllDevices());
            return "DeviceDetails";
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

    @GetMapping("/ListOfEmpDepartment")
    public String listOfDepartment(Model model)
    {
        model.addAttribute("ListOfDepartment",adminService.getAllEmpDepartments());
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
        try{
            adminService.saveRoles(role);
            model.addAttribute("List_of_Roles",adminService.getAllRoles());
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
            model.addAttribute("List_of_Roles",adminService.getAllRoles());
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

    @GetMapping("/add_device_details")
    public String addDeviceDetails(Model model)
    {
        List<DeviceCategory> devices = adminService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        model.addAttribute("devices",new Device());
        return "AddDeviceDetails";
    }

    @GetMapping("/user_devices")
    public String userDevices(Model model)
    {
        List<EmployeeDevices> userDevices = adminService.getUserDevices();
        if(!userDevices.isEmpty())
        {
            model.addAttribute("user_devices", userDevices);
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

    @GetMapping("/history")
    public String history(Model model)
    {
        try
        {
            return findPaginated(1,model);
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


    @PostMapping("/device_details")
    public String saveDevice(@ModelAttribute("devices") Device device,Model model)
    {
        try{
            adminService.addDeviceDetails(device);
            return "redirect:/add_device_details?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceDetails";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDeviceDetails(@PathVariable(name="id") Integer id, Model model)
    {
        adminService.deleteDeviceDetails(id);
        return "redirect:/List_Of_Devices";
    }

    @GetMapping("/employee_devices/delete/{id}")
    public String deleteEmpDevices(@PathVariable(name="id") int id, Model model)
    {
        adminService.deleteEmpDevices(id);
        return "redirect:/user_devices";
    }

    @GetMapping("category/delete/{id}")
    public String deleteDeviceCategoryDetails(@PathVariable(name="id") String  id)
    {
        adminService.deleteDeviceCategory(id);
        return "redirect:/getAllDeviceCategory";
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
        String deviceByEmpId=adminService.getAllDevicesById(empId);
        List<String> ListOfEmpDevices = List.of(deviceByEmpId.split(";"));
        model.addAttribute("List_Of_DevicesById",ListOfEmpDevices);
        List<EmployeeDepartment> employeeDepartmentList=adminService.getAllEmpDepartments();
        model.addAttribute("ListOfEmpDepartment",employeeDepartmentList);
        Employee employee = adminService.findEmployeeById(empId);
        editView.addObject("employee",employee);
        return editView;
    }

    @PostMapping("/updateEmployee")
    public String save(@ModelAttribute("employee") Employee registrationDto,Model model)
    {
        try
        {
            adminService.updateEmployee(registrationDto);
            return "redirect:/List_Of_Employees?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "UpdateEmployee";
        }
    }

    @PostMapping("/update_device_details")
    public String saveDevices(@ModelAttribute("device") Device device, Model model)
    {
        try
        {
            adminService.updateDeviceDetails(device);
            return "redirect:/List_Of_Devices";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "UpdateDevice";
        }
    }

    @GetMapping("/CategoryUpdate/{category}")
    public ModelAndView showUpdateDeviceCategoryPage(@PathVariable(name="category") DeviceCategory category,Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateDeviceCategory");
        DeviceCategory deviceCategory=deviceCategoryDao.findByDeviceCategory(category.getCategory());
        editView.addObject("device",deviceCategory);
        return editView;
    }

    @PostMapping("/update_device_category")
    public String updateDevicesCategory(@ModelAttribute("device") DeviceCategory deviceCategory,Model model)
    {
        try
        {
            adminService.updateDeviceCategory(deviceCategory);
            return "redirect:/getAllDeviceCategory";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "UpdateDeviceCategory";
        }
    }

    @GetMapping("/delete/employee/{id}")
    public String deleteEmpDetails(@PathVariable(name="id") String id,Model model) {
        try
        {
            adminService.deleteEmpDetails(id);
            return "redirect:/List_Of_Employees";
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

    @GetMapping("/searching")
    public String search(Device device, Model model, String keyword)
    {
        if(keyword!=null)
        {
            List<Device> list = adminService.getByKeywordDevice(keyword);
            model.addAttribute("Device_details",list);
        }
        else
        {
            List<Device> list = adminService.getAllDevices();
            model.addAttribute("Device_details",list);
        }
        return "DeviceDetails";
    }

    @PostMapping("/saveDeviceCategory")
    public String saveDeviceCategory(@ModelAttribute("deviceCategory") DeviceCategory deviceCategory,Model model)
    {
        try{
            adminService.saveDeviceCategory(deviceCategory);
            return "redirect:/category?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceCategory";
        }

    }
    @GetMapping("/category")
    public String addCategory(Model model)
    {
        model.addAttribute("deviceCategory",new DeviceCategory());
        return "AddDeviceCategory";
    }

    @PostMapping("/saveEmpDepartment")
    public String saveEmpDepartment(@ModelAttribute("employeeDepartment") EmployeeDepartment employeeDepartment,Model model)
    {
        try{
            adminService.saveEmpDepartment(employeeDepartment);
            return "redirect:/department?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddEmployeeDepartment";
        }

    }

    @GetMapping("/department")
    public String addDepartment(Model model)
    {
        EmployeeDepartment employeeDepartment=new EmployeeDepartment();
        model.addAttribute("employeeDepartment",employeeDepartment);
        return "AddEmployeeDepartment";
    }

    @PostMapping("/saveDeviceName")
    public String saveDeviceName(@ModelAttribute("deviceName") DeviceName deviceName,Model model)
    {
        try
        {
            List<DeviceCategory> devices = adminService.getCategory();
            model.addAttribute("ListOfDeviceCategory",devices);
            adminService.saveDeviceName(deviceName);
            return "redirect:/DeviceName?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceName";
        }
    }
    @GetMapping("/DeviceName")
    public String addName(Model model)
    {
        List<DeviceCategory> devices = adminService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        DeviceName deviceName=new DeviceName();
        model.addAttribute("deviceName",deviceName);
        return "AddDeviceName";
    }
    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value="pageNo") int pageNo, Model model)
    {
        int pageSize=5;
        Page<History> page = adminService.findPaginated(pageNo,pageSize);
        List<History> histories = page.getContent();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("maintain_history",histories);
        return "HistoryDetails";
    }

    @GetMapping("/getAllDeviceCategory")
    public String getAllDeviceCategory(Model model)
    {
        model.addAttribute("categoryList",adminService.getAllDeviceCategory());

        return "ListOfDeviceCategory";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("deviceCategories", adminService.findAll());
        return "AddDeviceDetails";
    }

    @ResponseBody
    @RequestMapping(value = "loadNamesByCategory/{name}", method = RequestMethod.GET)
    public String loadNamesByCategory(@PathVariable("name") String name) {
        Gson gson = new Gson();
        return gson.toJson(adminService.findByCategory(name));
    }
}
