package com.tsu.config;


import com.tsu.constant.MqttChannelName;
import com.tsu.service.RelayService;
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
@ConditionalOnProperty("mqtt.channel02.url")
@Slf4j
public class MqtChannel02Config implements ApplicationListener<ApplicationEvent> {

    @Autowired
    RelayService relayService;


    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        //todo 优化一下
        if (event instanceof MqttSubscribedEvent) {
            log.info("event out put {}", event);
        }
    }


    @Value("${mqtt.channel02.id}")
    private String clientId;


    @Value("${mqtt.channel02.input-topic}")
    private String[] inputTopic;


    @Value("${mqtt.channel02.url}")
    private String[] mqttServices;


    @Value("${mqtt.channel02.user:#{null}}")
    private String user;


    @Value("${mqtt.channel02.password:#{null}}")
    private String password;

    @Value("${mqtt.channel02.keep-alive-interval:300}")
    private Integer KeepAliveInterval;

    /**
     * 是否不保持session,默认为session保持
     */
    @Value("${mqtt.channel02.clean-session:false}")
    private Boolean CleanSession;

    /**
     * 是否自动重联，默认为开启自动重联
     */
    @Value("${mqtt.channel02.automatic-reconnect:true}")
    private Boolean AutomaticReconnect;

    /**
     * 连接超时,默认为30秒
     */
    @Value("${mqtt.channel02.completion-timeout:30000}")
    private Long CompletionTimeout;

    /**
     * 通信质量
     */
    @Value("${mqtt.channel02.qos:1}")
    private Integer qos;

    /**
     * MQTT连接配置
     *
     * @return 连接工厂
     */
    @Bean("mqttOutputClientFactory")
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(mqttServices);
        if (!ObjectUtils.isEmpty(user)) {
            options.setUserName(user);
        }
        if (!ObjectUtils.isEmpty(password)) {
            options.setPassword(password.toCharArray());
        }
        options.setKeepAliveInterval(KeepAliveInterval);
        options.setAutomaticReconnect(AutomaticReconnect);
        options.setCleanSession(CleanSession);
        factory.setConnectionOptions(options);
        return factory;
    }


    @Bean
    @ServiceActivator(inputChannel = MqttChannelName.OUTPUT_DATA_01)
    public AbstractMqttMessageHandler MQTTOutAdapter(@Qualifier("mqttOutputClientFactory") MqttPahoClientFactory connectionFactory) {
        MqttPahoMessageHandler outGate = new MqttPahoMessageHandler(clientId + "_put", connectionFactory);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        outGate.setAsync(true);
        outGate.setCompletionTimeout(CompletionTimeout);
        outGate.setDefaultQos(qos);
        outGate.setConverter(converter);
        return outGate;
    }


    @Bean
    public MessageProducerSupport mqttInput_01(@Qualifier("mqttOutputClientFactory") MqttPahoClientFactory mqttPahoClientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(clientId, mqttPahoClientFactory, inputTopic);
        DefaultPahoMessageConverter converter = new DefaultPahoMessageConverter();
        converter.setPayloadAsBytes(true);
        adapter.setCompletionTimeout(CompletionTimeout);
        adapter.setConverter(converter);
        adapter.setQos(qos);
        adapter.setOutputChannelName(MqttChannelName.INPUT_DATA_02);
        return adapter;
    }

    @ServiceActivator(inputChannel = MqttChannelName.INPUT_DATA_02)
    public void upCase(Message<byte[]> in) {
        log.info("接收到MQTT服务器传递的数据，{}", new String(in.getPayload()));
        relayService.saveMqttBytes(in.getPayload());
    }

}
