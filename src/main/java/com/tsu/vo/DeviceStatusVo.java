package com.tsu.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zzz
 */
@Data
@Accessors(chain = true)
public class DeviceStatusVo {
    private Long count;
    private Long alarmCount;
    private Long onlineCount;
}
