package at.spc.test.common;

import at.spc.App;
import at.spc.bean.FunctionMeta;
import at.spc.common.FunctionManager;
import at.spc.common.KafkaStreamFactory;
import at.spc.intf.Function;
import at.spc.intf.FunctionChain;
import at.spc.intf.SubFunction;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.processor.ProcessorSupplier;
import org.apache.kafka.streams.processor.TimestampExtractor;
import org.apache.kafka.streams.processor.TopologyBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by eyonlig on 9/22/2017.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({App.class, KafkaStreamFactory.class})
public class TestFunctionManager {
    @Mock private AnnotationConfigApplicationContext mockContext;
    private FunctionManager functionManager;
    @Mock private SubFunction mockSubFunction;
    @Mock private Function mockFunction;
    private List mockList = new LinkedList();
    @Mock private FunctionChain mockFunctionChain;
    @Mock KafkaStreams kafkaStreams;
    @Mock TopologyBuilder topologyBuilder;

    @Before
    public void init() {
        PowerMockito.mockStatic(App.class);
        PowerMockito.mockStatic(KafkaStreamFactory.class);
        PowerMockito.when(App.getContext()).thenReturn(mockContext);
        PowerMockito.when(KafkaStreamFactory.getKafkaStreams(any(TopologyBuilder.class), anyString(), anyString())).thenReturn(kafkaStreams);
        PowerMockito.when(KafkaStreamFactory.createTopologyBuilder()).thenReturn(topologyBuilder);
        when(mockContext.getBean("SystemMessageProcessor")).thenReturn(mockSubFunction);
        when(mockContext.getBean("SystemMessageFunction")).thenReturn(mockFunction);
        when(mockContext.getBean("DeviceEventFunction")).thenReturn(mockFunction);
        when(mockContext.getBean("DeviceNotificationFunction")).thenReturn(mockFunction);
        when(mockContext.getBean("DeviceEventProcessor")).thenReturn(mockSubFunction);
        when(mockContext.getBean("DeviceNotificationProcessor")).thenReturn(mockSubFunction);
        when(mockFunction.getSubFunctions()).thenReturn(mockList);
//        when(topologyBuilder.addSource(anyString(), anyString())).thenReturn(topologyBuilder);
//        when(topologyBuilder.addSource(any(TopologyBuilder.AutoOffsetReset.class), anyString(), any(TimestampExtractor.class),
//                any(Deserializer.class), any(Deserializer.class), any(String[].class)));
//        when(topologyBuilder.addProcessor(anyString(), any(ProcessorSupplier.class), anyString())).thenReturn(topologyBuilder);
//        when(topologyBuilder.addSink(anyString(), anyString(), anyString())).thenReturn(topologyBuilder);
        functionManager = new FunctionManager();
    }

    @Test
    public void testParseFunctionMeta() {
        FunctionMeta functionMeta = new FunctionMeta();
        functionMeta.setFunctionChainId("testFunctionChainId");
        functionMeta.setFunctionBeanNames(new String[]{"SystemMessageFunction"});
        functionMeta.setFunctionConfigurationClassName("at.spc.common.SystemFunctionConfiguration");
        functionMeta.getSubFunctionBeanNames().put("SystemMessageFunction", new String[]{"SystemMessageProcessor", "SystemMessageProcessor2"});
        FunctionChain functionChain = functionManager.parseFunctionMeta(functionMeta);
        when(mockFunction.getFunctionId()).thenReturn("SystemMessageFunction");
        Assert.assertEquals("SystemMessageFunction", functionChain.getFunctionChain().get(0).getFunctionId());
        verify(mockContext, times(3)).getBean(anyString());
        verify(mockList, times(2)).add(any());
        Assert.assertEquals(1, functionChain.getFunctionChain().size());
    }

    public void testActivateFunction() {
        FunctionChain functionChain = functionManager.parseFunctionMeta(getDeviceEventFunctionMeta());
        functionManager.activateFunction(functionChain);
        verify(topologyBuilder, times(2)).addSource(anyString(), anyString());
        verify(topologyBuilder, times(2)).addProcessor(anyString(), any(ProcessorSupplier.class), anyString());
        verify(topologyBuilder, times(2)).addSink(anyString(), anyString(), anyString());
    }

    private FunctionMeta getDeviceEventFunctionMeta() {
        FunctionMeta functionMeta = new FunctionMeta();
        functionMeta.setFunctionChainId("deviceNotificationFunctionChain");
        functionMeta.setFunctionConfigurationClassName("at.spc.common.DeviceNotificationConfiguration");
        functionMeta.setFunctionBeanNames(new String[]{"DeviceEventFunction", "DeviceNotificationFunction"});
        functionMeta.getSubFunctionBeanNames().put("DeviceEventFunction", new String[]{"DeviceEventProcessor"});
        functionMeta.getSubFunctionBeanNames().put("DeviceNotificationFunction", new String[]{"DeviceNotificationProcessor"});
        return functionMeta;
    }
}
