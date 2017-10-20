package com.at.amqrouter.service.impl;

import com.alibaba.fastjson.JSON;
import com.at.amqrouter.RestfulServer;
import com.at.amqrouter.aspect.AspectProperties;
import com.at.amqrouter.bean.DeviceEvent;
import com.at.amqrouter.service.SpcService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 * Created by eyonlig on 9/27/2017.
 */
@Service("KafkaSpcService")
public class KafkaSpcService implements SpcService<DeviceEvent> {
    @Value("${kafka.service.url}")
    private String kafkaServiceUrl;

    @Value("${spc.device.event.topic}")
    private String spcDeviceEventTopic;

    public void sendMessage(String key, String value) {

    }

    public void sendMessage(DeviceEvent message) {
        Producer<String, String> producer = new KafkaProducer<String, String>(buildKafkaProperties());
        String key = message.getDeviceId();
        String value = JSON.toJSONString(message);
        producer.send(new ProducerRecord<String, String>(spcDeviceEventTopic, key, value));
        producer.close();
    }

    private AspectProperties buildKafkaProperties() {
        AspectProperties props = (AspectProperties)RestfulServer.getContext().getBean("AspectProperties");
        props.put("bootstrap.servers", kafkaServiceUrl);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return props;
    }
}
