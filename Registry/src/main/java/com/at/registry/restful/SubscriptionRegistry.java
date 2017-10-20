package com.at.registry.restful;

import com.at.registry.bean.SubscriptionBean;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.SubscriptionRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Component
@Path("registry")
public class SubscriptionRegistry {
    @Resource(name = "SimpleSubscriptionRegistryService")
    private SubscriptionRegistryService subscriptionRegistryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionRegistry.class);

    @GET
    @Path("lookupSubscription/device/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupSubscription(@PathParam("deviceId") String deviceId) {
        try {
            List<SubscriptionBean> subscriptionBeanList = subscriptionRegistryService.lookupSubscriptionByDeviceId(deviceId);
            if (null == subscriptionBeanList || 0 == subscriptionBeanList.size()) {
                return Response.status(404).entity(GenernalResponse.build(404, "Subscription not found")).build();
            }
            return Response.status(200).entity(subscriptionBeanList).build();
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
    }

    @POST
    @Path("createSubscription")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSubscription(SubscriptionBean subscriptionBean) {
        try {
            subscriptionRegistryService.addSubscription(subscriptionBean);
            return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
    }

    @PUT
    @Path("updateSubscription")
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateSubscription(SubscriptionBean subscriptionBean) {
        try {
            subscriptionRegistryService.updateSubscription(subscriptionBean);
            return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
    }

    @DELETE
    @Path("deleteSubscription")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteSubscription(@QueryParam("subscriptionId") String subscriptionId) {
        try {
            subscriptionRegistryService.deleteSubscription(subscriptionId);
            return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.status(500).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
    }
}
