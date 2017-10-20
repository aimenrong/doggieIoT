package at.spc.intf.impl;

import at.spc.bean.DeviceEvent;
import at.spc.bean.DeviceNotification;
import at.spc.bean.GeoFenceBean;
import at.spc.bean.SubscriptionBean;
import at.spc.intf.NodeFunction;
import at.spc.intf.SubFunction;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/9.
 */
@Component("NotificationOnArriveFunction")
public class DeviceEventFunction implements NodeFunction {
    private static final String FUNCTION_ID = "DeviceEventFunction";
    private List<SubFunction> subFunctions = new LinkedList<>();

    @Override
    public List<SubFunction> getSubFunctions() {
        return subFunctions;
    }

    @Override
    public String getFunctionId() {
        return FUNCTION_ID;
    }

    @Override
    public String getTopic() {
        return "device-event";
    }

    @Override
    public String getSinkName() {
        return "DeviceEventSink";
    }
}
