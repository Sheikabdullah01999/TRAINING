package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceCategory;
import com.grootan.assetManagement.Model.DeviceName;
import com.grootan.assetManagement.Model.History;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.HistoryDao;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.request.DeviceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

import static com.grootan.assetManagement.Model.Constants.NO_RECORDS;


@RestController
public class DeviceRestController {
    @Autowired
    DeviceService deviceService;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    DeviceCategoryDao deviceCategoryDao;

    @PostMapping("/device/add/add")
    public ResponseEntity addDeviceDetails(@RequestBody DeviceRequest device)
    {
      return  deviceService.addDeviceDetails(device);
    }

    @GetMapping("/device/list/device")
    public ResponseEntity getAllDeviceList()
    {
        return deviceService.getAllDevices();
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity getDeviceById(@PathParam("deviceId") Integer deviceId)
    {
        return deviceService.findDeviceById(deviceId);
    }

    @PutMapping("/device/update/device")
    public ResponseEntity updateDevice(@RequestBody DeviceRequest device)
    {
        return deviceService.updateDeviceDetails(device);
    }

    @DeleteMapping("/device/delete/{id}")
    public ResponseEntity deleteDevice(@PathVariable(name="id") Integer id)
    {
        return deviceService.deleteDeviceDetails(id);
    }

    @PostMapping("/device/add/category/device")
    public ResponseEntity addDeviceCategory(@RequestBody DeviceCategory deviceCategory)
    {

            return deviceService.saveDeviceCategory(deviceCategory);

    }

    @PostMapping("/device/name/add")
    public ResponseEntity addDeviceName(@RequestBody DeviceName deviceName)
    {
            return  deviceService.saveDeviceName(deviceName);

    }

    @GetMapping("/get/history")
    public ResponseEntity history()
    {
        return deviceService.getHistory();
    }

    @GetMapping("/device/get/category/all")
    public ResponseEntity getDeviceCategory()
    {
        return deviceService.getCategory();
    }

    @DeleteMapping("/device/category/delete/{id}")
    public ResponseEntity deleteDeviceCategory(@PathVariable(name="id") String  category)
    {

        return deviceService.deleteDeviceCategory(category);
    }



}
