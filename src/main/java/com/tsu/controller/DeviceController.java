package com.tsu.controller;

import com.github.pagehelper.PageInfo;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.Device;
import com.tsu.service.DeviceService;
import com.tsu.vo.DeviceStatusVo;
import com.tsu.vo.Result;
import org.apache.shiro.SecurityUtils;

import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/device")
@CrossOrigin
public class DeviceController {


    @Autowired
    DeviceService deviceService;


    @Value("${system.admin-code}")
    private String adminCode;

    @PostMapping("/add")
    public Result add(@RequestBody Device device, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            deviceService.add(device);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                deviceService.checkAdd(device, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @GetMapping("/getByGreenHouseId/{greenHouseId}")
    public Result getByGreenHouseId(@PathVariable Integer greenHouseId) {
        //todo 这里需要
        List<Device> deviceList = deviceService.getByGreenHouseId(greenHouseId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("deviceList", deviceList);

    }


    @PostMapping("/update")
    public Result update(@RequestBody Device device, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            deviceService.update(device);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                deviceService.checkUpdate(device, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }


    @GetMapping("/getAllHasGreenHouse/{page}/{size}")
    public Result getAllByPage(@PathVariable Integer page, @PathVariable Integer size, HttpServletRequest request) {
        PageInfo<Device> pageInfo = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            pageInfo = deviceService.getAllHasGreenHouseByPage(page, size);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            pageInfo = deviceService.getAllHasGreenHouseByPageAndUserId(page, size, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("pageInfo", pageInfo);
    }

    @GetMapping("/delete/{deviceId}")
    public Result delete(@PathVariable Integer deviceId, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            deviceService.delete(deviceId);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                deviceService.checkDelete(deviceId, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }


    @PostMapping("/getOverviewData")
    public Result getUserDevicesInfo(@RequestBody List<String> dateList, HttpServletRequest request) {
        Map<String, Object> data = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            data = deviceService.getOverviewData(dateList);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            data = deviceService.getOverviewDataByUserId(dateList, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .setData(data);
    }

    @GetMapping("/getStatus/{id}")
    public Result getStatus(@PathVariable Integer id) {
        Integer status = deviceService.getStatus(id);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("status", status);
    }

    @GetMapping("/getStatusByGreenHouseId/{greenHouseId}")
    public Result getStatusByGreenHouseId(@PathVariable Integer greenHouseId) {
        DeviceStatusVo deviceStatusVo = deviceService.getStatusByGreenHouseId(greenHouseId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("deviceStatusVo", deviceStatusVo);
    }

}
