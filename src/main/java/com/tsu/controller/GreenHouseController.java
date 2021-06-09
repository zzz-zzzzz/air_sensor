package com.tsu.controller;

import com.github.pagehelper.PageInfo;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.GreenHouse;
import com.tsu.service.GreenHouseService;
import com.tsu.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/greenHouse")
@CrossOrigin
public class GreenHouseController {


    @Autowired
    GreenHouseService greenHouseService;

    @Value("${system.admin-code}")
    private String adminCode;

    @PostMapping("/add")
    public Result add(@RequestBody GreenHouse greenHouse, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            greenHouseService.add(greenHouse);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                greenHouseService.checkAdd(greenHouse, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @GetMapping("/delete/{greenHouseId}")
    public Result delete(@PathVariable Integer greenHouseId, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            greenHouseService.delete(greenHouseId);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                greenHouseService.checkDelete(greenHouseId, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }


    @GetMapping("/getAllHasLocation/{page}/{size}")
    public Result getAllHashLocationByPage(@PathVariable Integer page, @PathVariable Integer size, HttpServletRequest request) {
        PageInfo<GreenHouse> pageInfo = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            pageInfo = greenHouseService.getAllHasLocation(page, size);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            pageInfo = greenHouseService.getAllHasLocationByUserId(page, size, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("pageInfo", pageInfo);

    }

    @GetMapping("/getByLocationId/{locationId}")
    public Result getByLocationId(@PathVariable Integer locationId) {
        List<GreenHouse> greenHouseList = greenHouseService.getByLocationId(locationId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("greenHouseList", greenHouseList);
    }


    @GetMapping("/getAll")
    public Result getAll(HttpServletRequest request) {
        List<GreenHouse> greenHouseList = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            greenHouseList = greenHouseService.getAll();
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            greenHouseList = greenHouseService.getAllByUserId(userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("greenHouseList", greenHouseList);
    }

    @PostMapping("/update")
    public Result update(@RequestBody GreenHouse greenHouse, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            greenHouseService.update(greenHouse);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                greenHouseService.checkUpdate(greenHouse, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

}
