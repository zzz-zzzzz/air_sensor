package com.tsu.gateway;

/**----------------------------------------------------------------
 * 发送消息网关,其它需要发向服务器发送消息时，调用该接口
 **--------------------------------------------------------------**/

import com.tsu.constant.MqttChannelName;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * @author ZZZ
 */
@MessagingGateway
@Component
public interface MqttGateway {
    /**
     * MQTT 发送网关
     * @param a 主题，可以指定不同的数据发布主题，在消息中间件里面体现为不同的消息队列
     * @param out 消息内容
     */
    @Gateway(requestChannel = MqttChannelName.OUTPUT_DATA)
    void send(@Header(MqttHeaders.TOPIC) String a, Message<byte[]> out);
}