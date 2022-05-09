package com.grootan.assetManagement.Controller;

import com.google.gson.Gson;
import com.grootan.assetManagement.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = { "", "demo" })
public class RestController {
    @Autowired
    private AdminService service;
    @RequestMapping(method = RequestMethod.GET)
    public String index(ModelMap modelMap) {
        modelMap.put("deviceCategories", service.findAll());
        return "AddDeviceDetails";
    }

    @ResponseBody
    @RequestMapping(value = "loadNamesByCategory/{name}", method = RequestMethod.GET)
    public String loadNamesByCategory(@PathVariable("name") String name) {
        Gson gson = new Gson();
        return gson.toJson(service.findByCategory(name));
    }
}
