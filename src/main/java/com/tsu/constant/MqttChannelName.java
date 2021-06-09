package com.tsu.constant;

/**
 * @author zzz
 */
public class MqttChannelName {
    /**
     * 入站管道01，用于接收设备的实时数据
     */
    public final static String INPUT_DATA_01 = "input_data_01";

    /**
     * 入站管道02，用于接收节电器操作是否成功
     */
    public final static String INPUT_DATA_02 = "input_data_02";

    /**
     * 入站管道03，用于接受继电器的实时状态
     */
    public final static String INPUT_DATA_03 = "input_data_03";


    /**
     * mqtt出站管道名称
     */
    public final static String OUTPUT_DATA_01 = "output_data_01";
}
