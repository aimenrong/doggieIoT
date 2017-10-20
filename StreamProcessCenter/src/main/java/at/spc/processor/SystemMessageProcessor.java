package at.spc.processor;

import at.spc.intf.SubFunction;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/21.
 */
public class SystemMessageProcessor implements Processor<String, String>, SubFunction {
    private static final String FUNCTION_ID = "SystemMessageProcessor";
    private ProcessorContext context;
    private Map<String, String> kvStore = new ConcurrentHashMap<String, String>();

    @Override
    public void init(ProcessorContext processorContext) {
        System.out.println("Call SystemMessageProcessor init");
        this.context = processorContext;
    }

    @Override
    public void process(String s, String s2) {
        System.out.println("Call SystemMessageProcessor process");
    }

    private void parseMeta() {

    }

    private void downloadJar() {

    }

    private void registerInSpring() {

    }

    private void loadFunction() {

    }

    @Override
    public void punctuate(long l) {

    }

    @Override
    public void close() {

    }

    @Override
    public String getFunctionId() {
        return FUNCTION_ID;
    }
}
