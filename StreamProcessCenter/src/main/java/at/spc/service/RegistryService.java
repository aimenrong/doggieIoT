package at.spc.service;

/**
 * Created by Terry LIANG on 2017/10/1.
 */
public interface RegistryService {
    String resolveRouterService(String serviceId);

    String resolveKafkaService(String serviceId);

    String resolveService(String serviceId);
}
