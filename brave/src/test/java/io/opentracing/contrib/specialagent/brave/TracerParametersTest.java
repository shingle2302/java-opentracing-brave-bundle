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

import java.io.File;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import io.opentracing.contrib.specialagent.common.Configuration;
import io.opentracing.contrib.specialagent.common.Utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TracerParametersTest {
    @Before
    public void beforeTest() {
        Properties systemProps = System.getProperties();
        for (String propName : systemProps.stringPropertyNames()) {
            if (propName.startsWith(TracerParameters.BRAVE_PREFIX))
                System.clearProperty(propName);
        }
    }

    @Test
    public void loadParameters_fromConfigurationFile() throws Exception {
        Properties props = new Properties();
        props.setProperty(BraveTracerFactoryTest.SERVICE_NAME_KEY, "MyService");
        props.setProperty("NOT_RELATED", "SomeValue");

        File file = null;
        try {
            file = Utils.savePropertiesToTempFile(props);
            System.out.println(file.getAbsolutePath());
            System.setProperty(Configuration.CONFIGURATION_FILE_KEY, file.getAbsolutePath());

            TracerParameters.loadParameters();
            assertEquals("MyService", System.getProperty(BraveTracerFactoryTest.SERVICE_NAME_KEY));
            assertNull(System.getProperty("NOT_RELATED"));

        } finally {
            if (file != null)
                file.delete();
        }
    }

    @Test
    public void loadParameters_fromConfigurationFilePreventOverride() throws Exception {
        Properties props = new Properties();
        props.setProperty(BraveTracerFactoryTest.SERVICE_NAME_KEY, "MyService");

        File file = null;
        try {
            file = Utils.savePropertiesToTempFile(props);
            System.setProperty(Configuration.CONFIGURATION_FILE_KEY, file.getAbsolutePath());

            // The values from the file will only be loaded if there's no System property defined already.
            System.setProperty(BraveTracerFactoryTest.SERVICE_NAME_KEY, "MyCustomService");
            TracerParameters.loadParameters();
            assertEquals("MyCustomService", System.getProperty(BraveTracerFactoryTest.SERVICE_NAME_KEY));

        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }
}
