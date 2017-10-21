package at.spc.test;

import at.spc.bean.DeviceEvent;
import at.spc.bean.GeoFenceBean;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by Terry LIANG on 2017/9/5.
 */
public class TestProducer {

    public void testProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        DeviceEvent deviceEvent = new DeviceEvent();
        deviceEvent.setLongitude(10.2);;
        deviceEvent.setLatitude(85.3);
        deviceEvent.setDeviceId("integrateDevice1");
        List<DeviceEvent> eventList = new ArrayList<>();
        eventList.add(deviceEvent);
        String key = JSON.toJSONString(deviceEvent), value = key;
        producer.send(new ProducerRecord<String, String>("device-event", key, value));
        producer.close();


    }
}
