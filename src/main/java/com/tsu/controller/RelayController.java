package com.tsu.controller;

import com.github.pagehelper.PageInfo;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.Relay;
import com.tsu.service.RelayService;
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
@RequestMapping("/relay")
@CrossOrigin
public class RelayController {
    @Autowired
    RelayService relayService;


    @Value("${system.admin-code}")
    private String adminCode;

    @GetMapping("/getByGreenHouseId/{greenHouseId}")
    public Result getByGreenHouseId(@PathVariable Integer greenHouseId) {
        List<Relay> relayList = relayService.getByGreenHouseId(greenHouseId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("relayList", relayList);
    }

    @GetMapping("/getAllHasGreenHouseByPage/{page}/{size}")
    public Result getAllByPage(@PathVariable Integer page, @PathVariable Integer size, HttpServletRequest request) {
        PageInfo<Relay> pageInfo = null;
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            pageInfo = relayService.getAllHasGreenHouseByPage(page, size);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            pageInfo = relayService.getAllHasGreenHouseByPageAndUserId(page, size, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("pageInfo", pageInfo);
    }

    @GetMapping("/delete/{id}")
    public Result delete(@PathVariable Integer id, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            relayService.delete(id);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            relayService.checkDelete(id, userId);
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @PostMapping("/update")
    public Result update(@RequestBody Relay relay, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            relayService.update(relay);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                relayService.checkUpdate(relay, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }

        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @PostMapping("/add")
    public Result add(@RequestBody Relay relay, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            relayService.add(relay);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                relayService.checkAdd(relay, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @GetMapping("/sendInstruction/{id}/{action}")
    public Result sendInstruction(@PathVariable Integer id, @PathVariable Boolean action, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            relayService.sendInstruction(id, action);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                relayService.checkAndSendInstruction(id, action, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

    @GetMapping("/switchAutoTrigger/{id}/{autoTrigger}")
    public Result switchAutoTrigger(@PathVariable Integer id, @PathVariable Boolean autoTrigger, HttpServletRequest request) {
        if (SecurityUtils.getSubject().hasRole(adminCode)) {
            relayService.switchAutoTrigger(id, autoTrigger);
        } else {
            Integer userId = (Integer) request.getAttribute("userId");
            try {
                relayService.checkSwitchAutoTrigger(id, autoTrigger, userId);
            } catch (AuthorizationException e) {
                throw e;
            }
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS);
    }

}
