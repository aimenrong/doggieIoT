package at.spc.intf;

import org.apache.kafka.streams.KafkaStreams;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/21.
 */
public class FunctionChain {
    private String functionChainId;
    private List<Function> functionChain = new LinkedList<>();
    private KafkaStreams kafkaStreams;

    public String getFunctionChainId() {
        return functionChainId;
    }

    public void setFunctionChainId(String functionChainId) {
        this.functionChainId = functionChainId;
    }

    public List<Function> getFunctionChain() {
        return functionChain;
    }

    public KafkaStreams getKafkaStreams() {
        return kafkaStreams;
    }

    public void setKafkaStreams(KafkaStreams kafkaStreams) {
        this.kafkaStreams = kafkaStreams;
    }
}
