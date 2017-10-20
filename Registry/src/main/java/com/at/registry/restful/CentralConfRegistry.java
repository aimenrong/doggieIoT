package com.at.registry.restful;

import com.at.registry.bean.CentralConfEntry;
import com.at.registry.exception.ServiceException;
import com.at.registry.service.CentralConfRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Terry LIANG on 2017/9/24.
 */
@Component("CentralConfRegistry")
@Path("registry")
public class CentralConfRegistry {
    @Resource(name = "CentralConfRegistryServiceImpl")
    private CentralConfRegistryService centralConfRegistryService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CentralConfRegistry.class);

    @POST
    @Path("setPropertyWithDesc")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setConfWithDesc(@FormParam("key") String key, @FormParam("value") String value, @FormParam("description") String description) {
        try {
            centralConfRegistryService.set(key, value, description);
        } catch (ServiceException e) {
            LOGGER.error("Catch exception when setting configuration", e);
            return Response.status(200).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
        return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
    }

    @POST
    @Path("setProperty")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setConf(@FormParam("key") String key, @FormParam("value") String value) {
        try {
            centralConfRegistryService.set(key, value);
        } catch (ServiceException e) {
            LOGGER.error("Catch exception when setting configuration", e);
            return Response.status(200).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
        return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
    }

    @GET
    @Path("getProperty")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getConf(@QueryParam("key") String key) {
        try {
            CentralConfEntry centralConfEntry = (CentralConfEntry) centralConfRegistryService.get(key);
            return Response.status(200).entity(centralConfEntry).build();
        } catch (ServiceException e) {
            LOGGER.error("Catch exception when getting configuration", e);
            return Response.status(500).entity(GenernalResponse.build(500, e.getMessage())).build();
        }
    }

}
