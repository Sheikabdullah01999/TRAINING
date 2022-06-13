package com.grootan.assetManagement.Controller;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.*;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.DeviceNameDao;
import com.grootan.assetManagement.Repository.EmployeeDao;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.Service.CommonService;
import com.grootan.assetManagement.Service.EmployeeService;
import com.grootan.assetManagement.Service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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
    private DeviceNameDao deviceNameDao;
    @Autowired
    private CommonService service;



    @GetMapping("/device/add")
    public String addDeviceDetails(Model model)
    {
        List<DeviceCategory> devices = (List<DeviceCategory>) deviceCategoryDao.findAll();
        model.addAttribute("ListOfDeviceCategory",devices);
        model.addAttribute("devices",new Device());
        return "AddDeviceDetails";
    }

    @GetMapping("/device/list")
    public String list_of_devices(Model model)
    {
        try{
            model.addAttribute("DeviceDetails", deviceService.getAllDevices());
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

    @GetMapping("/device/searching")
    public String search(Device device, Model model, String keyword) throws ResourceNotFoundException {
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
        long id = deviceService.findByCategoryId(name);
        return gson.toJson(deviceService.findByCategory(id));
    }

    @ResponseBody
    @RequestMapping(value="loadIdsByCategory/{name}", method=RequestMethod.GET)
    public long loadIdsByCategory(@PathVariable("name") String name)
    {
        Gson gson = new Gson();
        return Long.parseLong(gson.toJson(deviceCategoryDao.findByDeviceCategoryId(name)));
    }

//    @ResponseBody
//    @RequestMapping(value = "/device/update/{id}", method = RequestMethod.GET)
    @GetMapping("/device/update/{id}")
    public ModelAndView showUpdateDevicePage(@PathVariable(name="id") int id,Model model)
    {
        ModelAndView editView = new ModelAndView("UpdateDevice");
        Device device=new Device();
        model.addAttribute("devices",device);
        List<DeviceCategory> devicesCategory = (List<DeviceCategory>) deviceCategoryDao.findAll();
        model.addAttribute("ListOfDeviceCategory",devicesCategory);
        List<DeviceName> devicesNameList = (List<DeviceName>) deviceNameDao.findAll();
        model.addAttribute("ListOfDeviceName",devicesNameList);
        Optional<Device> allDevice = deviceDao.findById(id);
        editView.addObject("device", allDevice);
        return editView;
    }


    @GetMapping("/device/delete/{id}")
    public String deleteDeviceDetails(@PathVariable(name="id") Integer id, Model model) throws ResourceNotFoundException {
        deviceService.deleteDeviceDetails(id);
        return "redirect:/device/list";
    }

    @GetMapping("/device/add/category")
    public String addCategory(Model model)
    {
        model.addAttribute("deviceCategory",new DeviceCategory());
        return "AddDeviceCategory";
    }


    @GetMapping("/device/add/name")
    public String addName(Model model) throws ResourceNotFoundException {
        List<DeviceCategory> devices =  deviceCategoryDao.findAll();
        model.addAttribute("ListOfDeviceCategory",devices);
        DeviceName deviceName=new DeviceName();
        model.addAttribute("deviceName",deviceName);
        return "AddDeviceName";
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
    public String deleteDeviceCategoryDetails(@PathVariable(name="id") String  id) throws ResourceNotFoundException {
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


    @GetMapping("/device/get/category")
    public String getAllDeviceCategory(Model model)
    {
        model.addAttribute("categoryList", deviceService.getAllDeviceCategory());

        return "ListOfDeviceCategory";
    }

}