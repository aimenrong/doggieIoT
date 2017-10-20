package at.spc.service.impl;

import at.spc.bean.registry.ServiceComponent;
import at.spc.bean.registry.ServiceNode;
import at.spc.service.RegistryService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
@Service("RegistryServiceImpl")
public class RegistryServiceImpl implements RegistryService {
    @Resource(name = "RestTemplate")
    private RestTemplate restTemplate;
    @Value("${registry.lookup.service.url}")
    private String serviceLookupUrl;
    @Value("${amqrouter.node.id}")
    private int routerNodeId;
    @Value("${amqrouter.subnode.id}")
    private int routerSubNodeId;

    @Override
    public String resolveRouterService(String serviceId) {
        String str = restTemplate.getForObject(String.format(this.serviceLookupUrl, serviceId), String.class);
        try {
            ServiceComponent serviceComponent = JSON.parseObject(str, ServiceComponent.class);
            if (serviceComponent.getServiceNodes().containsKey(routerNodeId)) {
                ServiceNode serviceNode = serviceComponent.getServiceNodes().get(routerNodeId);
                if (serviceNode.getSubNodes().containsKey(routerSubNodeId))
                return serviceNode.getSubNodes().get(routerSubNodeId).getServiceUrl();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Override
    public String resolveKafkaService(String serviceId) {
        return null;
    }

    @Override
    public String resolveService(String serviceId) {
        String str = restTemplate.getForObject(String.format(this.serviceLookupUrl, serviceId), String.class);
        try {
            ServiceComponent serviceComponent = JSON.parseObject(str, ServiceComponent.class);
            if (serviceComponent.getServiceNodes().containsKey(routerNodeId)) {
                ServiceNode serviceNode = serviceComponent.getServiceNodes().get(routerNodeId);
                if (serviceNode.getSubNodes().containsKey(routerSubNodeId))
                    return serviceNode.getSubNodes().get(routerSubNodeId).getServiceUrl();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}
