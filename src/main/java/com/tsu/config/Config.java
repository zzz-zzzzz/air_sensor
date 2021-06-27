package com.tsu.config;

import com.tsu.constant.HttpStatusConstant;
import com.tsu.vo.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zzz
 */
@Configuration
public class Config {

    @Bean
    public Result unauthorizedResult() {
        return new Result()
                .setStatus(HttpStatusConstant.UNAUTHORIZED);
    }
}
