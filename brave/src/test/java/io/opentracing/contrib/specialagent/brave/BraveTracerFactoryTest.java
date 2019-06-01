package io.opentracing.contrib.specialagent.brave;

import brave.opentracing.BraveTracer;
import io.opentracing.Tracer;
import io.opentracing.contrib.specialagent.common.Configuration;
import io.opentracing.contrib.specialagent.common.Utils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertTrue;

public class BraveTracerFactoryTest {
    final static String SERVICE_NAME_KEY = "BRAVE_SERVICE_NAME";

    Tracer tracer;

    @Before
    public void beforeTest() {
        Properties systemProps = System.getProperties();
        for (String propName : systemProps.stringPropertyNames()) {
            if (propName.startsWith(TracerParameters.BRAVE_PREFIX))
                System.clearProperty(propName);
        }
    }

    @After
    public void afterTest() {
        if (tracer != null) {
//      tracer
            tracer = null;
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getTracer_noServiceName() {
        new BraveTracerFactory().getTracer();
    }

    @Test
    public void getTracer_serviceNameViaProperty() {
        System.setProperty(SERVICE_NAME_KEY, "MyService");
        tracer = new BraveTracerFactory().getTracer();
        assertTrue(tracer instanceof BraveTracer);
    }

    @Test
    public void getTracer_serviceNameViaConfigFile() throws Exception {
        Properties props = new Properties();
        props.setProperty(BraveTracerFactoryTest.SERVICE_NAME_KEY, "MyService");

        File file = null;
        try {
            file = Utils.savePropertiesToTempFile(props);
            System.setProperty(Configuration.CONFIGURATION_FILE_KEY, file.getAbsolutePath());

            tracer = new BraveTracerFactory().getTracer();
            assertTrue(tracer instanceof BraveTracer);

        } finally {
            if (file != null)
                file.delete();
        }
    }
}
