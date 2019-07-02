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

import io.opentracing.Tracer;
import io.opentracing.contrib.specialagent.common.Configuration;
import io.opentracing.contrib.tracerresolver.TracerFactory;

import javax.annotation.Priority;

/**
 * @author zhangbin
 * @date 2019/06/01
 * @description Higher priority than the original factory (which we wrap)
 */
@Priority(1)
public class BraveTracerFactory implements TracerFactory {

    public BraveTracerFactory() {
        TracerParameters.loadParameters();

    }

    @Override
    public Tracer getTracer() {
        return Configuration.fromEnv().getTracer();
    }

}
