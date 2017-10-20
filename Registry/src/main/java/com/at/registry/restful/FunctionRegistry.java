package com.at.registry.restful;

import com.at.registry.bean.SubscriptionBean;
import com.at.registry.exception.ServiceException;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.Map;

/**
 * Created by Terry LIANG on 2017/9/18.
 */
@Path("registry")
public class FunctionRegistry {

    @POST
    @Path("createFunction")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createSubscription(SubscriptionBean subscriptionBean) {
        return Response.status(200).entity(GenernalResponse.build(200, "success")).build();
    }

}

class FunctionParams {
    public String functionId;
    public String jarPath;
    public String configurationClassName;
    public String[] functionBeanNames;
    public Map<String, String[]> subFuncBeansMapping;
}