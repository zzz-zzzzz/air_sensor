package com.tsu.controller;

import com.github.pagehelper.PageInfo;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.entity.User;
import com.tsu.exception.HasUserException;
import com.tsu.service.UserService;
import com.tsu.utils.JwtUtil;
import com.tsu.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzz
 */
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Value("${system.admin-code}")
    private String adminCode;

    @PostMapping("/login")
    public Result login(@RequestBody User user, HttpServletRequest request) {
        Subject subject = SecurityUtils.getSubject();
        String username = user.getUsername();
        String password = user.getPassword();
        try {
            subject.login(new UsernamePasswordToken(username, password));
            Integer userId = (Integer) request.getAttribute("userId");
            boolean isAdmin = subject.hasRole(adminCode);
            return Result
                    .success()
                    .add("token", JwtUtil.getJwtToken(userId, username, isAdmin));
        } catch (Exception e) {
            return new Result()
                    .setStatus(HttpStatusConstant.LOGIN_FAILURE);
        }
    }

    @RequiresRoles("admin")
    @PostMapping("/add")
    public Result add(@RequestBody User user) {
        try {
            userService.add(user);
        } catch (HasUserException e) {
            throw e;
        }
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("message", "添加成功");
    }

    @RequiresRoles("admin")
    @GetMapping("/delete/{userId}")
    public Result delete(@PathVariable Integer userId) {
        userService.delete(userId);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("message", "删除成功");
    }

    @RequiresRoles("admin")
    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        userService.update(user);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("message", "修改成功");
    }

    @RequiresRoles("admin")
    @GetMapping("/getAll/{page}/{size}")
    public Result getAll(@PathVariable Integer page, @PathVariable Integer size) {
        PageInfo<User> pageInfo = userService.getAllByPage(page, size);
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("pageInfo", pageInfo);
    }

    @RequiresRoles("admin")
    @GetMapping("/sayHello")
    public Result sayHello() {
        return new Result()
                .setStatus(HttpStatusConstant.SUCCESS)
                .add("message", "helloWorld");
    }


}
