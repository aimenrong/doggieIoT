package com.at.registry.restful;

import com.at.registry.bean.DeviceInfo;
import com.at.registry.service.DeviceRegistryService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Terry LIANG on 2017/9/14.
 */
@Component
@Path("registry")
public class DeviceRegistry {
    @Resource(name = "SimpleDeviceRegistryService")
    private DeviceRegistryService deviceRegistryService;

    @POST
    @Path("registerDevice")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registryDevice(DeviceInfo deviceInfo) {
        deviceRegistryService.registryDevice(deviceInfo);
        return Response.status(200).entity("success").build();
    }

    @POST
    @Path("unregisterDevice/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response unregistryDevice(@PathParam("deviceId") String deviceId) {
        deviceRegistryService.unregistryDevice(deviceId);
        return Response.status(200).entity("success").build();
    }

    @GET
    @Path("lookupDevice/{deviceId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response lookupDevice(@PathParam("deviceId") String deviceId) {
        DeviceInfo deviceInfo = deviceRegistryService.lookupDevice(deviceId);
        return Response.status(200).entity(deviceInfo).build();
    }
}
