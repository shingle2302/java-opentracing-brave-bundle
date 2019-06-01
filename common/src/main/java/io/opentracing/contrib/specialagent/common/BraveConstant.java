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

package io.opentracing.contrib.specialagent.common;

/**
 * @author zhangbin
 */
public interface BraveConstant {

    /* tracing*/
    String BRAVE_SERVICE_NAME = "BRAVE_SERVICE_NAME";
    String BRAVE_TRACE_ID_128BIT = "BRAVE_TRACE_ID_128BIT";

    /* sender*/
    String BRAVE_SENDER_TYPE = "BRAVE_SENDER_TYPE";
    String BRAVE_SENDER_ADDRESS = "BRAVE_SENDER_ADDRESS";

    /* reporter*/
    String BRAVE_MESSAGE_MAX_BYTES = "BRAVE_MESSAGE_MAX_BYTES";
    String BRAVE_CLOSE_TIMEOUT_NANOS = "BRAVE_CLOSE_TIMEOUT_NANOS";
    String BRAVE_MESSAGE_TIMEOUT_NANOS = "BRAVE_MESSAGE_TIMEOUT_NANOS";
    String BRAVE_QUEUE_MAX_BYTES = "BRAVE_QUEUE_MAX_BYTES";
    String BRAVE_QUEUE_MAX_SPANS = "BRAVE_QUEUE_MAX_SPANS";
    String BRAVE_SPAN_BYTES_ENCODER = "BRAVE_SPAN_BYTES_ENCODER";


    /* sampler*/
    String BRAVE_SAMPLER_TYPE = "BRAVE_SAMPLER_TYPE";
    String BRAVE_SAMPLER_PARAM = "BRAVE_SAMPLER_PARAM";


}
