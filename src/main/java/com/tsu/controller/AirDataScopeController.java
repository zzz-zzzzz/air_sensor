package com.tsu.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.AirDataScope;
import com.tsu.service.AirDataScopeService;
import com.tsu.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/airDataScope")
@CrossOrigin
public class AirDataScopeController {

    @Value("${system.admin-code}")
    private String adminCode;


    @Autowired
    AirDataScopeService airDataScopeService;

    @GetMapping("/getByDeviceId/{deviceId}")
    public Result getByDeviceId(@PathVariable Integer deviceId) {
        AirDataScope airDataScope = airDataScopeService.getByDeviceId(deviceId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("airDataScope", airDataScope);
    }

    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody AirDataScope airDataScope, HttpServletRequest request) throws JsonProcessingException {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            airDataScopeService.saveOrUpdate(airDataScope);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            airDataScopeService.saveOrUpdateAndCheck(airDataScope, userId);
        }

        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }





}
