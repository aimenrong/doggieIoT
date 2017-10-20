package com.at.registry;

import com.at.registry.common.SpringJavaConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Created by Terry LIANG on 2017/9/14.
 */
public class RestfulServer {
    private static final String PORT_KEY = "registry.service.port";

    public RestfulServer() throws Exception {
        ResourceConfig config = new ResourceConfig();
        config.packages("com.at.registry");
        config.property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
        config.register(JacksonFeature.class);
        config.register(MultiPartFeature.class);
        ServletHolder servlet = new ServletHolder(new ServletContainer(config));
        int servicePort = Integer.parseInt(System.getProperty(PORT_KEY, "2222"));
        Server server = new Server(servicePort);
        ServletContextHandler context = new ServletContextHandler(server, "/*");
        context.addEventListener(new ContextLoaderListener());
        context.addEventListener(new RequestContextListener());
        context.addServlet(servlet, "/*");
        context.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        context.setInitParameter("contextConfigLocation", SpringJavaConfiguration.class.getName());
        server.start();
        server.join();
    }

    public static void main(String[] args) throws Exception {
        new RestfulServer();
    }
}
