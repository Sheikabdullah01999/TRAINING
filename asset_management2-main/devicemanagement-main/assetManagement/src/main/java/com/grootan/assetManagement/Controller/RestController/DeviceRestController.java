package com.grootan.assetManagement.Controller.RestController;

import com.google.gson.Gson;
import com.grootan.assetManagement.Exception.AlreadyExistsException;
import com.grootan.assetManagement.Exception.FieldEmptyException;
import com.grootan.assetManagement.Exception.ResourceNotFoundException;
import com.grootan.assetManagement.Model.Device;
import com.grootan.assetManagement.Model.DeviceCategory;
import com.grootan.assetManagement.Model.DeviceName;
import com.grootan.assetManagement.Repository.DeviceCategoryDao;
import com.grootan.assetManagement.Repository.DeviceDao;
import com.grootan.assetManagement.Repository.HistoryDao;
import com.grootan.assetManagement.Response;
import com.grootan.assetManagement.Service.DeviceService;
import com.grootan.assetManagement.request.DeviceCategoryRequest;
import com.grootan.assetManagement.request.DeviceNameRequest;
import com.grootan.assetManagement.request.DeviceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.websocket.server.PathParam;
import java.util.List;

@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "OK",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "201", description = "CREATED",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "404", description = "NO_RECORDS",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "406", description = "NOT_ACCEPTABLE",content = @Content(schema = @Schema(implementation = Response.class))),
        @ApiResponse(responseCode = "409", description = "CONFLICT",content = @Content(schema = @Schema(implementation = Response.class)))
})
@RestController
public class DeviceRestController {
    @Autowired
    DeviceService deviceService;

    @Autowired
    HistoryDao historyDao;

    @Autowired
    DeviceCategoryDao deviceCategoryDao;

    @Autowired
    DeviceDao deviceDao;

    @GetMapping("/v1/history")
    @Operation(summary = "GetHistories", description = "Returns a List Of Histories")
    public ResponseEntity<Object> history() throws ResourceNotFoundException {
        return deviceService.getHistory();
    }

    @GetMapping("/v1/devices")
    @Operation(summary = "GetDevices", description = "Returns a List Of Devices")
    public ResponseEntity<Object> getAllDeviceList() throws ResourceNotFoundException {
        List<Device> devices = deviceDao.findAll();
        if (devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }


    @GetMapping("/v1/devices/{deviceId}")
    @Operation(summary = "GetDevice", description = "Returns a Device By Id")
    public ResponseEntity<Object> getDeviceById(@PathParam("deviceId") Integer deviceId) {
        return deviceService.findDeviceById(deviceId);
    }

    @GetMapping("/v1/devices/categories")
    @Operation(summary = "GetDeviceCategories", description = "Returns a List Of DeviceCategories")
    public ResponseEntity<Object> getDeviceCategory() throws ResourceNotFoundException {
        return deviceService.getCategory();
    }

    @PostMapping("/v1/device")
    @Operation(summary = "CreateDevice", description = "Create a Device")
    public ResponseEntity<Object> addDeviceDetails(@RequestBody DeviceRequest device) throws FieldEmptyException, ResourceNotFoundException {
        return deviceService.addDeviceDetails(device);
    }

    @PostMapping("/v1/devices/category")
    @Operation(summary = "CreateDeviceCategory", description = "Create a DeviceCategory")
    public ResponseEntity<Object> addDeviceCategory(@RequestBody DeviceCategoryRequest deviceCategory) throws FieldEmptyException, AlreadyExistsException {

        return deviceService.saveDeviceCategory(deviceCategory);
    }

    @PostMapping("/v1/devices/deviceName")
    @Operation(summary = "CreateDeviceName", description = "Create a DeviceName")
    public ResponseEntity addDeviceName(@RequestBody DeviceNameRequest deviceName) throws FieldEmptyException, AlreadyExistsException {
        return deviceService.saveDeviceName(deviceName);

    }

    @PutMapping("/v1/device/{id}")
    @Operation(summary = "EditDevice", description = "update the device")
    public ResponseEntity updateDevice(@PathVariable(name = "id") Integer id, @RequestBody DeviceRequest device) throws FieldEmptyException {
        return deviceService.updateDeviceDetails(id,device);
    }

    @DeleteMapping("/v1/devices/{id}")
    @Operation(summary = "DeleteDevice", description = "delete the device")
    public ResponseEntity deleteDevice(@PathVariable(name = "id") Integer id) throws ResourceNotFoundException {
        return deviceService.deleteDeviceDetails(id);
    }

//    @DeleteMapping("/v1/devices/category/{id}")
//    @Operation(summary = "DeleteDeviceCategory", description = "delete the device category")
//    public ResponseEntity deleteDeviceCategory(@PathVariable(name = "id") String category) throws ResourceNotFoundException {
//
//        return deviceService.deleteDeviceCategory(category);
//    }

    @GetMapping("/v1/devices/search")
    @Operation(summary = "DeviceSearch", description = "search device by keyword")
    public ResponseEntity search(@RequestParam(name = "keyword") String keyword) throws ResourceNotFoundException {
        List<Device> devices = deviceDao.findByKeyword(keyword);
        if (devices.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(devices);
    }

    @ResponseBody
    @RequestMapping(value = "/v1/devices/deviceCategory/{deviceName}", method = RequestMethod.GET)
    @Operation(summary = "GetDeviceCategory", description = "get deviceCategory by deviceName")
    public String loadNamesByCategory(@PathVariable("deviceName") String name) {
        Gson gson = new Gson();
        long id = deviceService.findByCategoryId(name);
        return gson.toJson(deviceService.findByCategory(id));
    }

    @ResponseBody
    @RequestMapping(value="/v1/devices/deviceCategoryIds/{deviceName}", method=RequestMethod.GET)
    @Operation(summary = "GetDeviceCategoryId", description = "get deviceCategory by deviceName")
    public long loadIdsByCategory(@PathVariable("deviceName") String name)
    {
        Gson gson = new Gson();
        return Long.parseLong(gson.toJson(deviceCategoryDao.findByDeviceCategoryId(name)));
    }

    @GetMapping("/v1/devices/deviceNames")
    @Operation(summary = "GetDeviceNames", description = "Returns a List Of DeviceNames")
    public ResponseEntity<Object> getDeviceName() throws ResourceNotFoundException {
        return deviceService.getDeviceNames();
    }
}