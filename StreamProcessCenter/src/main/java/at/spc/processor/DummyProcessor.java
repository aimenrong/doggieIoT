package at.spc.processor;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/21.
 */
public class DummyProcessor implements Processor<String, String> {
    private ProcessorContext context;
    private Map<String, String> kvStore = new ConcurrentHashMap<String, String>();
    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
        this.context.schedule(3000);
        System.out.println(String.format("Call DummyProcessor init"));
    }

    @Override
    public void process(String s, String s2) {
        System.out.println(String.format("Call DummyProcessor : %s = %s", s, s2));
    }

    @Override
    public void punctuate(long l) {

    }

    @Override
    public void close() {

    }
}
