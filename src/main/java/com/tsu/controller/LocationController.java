package com.tsu.controller;

import com.github.pagehelper.PageInfo;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.GreenHouse;
import com.tsu.entity.Location;
import com.tsu.service.LocationService;
import com.tsu.vo.DistributeLocationVO;
import com.tsu.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/location")
@CrossOrigin
public class LocationController {

    @Autowired
    LocationService locationService;

    @Value("${system.admin-code}")
    private String adminCode;


    @GetMapping("/getAll/{page}/{size}")
    public Result getAllByPage(@PathVariable Integer page, @PathVariable Integer size, HttpServletRequest request) {
        PageInfo<Location> pageInfo = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            pageInfo = locationService.getAll(page, size);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            pageInfo = locationService.getAllByUserIdAndPage(page, size, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("pageInfo", pageInfo);
    }


    @GetMapping("/getAll")
    public Result getAll(HttpServletRequest request) {
        List<Location> locationList = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            locationList = locationService.getAll();
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            locationList = locationService.getAllByUserId(userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("locationList", locationList);
    }


    @GetMapping("/getAllAndCollection/{page}/{size}")
    public Result getAllAndCollectionAndPage(@PathVariable Integer page, @PathVariable Integer size, HttpServletRequest request) {
        List<Location> locationList = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            locationList = locationService.getAllAndCollection(page, size);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            locationList = locationService.getAllAndCollectionByPageAndUserId(page, size, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("locationList", locationList);
    }


    @GetMapping("/getAllAndCollection")
    public Result getAllAndCollection(HttpServletRequest request) {
        List<Location> locationList = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            locationList = locationService.getAllAndCollection();
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            locationList = locationService.getAllAndCollectionByUserId(userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("locationList", locationList);
    }


    @RequiresRoles("admin")
    @PostMapping("/add")
    public Result add(@RequestBody Location location) {
        locationService.add(location);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @RequiresRoles("admin")
    @PostMapping("/update")
    public Result update(@RequestBody Location location) {
        locationService.update(location);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @RequiresRoles("admin")
    @PostMapping("/distributeLocation")
    public Result distributeLocation(@RequestBody DistributeLocationVO distributeLocationVO) {
        locationService.distributeLocation(distributeLocationVO.getUserId(), distributeLocationVO.getLocationId());
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @RequiresRoles("admin")
    @PostMapping("/deleteLocation")
    public Result deleteLocation(@RequestBody DistributeLocationVO distributeLocationVO) {
        locationService.deleteLocationUserRelation(distributeLocationVO.getUserId(), distributeLocationVO.getLocationId());
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @RequiresRoles("admin")
    @GetMapping("/delete/{locationId}")
    public Result deleteLocation(@PathVariable Integer locationId) {
        locationService.delete(locationId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @GetMapping("/getByGreenHouseId/{greenHouseId}")
    public Result getByGreenHouseId(@PathVariable Integer greenHouseId) {
        Location location = locationService.getByGreenHouseId(greenHouseId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("location", location);
    }

}
