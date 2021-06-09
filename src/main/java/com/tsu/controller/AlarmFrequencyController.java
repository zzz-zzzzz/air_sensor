package com.tsu.controller;

import com.tsu.constant.HttpStatusConstant;
import com.tsu.service.AlarmFrequencyService;
import com.tsu.vo.Result;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author zzz
 */
@RestController
@RequestMapping("/alarmFrequency")
@CrossOrigin
public class AlarmFrequencyController {

    @Value("${system.admin-code}")
    private String adminCode;

    @Autowired
    AlarmFrequencyService alarmFrequencyService;

}
