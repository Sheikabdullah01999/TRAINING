package com.grootan.assetManagement.Controller;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.Service.CommonService;
import com.grootan.assetManagement.Service.EmployeeService;
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
public class DeviceController {
    @Autowired
    DeviceDao deviceDao;
    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private DeviceCategoryDao deviceCategoryDao;
    @Autowired
    private CommonService service;

    @GetMapping("/add_device_details")
    public String addDeviceDetails(Model model)
    {
        List<DeviceCategory> devices = deviceService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        model.addAttribute("devices",new Device());
        return "AddDeviceDetails";
    }

    @PostMapping("/device_details")
    public String saveDevice(@ModelAttribute("devices") Device device,Model model)
    {
        try{
            deviceService.addDeviceDetails(device);
            return "redirect:/add_device_details?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceDetails";
        }
    }

    @GetMapping("/List_Of_Devices")
    public String list_of_devices(Model model)
    {
        try{
            model.addAttribute("Device_details", deviceService.getAllDevices());
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

    @GetMapping("/searching")
    public String search(Device device, Model model, String keyword)
    {
        if(keyword!=null)
        {
            List<Device> list = deviceService.getByKeywordDevice(keyword);
            model.addAttribute("Device_details",list);
        }
        else
        {
            List<Device> list = deviceService.getAllDevices();
            model.addAttribute("Device_details",list);
        }
        return "DeviceDetails";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("deviceCategories", deviceService.findAll());
        return "AddDeviceDetails";
    }

    @ResponseBody
    @RequestMapping(value = "loadNamesByCategory/{name}", method = RequestMethod.GET)
    public String loadNamesByCategory(@PathVariable("name") String name) {
        Gson gson = new Gson();
        return gson.toJson(deviceService.findByCategory(name));
    }

    @GetMapping("/update/{id}")
    public ModelAndView showUpdateDevicePage(@PathVariable(name="id") int id,Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateDevice");
        Device device=new Device();
        model.addAttribute("devices",device);
        List<DeviceCategory> devices = deviceService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        List<DeviceName> devicesList = deviceService.getName();
        model.addAttribute("ListOfDeviceName",devicesList);
        Optional<Device> allDevice = deviceService.findDeviceById(id);
        editView.addObject("device",allDevice);
        return editView;
    }

    @PostMapping("/update_device_details")
    public String saveDevices(@ModelAttribute("device") Device device, Model model)
    {
        try
        {
            deviceService.updateDeviceDetails(device);
            return "redirect:/List_Of_Devices";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "UpdateDevice";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteDeviceDetails(@PathVariable(name="id") Integer id, Model model)
    {
        deviceService.deleteDeviceDetails(id);
        return "redirect:/List_Of_Devices";
    }

    @GetMapping("/category")
    public String addCategory(Model model)
    {
        model.addAttribute("deviceCategory",new DeviceCategory());
        return "AddDeviceCategory";
    }

    @PostMapping("/saveDeviceCategory")
    public String saveDeviceCategory(@ModelAttribute("deviceCategory") DeviceCategory deviceCategory,Model model)
    {
        try{
            deviceService.saveDeviceCategory(deviceCategory);
            return "redirect:/category?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceCategory";
        }

    }

    @GetMapping("/DeviceName")
    public String addName(Model model)
    {
        List<DeviceCategory> devices = deviceService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        DeviceName deviceName=new DeviceName();
        model.addAttribute("deviceName",deviceName);
        return "AddDeviceName";
    }

    @PostMapping("/saveDeviceName")
    public String saveDeviceName(@ModelAttribute("deviceName") DeviceName deviceName,Model model)
    {
        try
        {
            List<DeviceCategory> devices = deviceService.getCategory();
            model.addAttribute("ListOfDeviceCategory",devices);
            deviceService.saveDeviceName(deviceName);
            return "redirect:/DeviceName?success";
        }
        catch(GeneralException e)
        {
            model.addAttribute("errorMessage",e.getMessage());
            return "AddDeviceName";
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

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value="pageNo") int pageNo, Model model)
    {
        int pageSize=5;
        Page<History> page = deviceService.findPaginated(pageNo,pageSize);
        List<History> histories = page.getContent();
        model.addAttribute("currentPage",pageNo);
        model.addAttribute("totalPages",page.getTotalPages());
        model.addAttribute("totalItems",page.getTotalElements());
        model.addAttribute("maintain_history",histories);
        return "HistoryDetails";
    }

    @GetMapping("category/delete/{id}")
    public String deleteDeviceCategoryDetails(@PathVariable(name="id") String  id)
    {
        deviceService.deleteDeviceCategory(id);
        return "redirect:/getAllDeviceCategory";
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
    public String updateDevicesCategory(@ModelAttribute("device") DeviceCategory deviceCategory,Model model) {
        try {
            deviceService.updateDeviceCategory(deviceCategory);
            return "redirect:/getAllDeviceCategory";
        } catch (GeneralException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "UpdateDeviceCategory";
        }
    }

    @GetMapping("/getAllDeviceCategory")
    public String getAllDeviceCategory(Model model)
    {
        model.addAttribute("categoryList", deviceService.getAllDeviceCategory());

        return "ListOfDeviceCategory";
    }

}
