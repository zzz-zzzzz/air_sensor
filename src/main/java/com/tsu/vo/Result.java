package com.tsu.vo;

import com.tsu.constant.HttpStatusConstant;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
public class Result {
    private int status;

//    public static final String UNAUTHORIZED_RESULT = "{status:" + HttpStatusConstant.UNAUTHORIZED + "}";


    private Map<String, Object> data;

    public Result add(String key, Object object) {
        data.put(key, object);
        return this;
    }

    public Result() {
        data = new HashMap<>();
    }

    public static Result success() {
        Result result =
                new Result()
                        .setStatus(HttpStatusConstant.SUCCESS);
        return result;
    }
}
