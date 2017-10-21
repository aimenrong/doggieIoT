package com.at.amqrouter;

import com.at.amqrouter.common.Constants;
import com.at.amqrouter.common.SpringOnLoadConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

/**
 * Created by eyonlig on 9/12/2017.
 */
public class RestfulServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulServer.class);
    private static AnnotationConfigWebApplicationContext springContext;

    public RestfulServer() throws Exception {
        JerseyConfig config = new JerseyConfig();

        ServletHolder servlet = new ServletHolder(new ServletContainer(config));

        int servicePort = Integer.parseInt(System.getProperty(Constants.PORT_KEY, "3333"));
        registerLocalService("ROUTER-SERVICE", servicePort);

        Server server = new Server(servicePort);
        ServletContextHandler sc = new ServletContextHandler(server, "/*");
        sc.addEventListener(new ContextLoaderListener());
        sc.addEventListener(new RequestContextListener());
        sc.addServlet(servlet, "/*");
        sc.setInitParameter("contextClass", AnnotationConfigWebApplicationContext.class.getName());
        sc.setInitParameter("contextConfigLocation", SpringOnLoadConfiguration.class.getName());
        server.start();
        springContext = (AnnotationConfigWebApplicationContext)sc.getServletContext().
                getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

        if (null == springContext) {
            LOGGER.warn("Spring Context is null");
        }

        server.join();

    }

    private void registerLocalService(String serviceId, int port) {
        StringBuffer sb = new StringBuffer(System.getProperty(Constants.SERVICE_KEY, ""));
        System.setProperty(Constants.SERVICE_KEY, sb.append("," + String.format("%s:%d", serviceId, port)).toString());
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("config.path", "E:/git/project/doggieIoT/doggieIoT/build/amq-cluster-router/conf/router.properties");
        new RestfulServer();
    }

    public static ApplicationContext getContext() {
        return springContext;
    }
}

class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("com.at.amqrouter");
        property(ServerProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
        register(JacksonFeature.class);
    }
}