package at.spc.processor;

import at.spc.bean.GeoFenceBean;
import com.alibaba.fastjson.JSON;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Component("ApplicationEventProcessor")
public class ApplicationEventProcessor implements Processor<String, String> {
    private ProcessorContext context;
    private Map<String, String> kvStore = new ConcurrentHashMap<String, String>();
    private final long MIN_DIST = 10;


    public void init(ProcessorContext context) {
        System.out.println("Call init");
        this.context = context;
    }

    public void process(String dummy, String line) {
        GeoFenceBean geoFenceBean = JSON.parseObject(line, GeoFenceBean.class);
        if (null != geoFenceBean) {
        }
    }

    public void punctuate(long timestamp) {
        System.out.println("Call punctuate");
        Iterator<Map.Entry<String, String>> iter = this.kvStore.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, String> entry = iter.next();
            context.forward(entry.getKey(), entry.getKey());
        }
        // commit the current processing progress
        context.commit();
    }

    public void close() {
        System.out.println("Call close");
    }
}
