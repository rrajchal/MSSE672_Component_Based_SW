package com.topcard.service.factory;

import com.topcard.exceptions.TopCardException;
import com.topcard.marker.TopCardMarker;
import com.topcard.xml.sax.SaxParserUtil;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * ServiceFactory loads service mappings from services.xml using SAX and instantiates services dynamically.
 * Author: Rajesh Rajchal
 * Date: 08/10/2025
 */
public class ServiceFactory implements TopCardMarker {

    private static final Map<Class<?>, Object> serviceInstances = new HashMap<>();
    private static final Map<Class<?>, Class<?>> serviceMappings = new HashMap<>();

    static {
        try {
            InputStream inputStream = ServiceFactory.class.getClassLoader().getResourceAsStream("services.xml");
            if (inputStream == null) {
                throw new TopCardException("services.xml not found in classpath.");
            }

            Map<String, String> mappings = new SaxParserUtil().parseServiceMappings(inputStream);

            for (Map.Entry<String, String> entry : mappings.entrySet()) {
                Class<?> interfaceClass = Class.forName(entry.getKey());
                Class<?> implClass = Class.forName(entry.getValue());
                serviceMappings.put(interfaceClass, implClass);
            }
        } catch (Exception e) {
            throw new TopCardException("Failed to load service mappings from XML", e);
        }
    }

    /**
     * Creates or retrieves an instance of the specified service interface or class.
     *
     * @param requestedClass the interface or concrete class of the service
     * @param args optional constructor arguments
     * @param <T> the type of the service
     * @return an instance of the service
     */
    @SuppressWarnings("unchecked")
    public static <T> T createService(Class<T> requestedClass, Object... args) {
        try {
            if (serviceInstances.containsKey(requestedClass)) {
                return (T) serviceInstances.get(requestedClass);
            }

            // Find implementation from mapping or fallback to requested class itself
            Class<?> implClass = serviceMappings.getOrDefault(requestedClass, requestedClass);

            Object instance;

            if (args == null || args.length == 0) {
                instance = implClass.getDeclaredConstructor().newInstance();
            } else {
                Constructor<?> matched = getConstructor(args, implClass);
                instance = matched.newInstance(args);
            }

            serviceInstances.put(requestedClass, instance);
            return (T) instance;

        } catch (Exception e) {
            throw new TopCardException("Failed to instantiate service: " + requestedClass.getName(), e);
        }
    }

    private static Constructor<?> getConstructor(Object[] args, Class<?> implClass) {
        Constructor<?>[] constructors = implClass.getDeclaredConstructors();
        Constructor<?> matched = null;

        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length == args.length) {
                boolean match = true;
                for (int i = 0; i < paramTypes.length; i++) {
                    if (!paramTypes[i].isAssignableFrom(args[i].getClass())) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matched = constructor;
                    break;
                }
            }
        }

        if (matched == null) {
            throw new TopCardException("No matching constructor found for: " + implClass.getName());
        }
        return matched;
    }

    /**
     * Clears all cached service instances. Intended for test isolation.
     */
    public static void reset() {
        serviceInstances.clear();
    }
}
