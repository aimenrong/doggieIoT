package at.spc.aspect;

import at.spc.service.RegistryService;
import org.apache.kafka.streams.StreamsConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by eyonlig on 9/25/2017.
 */
@Aspect
@Component("AspectManager")
public class AspectManager {
    @Value("${kafka.service.url}")
    private String kafkaServiceUrl;
    @Value(("${router.service.url.prefix}"))
    private String routerServiceUrlPrefix;
    @Resource(name = "RegistryServiceImpl")
    private RegistryService registryService;

    private static final Logger LOGGER = LoggerFactory.getLogger(AspectManager.class);

    public AspectManager() {
        System.out.println("init AspectManager");
    }

    @Around("execution(* at.spc.aspect.AspectRestTemplate.*(..))")
    public void replaceRestUrl(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args[0] instanceof String) {
            String str = (String) args[0];
            if (str.contains(routerServiceUrlPrefix)) {
                System.out.println("Aspect to replace the ROUTER-SERVICE url");
                String resolvedService = registryService.resolveService(routerServiceUrlPrefix);
                System.out.println("Resolve ROUTER-SERVICE to " + resolvedService);
                args[0] = str.replace(routerServiceUrlPrefix, resolvedService);
            }
        }
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Around("execution(* at.spc.aspect.AspectProperties.put(..))")
    public void replaceKafkaUrl(ProceedingJoinPoint proceedingJoinPoint) {
        Object[] args = proceedingJoinPoint.getArgs();
        if (args[0] instanceof String) {
            if (StreamsConfig.BOOTSTRAP_SERVERS_CONFIG.equals(args[0])) {
                LOGGER.info("Aspect to replace the Kafka url");
                if (kafkaServiceUrl.equals(args[1])) {
                    String resolvedService = registryService.resolveService(kafkaServiceUrl);
                    args[1] = resolvedService;
                }
            }
        }
        try {
            proceedingJoinPoint.proceed(args);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
