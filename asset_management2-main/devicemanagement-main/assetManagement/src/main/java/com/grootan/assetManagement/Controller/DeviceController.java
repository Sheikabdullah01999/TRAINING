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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
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

    @GetMapping("/device/add")
    public String addDeviceDetails(Model model)
    {
        List<DeviceCategory> devices = (List<DeviceCategory>) deviceService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        model.addAttribute("devices",new Device());
        return "AddDeviceDetails";
    }

//    @PostMapping("/device/add/details")
//    public String saveDevice(@ModelAttribute("devices") Device device,Model model)
//    {
//        try{
//            deviceService.addDeviceDetails(device);
//            return "redirect:/device/add?success";
//        }
//        catch(GeneralException e)
//        {
//            model.addAttribute("errorMessage",e.getMessage());
//            return "AddDeviceDetails";
//        }
//    }

    @GetMapping("/device/list")
    public String list_of_devices(Model model)
    {
        try{
            model.addAttribute("DeviceDetails", deviceService.getAllDevices());
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

    @GetMapping("/device/searching")
    public String search(Device device, Model model, String keyword)
    {
        if(keyword!=null)
        {
            List<Device> list = deviceService.getByKeywordDevice(keyword);
            model.addAttribute("DeviceDetails",list);
        }
        else
        {
            List<Device> list = (List<Device>) deviceService.getAllDevices();
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

    @ResponseBody
    @RequestMapping(value = "/device/update/{id}", method = RequestMethod.GET)
   // @GetMapping("/device/update/{id}")
    public ModelAndView showUpdateDevicePage(@PathVariable(name="id") int id,Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateDevice");
        Device device=new Device();
        model.addAttribute("devices",device);
        List<DeviceCategory> devicesCategory = (List<DeviceCategory>) deviceService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devicesCategory);
        List<DeviceName> devicesNameList = (List<DeviceName>) deviceService.getDeviceName();
        model.addAttribute("ListOfDeviceName",devicesNameList);
        ResponseEntity allDevice = deviceService.findDeviceById(id);
        editView.addObject("device", allDevice);
        return editView;
    }

//    @PostMapping("/device/update")
//    public String saveDevices(@ModelAttribute("device") Device device, Model model)
//    {
//        try
//        {
//            deviceService.updateDeviceDetails(device);
//            return "redirect:/device/list";
//        }
//        catch(GeneralException e)
//        {
//            model.addAttribute("errorMessage",e.getMessage());
//            return "UpdateDevice";
//        }
//    }

    @GetMapping("/device/delete/{id}")
    public String deleteDeviceDetails(@PathVariable(name="id") Integer id, Model model)
    {
        deviceService.deleteDeviceDetails(id);
        return "redirect:/device/list";
    }

    @GetMapping("/device/add/category")
    public String addCategory(Model model)
    {
        model.addAttribute("deviceCategory",new DeviceCategory());
        return "AddDeviceCategory";
    }

//    @PostMapping("/device/save/category")
//    public String saveDeviceCategory(@ModelAttribute("deviceCategory") DeviceCategory deviceCategory,Model model)
//    {
//        try{
//            deviceService.saveDeviceCategory(deviceCategory);
//            return "redirect:/device/add/category?success";
//        }
//        catch(GeneralException e)
//        {
//            model.addAttribute("errorMessage",e.getMessage());
//            return "AddDeviceCategory";
//        }

//    }


    @GetMapping("/device/add/name")
    public String addName(Model model)
    {
        List<DeviceCategory> devices = (List<DeviceCategory>) deviceService.getCategory();
        model.addAttribute("ListOfDeviceCategory",devices);
        DeviceName deviceName=new DeviceName();
        model.addAttribute("deviceName",deviceName);
        return "AddDeviceName";
    }

    @PostMapping("/device/save/name")
    public String saveDeviceName(@ModelAttribute("deviceName") DeviceName deviceName,Model model)
    {
        try
        {
            List<DeviceCategory> devices = (List<DeviceCategory>) deviceService.getCategory();
            model.addAttribute("ListOfDeviceCategory",devices);
            deviceService.saveDeviceName(deviceName);
            return "redirect:/device/add/name?success";
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
        model.addAttribute("maintainHistory",histories);
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

//    @PostMapping("/device/update/category")
//    public String updateDevicesCategory(@ModelAttribute("device") DeviceCategory deviceCategory,Model model) {
//        try {
//            deviceService.updateDeviceCategory(deviceCategory);
//            return "redirect:/getAllDeviceCategory";
//        } catch (GeneralException e) {
//            model.addAttribute("errorMessage", e.getMessage());
//            return "UpdateDeviceCategory";
//        }
//    }

    @GetMapping("/device/get/category")
    public String getAllDeviceCategory(Model model)
    {
        model.addAttribute("categoryList", deviceService.getAllDeviceCategory());

        return "ListOfDeviceCategory";
    }

}
