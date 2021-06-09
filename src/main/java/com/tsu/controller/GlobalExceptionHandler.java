package com.tsu.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.tsu.constant.HttpStatusConstant;
import com.tsu.exception.HasUserException;
import com.tsu.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.AuthorizationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zzz
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕捉没有权限出现的异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(AuthorizationException.class)
    @ResponseBody
    public Result handleAuthorizationException(Exception e) {
        log.error("出现异常 {} ", e.getMessage());
        return new Result()
                .setStatus(HttpStatusConstant.NO_PERMISSION);
    }


    @ExceptionHandler(HasUserException.class)
    @ResponseBody
    public Result handleHasUserException(Exception e) {
        log.error("出现异常 {} ", e.getMessage());
        return new Result()
                .setStatus(HttpStatusConstant.HAS_USER);
    }


}
