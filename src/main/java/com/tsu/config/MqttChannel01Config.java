package com.tsu.config;


import com.tsu.constant.MqttChannelName;
import com.tsu.service.AirDataService;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.event.MqttSubscribedEvent;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.AbstractMqttMessageHandler;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.util.ObjectUtils;

/**
 * @author ZZZ
 */
@EnableIntegration
@Configuration
@ConditionalOnProperty("mqtt.channel01.url")
@Slf4j
public class MqttChannel01Config implements ApplicationListener<ApplicationEvent> {


    @Autowired
    AirDataService airDataService;

    /**
     * 客户端ID
     */
    @Value("${mqtt.channel01.id}")
    private String clientId;

    /**
     * 订阅主题，可以是多个主题
     */
    @Value("${mqtt.channel01.input-topic}")
    private String[] inputTopic;

    /**
     * 服务器地址以及端口
     */
    @Value("${mqtt.channel01.url}")
    private String[] mqttServices;

    /**
     * 用户名
     */
    @Value("${mqtt.channel01.user:#{null}}")
    private String user;

    /**
     * 密码
     */
    @Value("${mqtt.channel01.password:#{null}}")
    private String password;

    /**
     * 心跳时间,默认为5分钟
     */
    @Value("${mqtt.channel01.keep-alive-interval:300}")
    private Integer KeepAliveInterval;

    /**
     * 是否不保持session,默认为session保持
     */
    @Value("${mqtt.channel01.clean-session:false}")
    private Boolean CleanSession;

    /**
     * 是否自动重联，默认为开启自动重联
     */
    @Value("${mqtt.channel01.automatic-reconnect:true}")
    private Boolean AutomaticReconnect;

    /**
     * 连接超时,默认为30秒
     */
    @Value("${mqtt.channel01.completion-timeout:30000}")
    private Long CompletionTimeout;

    /**
     * 通信质量
     */
    @Value("${mqtt.channel01.qos:1}")
    private Integer qos;


    /**
     * MQTT连接配置
     *
     * @return 连接工厂
     */
    @Bean("mqttInputClientFactory")
    public MqttPahoClientFactory mqttClientFactory() {
        //连接工厂类
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        //连接参数
        MqttConnectOptions options = new MqttConnectOptions();
        //连接地址
        options.setServerURIs(mqttServices);
        if (!ObjectUtils.isEmpty(password)) {
            //用户名
            options.setUserName(user);
        }
        if (!ObjectUtils.isEmpty(password)) {
            //密码
            options.setPassword(password.toCharArray());
        }
        //心跳时间
        options.setKeepAliveInterval(KeepAliveInterval);
        //断开是否自动重联
        options.setAutomaticReconnect(AutomaticReconnect);
        //保持session
        options.setCleanSession(CleanSession);
        factory.setConnectionOptions(options);
        return factory;
    }

    /**
     * 入站管道
     *
     * @param mqttPahoClientFactory
     * @return
     */
    @Bean
    public MessageProducerSupport mqttInput(@Qualifier("mqttInputClientFactory") MqttPahoClientFactory mqttPahoClientFactory) {
        //建立订阅连接
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, mqttPahoClientFactory, inputTopic);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        //bytes类型接收
        converter.setPayloadAsBytes(true);
        //连接超时的时间
        adapter.setCompletionTimeout(CompletionTimeout);
        adapter.setConverter(converter);
        //消息质量
        adapter.setQos(qos);
        //输入管道名称
        adapter.setOutputChannelName(MqttChannelName.INPUT_DATA_01);
        return adapter;
    }

    /**
     * MQTT连接时调用的方法
     *
     * @param event
     */
    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof MqttSubscribedEvent) {
            log.info("event {}",event);
            log.info("MQTT服务器连接成功");
        }
    }
    /**
     * 服务器有数据下发
     * 用ServiceActivator配置需要接收的数据管道名称，当该管道里面的数据时，会自动调用该方法
     *
     * @param in 服务器有数据下发时，序列化后的对象，这里使用byte数组
     */
    @ServiceActivator(inputChannel = MqttChannelName.INPUT_DATA_01)
    public void upCase(Message<byte[]> in) {
        log.info("接收到MQTT服务器传递的数据，{}", new String(in.getPayload()));
        airDataService.saveMqttBytes(in.getPayload());
    }
}
