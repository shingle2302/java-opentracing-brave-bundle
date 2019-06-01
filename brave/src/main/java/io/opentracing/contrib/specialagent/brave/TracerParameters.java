package io.opentracing.contrib.specialagent.brave;

import io.opentracing.contrib.specialagent.common.Configuration;


import java.util.Properties;
import java.util.logging.Logger;

public final class TracerParameters {

    private TracerParameters() {
    }

    final static String BRAVE_PREFIX = "BRAVE_";

    private final static Logger logger = Logger.getLogger(TracerParameters.class.getName());

    public static void loadParameters() {
        Properties props = Configuration.loadConfigurationFile();
        loadParametersIntoSystemProperties(props);
    }

    static void loadParametersIntoSystemProperties(Properties props) {
        for (String propName : props.stringPropertyNames()) {
            // Only load the parameter if it is not *already* defined as a System property.
            if (!propName.startsWith(BRAVE_PREFIX) || System.getProperty(propName) != null)
                continue;

            String propValue = props.getProperty(propName);
            System.setProperty(propName, propValue);
            logger.info("Set System property " + propName + "=" + propValue + " from Tracer configuration file");
        }
    }
}
