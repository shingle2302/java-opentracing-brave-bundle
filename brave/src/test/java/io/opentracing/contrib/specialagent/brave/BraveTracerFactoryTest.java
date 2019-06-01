/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

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
