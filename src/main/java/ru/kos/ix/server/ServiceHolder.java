package ru.kos.ix.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton. The creator and keeper of services. <br/>
 * Created by Константин on 09.04.2016.
 */
public class ServiceHolder {

    private static final Logger logger = LogManager.getLogger(ServiceHolder.class);
    private static final String SERVICE_PREFIX = "service.";

    private static ServiceHolder instance;

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    /**
     * Initialization of holder: <br/>
     * 1)Creates instances of services according to service.* properties <br/>
     * Example of property: service.some=ru.kos.ix.service.SomeService <br/>
     * 2)Saves instances in ConcurrentHashMap for next usage. <br/>
     * @param properties
     * @throws IOException
     */
    public static void init(Properties properties) throws IOException {
        instance = new ServiceHolder(properties);
    }

    /**
     * Returns instance. {@link #init(Properties)} method must be called before.
     * @return
     */
    public static ServiceHolder getInstance() {
        if (instance == null) throw new IllegalStateException("ServiceHolder must be initialized");
        return instance;
    }

    private ServiceHolder(Properties properties) throws IOException {
        serviceMap = new ConcurrentHashMap<>();
        properties.forEach((key, value) -> addInstanceToMap((String) key, (String) value));
    }

    private void addInstanceToMap(String key, String className) {
        if (key.startsWith(SERVICE_PREFIX)) {
            String serviceName = extractServiceName(key);
            try {
                Class<?> serviceClass = Class.forName(className);
                Object instance = serviceClass.newInstance();
                serviceMap.put(serviceName, instance);
                logger.info(serviceClass + " instance was created");
            } catch (ClassNotFoundException e) {
                logger.error(className + " is not found. Ignore");
            } catch (Exception e) {
                logger.error("Can't create instance of " + className + ". Ignore");
            }
        }
    }

    /**
     * Returns service by name. Null if service is not found.
     * @param key
     * @return
     */
    public Object getServiceByName(String key) {
        return serviceMap.get(key);
    }

    private String extractServiceName(String key) {
        int index = key.indexOf(SERVICE_PREFIX) + SERVICE_PREFIX.length();
        return key.substring(index);
    }

}
