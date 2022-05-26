package com.grootan.assetManagement.Controller.RestController;

import com.grootan.assetManagement.Exception.GeneralException;
import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceCategory;
import com.grootan.assetManagement.Model.DeviceName;
import com.grootan.assetManagement.Model.History;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.HistoryDao;
import com.grootan.assetManagement.Service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<Object> addDeviceDetails(@RequestBody Device device)
    {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.addDeviceDetails(device));
        }
        catch(GeneralException e)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/device/list/device")
    public ResponseEntity<List<Device>> getAllDeviceList()
    {
        return ResponseEntity.status(HttpStatus.OK).body(deviceService.getAllDevices());
    }

    @PutMapping("/device/update/device")
    public ResponseEntity<Device> updateDevice(@RequestBody Device device)
    {
        deviceService.updateDeviceDetails(device);
        return ResponseEntity.status(HttpStatus.OK).body(device);
    }

    @DeleteMapping("/device/delete/{id}")
    public ResponseEntity<List<Device>> deleteDevice(@PathVariable(name="id") Integer id)
    {
        deviceService.deleteDeviceDetails(id);
        return ResponseEntity.status(HttpStatus.OK).body(deviceService.getAllDevices());
    }

    @PostMapping("/device/add/category/device")
    public ResponseEntity<Object> addDeviceCategory(@RequestBody DeviceCategory deviceCategory)
    {
        try{
            return ResponseEntity.status(HttpStatus.CREATED).body(deviceService.saveDeviceCategory(deviceCategory));
        }
        catch(GeneralException e)
        {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/device/name/add")
    public ResponseEntity<Object> addDeviceName(@RequestBody DeviceName deviceName)
    {
        try{
            return  ResponseEntity.status(HttpStatus.CREATED).body(deviceService.saveDeviceName(deviceName));
        }
        catch(GeneralException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @GetMapping("/get/history")
    public ResponseEntity<List<History>> history()
    {
        return ResponseEntity.status(HttpStatus.FOUND).body(historyDao.findAll());
    }

    @DeleteMapping("/device/category/delete/{id}")
    public ResponseEntity<List<DeviceCategory>> deleteDeviceCategory(@PathVariable(name="id") String  category)
    {
        deviceService.deleteDeviceCategory(category);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(deviceService.getAllDeviceCategory());
    }

    @GetMapping("/device/get/category/all")
    public ResponseEntity<List<DeviceCategory>> getDeviceCategory()
    {
        List<DeviceCategory> list=deviceService.getCategory();
        if(list.isEmpty())
        {
            throw  new GeneralException( NO_RECORDS);
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(list);
    }

}
