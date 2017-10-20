package at.spc.intf.impl;

import at.spc.bean.DeviceNotification;
import at.spc.bean.GenernalRestResponse;
import at.spc.bean.GeoFenceBean;
import at.spc.intf.LeafFunction;
import at.spc.intf.SubFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.xml.ws.Response;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Component("NotificationFunction")
public class DeviceNotificationFunction implements LeafFunction {
    private static final String FUNCTION_ID = "DeviceNotificationFunction";
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
        return "notification-topic";
    }

    @Override
    public String getSinkName() {
        return "DeviceNotificationSink";
    }
}
