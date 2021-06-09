package com.tsu.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
public class AlarmFrequency {
    private Integer id;
    private Integer deviceId;
    private Integer frequency;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private Date alarmDate;
}
