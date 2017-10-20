package com.at.registry.restful;

import com.at.registry.bean.registry.BrokerServiceComponent;
import com.at.registry.bean.registry.ServiceComponent;
import com.at.registry.bean.ServiceParam;
import com.at.registry.bean.registry.ServiceNode;
import com.at.registry.bean.registry.ServiceSubNode;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.ServiceRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Terry LIANG on 2017/9/14.
 */
@Component("ServiceRegistry")
@Path("registry")
public class ServiceRegistry {
    @Resource(name = "SimpleServiceRegistryService")
    private ServiceRegistryService serviceRegistryService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);


    @POST
    @Path("registerService")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registryService(ServiceParam serviceParam) {
        try {
            LOGGER.info("Register service, serviceId : {}, nodeId : {}, subNodeId : {} serviceUrl : {}", serviceParam.getServiceId(), serviceParam.getNodeId(),
                    serviceParam.getSubNodeId(), serviceParam.getServiceUrl());
            serviceRegistryService.registerService(serviceParam);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(200, e.getMessage())).build();
        }
        return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
    }

    @GET
    @Path("heartBeat")
    @Produces(MediaType.APPLICATION_JSON)
    public Response processHeartBeat(@QueryParam("serviceId") String serviceId, @QueryParam("nodeId") String nodeId,
                                     @QueryParam("subNodeId") String subNodeId) {
        try {
            LOGGER.info("HeartBeat message, serviceId : {}, nodeId : {}, subNodeId : {}", serviceId, nodeId, subNodeId);
            serviceRegistryService.updateHeartBeat(serviceId, nodeId, subNodeId);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
        return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
    }

    @GET
    @Path("lookupService")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupService(@QueryParam("serviceId") String serviceId) {
        try {
            LOGGER.info("Lookup service, serviceId : {}", serviceId);
            ServiceComponent serviceComponent = serviceRegistryService.getService(serviceId);
            if (null != serviceComponent) {
                return Response.status(200).entity(serviceComponent).build();
            } else {
                return Response.status(404).entity(GenernalResponse.build(404, String.format("Service id with %s not found", serviceId))).build();
            }
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(200, e.getMessage())).build();
        }
    }

    @GET
    @Path("lookupBrokerService")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupBrokerService(@QueryParam("serviceId") String serviceId, @QueryParam("brokerId") String brokerId) {
        try {
            LOGGER.info("Lookup service, serviceId : {}, brokerId : {}", serviceId, brokerId);
            ServiceComponent serviceComponent = serviceRegistryService.getService(serviceId);
            if (null != serviceComponent) {
                BrokerServiceComponent brokerServiceComponent = BrokerServiceComponent.buildBrokerServiceComponent(serviceId, brokerId);
                ServiceNode serviceNode = serviceComponent.getServiceNodes().get(brokerId);
                Iterator<Map.Entry<String, ServiceSubNode>> iterator = serviceNode.getSubNodes().entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, ServiceSubNode> entry = iterator.next();
                    brokerServiceComponent.getServiceUrls().put(entry.getKey(), entry.getValue().getServiceUrl());
                }

                return Response.status(200).entity(brokerServiceComponent).build();
            } else {
                return Response.status(404).entity(GenernalResponse.build(404, String.format("Service id with %s not found", serviceId))).build();
            }
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(200, e.getMessage())).build();
        }
    }
}

