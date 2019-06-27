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

import io.opentracing.contrib.specialagent.common.Configuration;


import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author zhangbin
 */
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
