package ru.kos.ix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Константин on 09.04.2016.
 */
public class ServiceHolder {

    private static final Logger logger = LogManager.getLogger(ServiceHolder.class);

    private static ServiceHolder instance;

    private Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    public static void init() throws IOException {
        instance = new ServiceHolder();
    }

    public static ServiceHolder getInstance() {
        if (instance == null) throw new IllegalStateException("ServiceHolder must be initialized");
        return instance;
    }

    private ServiceHolder() throws IOException {
        InputStream inputStream  = ServiceHolder.class.getClassLoader().getResourceAsStream("server.properties");
        Properties properties = new Properties();
        properties.load(inputStream);
        logger.info("Properties files is loaded. Properties.size=" + properties.size());
        serviceMap = new ConcurrentHashMap<>();
        properties.forEach((key, value) -> addInstanceToMap((String)key, (String) value));
    }

    private void addInstanceToMap(String key, String className) {
        try {
            Class<?> serviceClass = Class.forName(className);
            Object instance = serviceClass.newInstance();
            serviceMap.put(key, instance);
        } catch (ClassNotFoundException e) {
            logger.error(className + " is not found. Ignore");
        } catch (Exception e) {
            logger.error("Can't create instance of " + className + ". Ignore");
        }
    }

    public Object getServiceByName(String key) {
        return serviceMap.get(key);
    }
}
