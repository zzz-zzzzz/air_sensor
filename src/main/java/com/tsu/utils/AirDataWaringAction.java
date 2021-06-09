package com.tsu.utils;

/**
 * @author zzz
 */
@FunctionalInterface
public interface AirDataWaringAction {
    /**
     * 是一个函数式接口中的方法，用于应对验证不同的数据时使用同一个方法
     * @return
     */
    void execute();
}
