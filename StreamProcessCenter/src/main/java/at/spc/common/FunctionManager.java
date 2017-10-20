package at.spc.common;

import at.spc.App;
import at.spc.aspect.AspectProperties;
import at.spc.bean.FunctionMeta;
import at.spc.exception.ParseFunctionMetaException;
import at.spc.faas.ExternalClassManager;
import at.spc.intf.*;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/9/21.
 */
@Component("FunctionManager")
public class FunctionManager {
    private Map<String, FunctionChain> functionMap = new ConcurrentHashMap<String, FunctionChain>();
    @Resource(name = "ExternalClassManager")
    private ExternalClassManager externalClassManager;
    @Value("${kafka.service.url:localhost:9092}")
    private String kafkaServiceUrl;
    @Value("${spc.app.id}")
    private String spcAppId;
    private static final Logger LOGGER = LoggerFactory.getLogger(FunctionManager.class);

    public void deactivateFunction(String functionId) {
        functionMap.remove(functionId).getKafkaStreams().close();
    }

    public void activateFunction(FunctionChain functionChain) {
        LOGGER.info("Start to process functin chain " + functionChain.getFunctionChainId());
        TopologyBuilder builder = KafkaStreamFactory.createTopologyBuilder();
        Function previousFunction = null;
        for (Function function : functionChain.getFunctionChain()) {
            LOGGER.info(String.format("Start to process function %s, topic is %s", function.getFunctionId(), function.getTopic()));
            if (function instanceof NodeFunction) {
                if (previousFunction != null) {
                    LOGGER.info(String.format("Start to add sink %s to function %s, topic is %s",function.getSinkName(), previousFunction.getFunctionId(), function.getTopic()));
                    // Refactor below code later
                    builder.addSink(function.getSinkName(), function.getTopic(), previousFunction.getSubFunctions().get(previousFunction.getSubFunctions().size() - 1).getFunctionId());
                }
                builder.addSource(function.getFunctionId(), function.getTopic());
                for (SubFunction subFunction : function.getSubFunctions()) {
                    LOGGER.info(String.format("Start to add subfunction %s to function %s",subFunction.getFunctionId(), function.getFunctionId()));
                    builder.addProcessor(subFunction.getFunctionId(), () -> {
                        return (Processor) App.getContext().getBean(subFunction.getFunctionId());
                    }, function.getFunctionId());
                }
            } else if (function instanceof LeafFunction) {
                if (previousFunction != null) {
                    LOGGER.info(String.format("Start to add sink %s to function %s, topic is %s",function.getSinkName(), previousFunction.getFunctionId(), function.getTopic()));
                    // Refactor below code later
                    builder.addSink(function.getSinkName(), function.getTopic(), previousFunction.getSubFunctions().get(previousFunction.getSubFunctions().size() - 1).getFunctionId());
                }
                builder.addSource(function.getFunctionId(), function.getTopic());
                for (SubFunction subFunction : function.getSubFunctions()) {
                    LOGGER.info(String.format("Start to add subfunction %s to function %s",subFunction.getFunctionId(), function.getFunctionId()));
                    builder.addProcessor(subFunction.getFunctionId(), () -> {
                        return (Processor)App.getContext().getBean(subFunction.getFunctionId());
                    }, function.getFunctionId());
                }
            }
            previousFunction = function;
        }
        KafkaStreams streams = KafkaStreamFactory.getKafkaStreams(builder, spcAppId, kafkaServiceUrl);
        streams.start();
        functionChain.setKafkaStreams(streams);
        functionMap.put(functionChain.getFunctionChainId(), functionChain);
    }

    public Map<String, FunctionChain> getFunctionMap() {
        return functionMap;
    }

    public FunctionChain parseFunctionMeta(FunctionMeta functionMeta) {
        if (functionMeta.getFunctionBeanNames() == null || null == functionMeta.getFunctionConfigurationClassName()) {
            throw new IllegalArgumentException("Found argument is null");
        }
        FunctionChain functionChain = new FunctionChain();
        try {
            functionChain.setFunctionChainId(functionMeta.getFunctionChainId());
            Class configurationClass = null;
            if (functionMeta.isExternalFunction()) {
                externalClassManager.loadExternalJar(functionMeta.getFunctionChainId(), functionMeta.getFunctionJarRootPath());
                configurationClass = externalClassManager.loadClass(functionMeta.getFunctionChainId(), functionMeta.getFunctionConfigurationClassName());
            } else {
                configurationClass = Class.forName(functionMeta.getFunctionConfigurationClassName());
            }
            App.getContext().register(configurationClass);
            for (String functionBeanName : functionMeta.getFunctionBeanNames()) {
                Function function = (Function) App.getContext().getBean(functionBeanName);
                String[] subFunctionBeanNames = functionMeta.getSubFunctionBeanNames().get(functionBeanName);
                for (String subFunctionBeanName : subFunctionBeanNames) {
                    SubFunction subFunction = (SubFunction) App.getContext().getBean(subFunctionBeanName);
                    function.getSubFunctions().add(subFunction);
                }
                functionChain.getFunctionChain().add(function);
            }
        } catch (Exception e) {
            throw new ParseFunctionMetaException(e);
        }
        return functionChain;
    }

}
