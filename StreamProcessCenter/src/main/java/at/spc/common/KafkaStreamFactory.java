package at.spc.common;

import at.spc.App;
import at.spc.aspect.AspectProperties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.TopologyBuilder;

import java.util.Properties;

/**
 * Created by Terry LIANG on 2017/10/3.
 */
public class KafkaStreamFactory {
    public static KafkaStreams getKafkaStreams(TopologyBuilder builder, String appId, String kafkaBootstrapUrl) {
        KafkaStreams streams = new KafkaStreams(builder, buildKafkaProperties(appId, kafkaBootstrapUrl));
        return streams;
    }

    private static Properties buildKafkaProperties(String appId, String kafkaBootstrapUrl) {
        AspectProperties config = (AspectProperties) App.getContext().getBean("AspectProperties");
        config.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapUrl);
        config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        return config;
    }

    public static TopologyBuilder createTopologyBuilder() {
        return new TopologyBuilder();
    }
}
