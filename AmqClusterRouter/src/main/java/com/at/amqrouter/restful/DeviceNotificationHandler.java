package com.at.amqrouter.restful;

import com.at.amqrouter.bean.DeviceNotification;
import com.at.amqrouter.bean.GeneralResponse;
import com.at.amqrouter.exception.BrokerException;
import com.at.amqrouter.service.BrokerSenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by eyonlig on 9/12/2017.
 */
@Component("DeviceNotificationHandler")
@Path("deviceNotification")
public class DeviceNotificationHandler {
    @Resource(name = "JmsBrokerSender")
    private BrokerSenderService brokerSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceNotificationHandler.class);

    @GET
    @Path("dummy")
    public Response dummy() {
        return Response.status(200).entity(GeneralResponse.buildResponse(200, "success")).build();
    }

    @POST
    @Path("sendNotification")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response sendNotification(DeviceNotification notification) {
        LOGGER.info("Receive notification to {}, content is {} ",notification.getDeviceId(), notification.getContent());
        try {
            brokerSender.sendQueueMsg(notification.getContent(), notification.getDeviceId());
        } catch (BrokerException e) {
            LOGGER.error("Catch exception when sending notification", e);
            return Response.status(500).entity(GeneralResponse.buildResponse(500, e.getMessage())).build();
        }
        return Response.status(200).entity(GeneralResponse.buildResponse(200, "success")).build();
    }

}
