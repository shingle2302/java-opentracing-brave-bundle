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

import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.sampler.BoundarySampler;
import brave.sampler.CountingSampler;
import brave.sampler.RateLimitingSampler;
import brave.sampler.Sampler;
import io.opentracing.Tracer;
import zipkin2.Span;
import zipkin2.codec.SpanBytesEncoder;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.Sender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static zipkin2.reporter.AsyncReporter.builder;

/**
 * @author zhangbin
 */
public final class Configuration {

    private final static Logger logger = Logger.getLogger(Configuration.class.getName());


    private String serviceName;
    private Configuration.SamplerConfiguration samplerConfig;
    private Configuration.ReporterConfiguration reporterConfig;
    private Configuration.SenderConfiguration senderConfiguration;
    private boolean useTraceId128Bit;
    private BraveTracer tracer;

    private Configuration(String serviceName) {
        this.serviceName = serviceName;
    }

    private final static String DEFAULT_CONFIGURATION_FILE_PATH = "tracer.properties";
    public final static String CONFIGURATION_FILE_KEY = "tracer.configurationFile";

    public static Properties loadConfigurationFile() {
        String path = System.getProperty(CONFIGURATION_FILE_KEY);
        if (path == null)
            path = DEFAULT_CONFIGURATION_FILE_PATH;

        Properties props = new Properties();

        File file = new File(path);
        if (!file.isFile())
            return props;

        try (FileInputStream stream = new FileInputStream(file)) {
            props.load(stream);
        } catch (IOException e) {
            logger.log(Level.WARNING, "Failed to read the Tracer configuration file '" + path + "'");
            logger.log(Level.WARNING, e.toString());
        }

        logger.info("Successfully loaded Tracer configuration file " + path);
        return props;
    }


    public static Configuration fromEnv() {
        return fromEnv(getProperty(BraveConstant.BRAVE_SERVICE_NAME));
    }

    private static Configuration fromEnv(String serviceName) {
        return (new Configuration(serviceName)).withTraceId128Bit(getPropertyAsBool(BraveConstant.BRAVE_TRACE_ID_128BIT)).withReporter(Configuration.ReporterConfiguration.fromEnv()).withSampler(Configuration.SamplerConfiguration.fromEnv());
    }


    public synchronized Tracer getTracer() {
        if (this.tracer != null) {
            return this.tracer;
        } else {
            this.tracer = BraveTracer.create(this.getTracerBuilder().build());
            logger.log(Level.INFO, "Initialized tracer={}", this.tracer);
            return this.tracer;
        }
    }

    private Tracing.Builder createTracerBuilder(String serviceName) {
        return Tracing.newBuilder().localServiceName(serviceName);
    }

    public synchronized void closeTracer() {
        tracer.scopeManager().active().close();
    }


    private Tracing.Builder getTracerBuilder() {
        if (this.reporterConfig == null) {
            this.reporterConfig = new Configuration.ReporterConfiguration();
        }

        if (this.samplerConfig == null) {
            this.samplerConfig = new Configuration.SamplerConfiguration();
        }

        if (this.senderConfiguration == null) {
            this.senderConfiguration = new Configuration.SenderConfiguration();
        }


        Reporter<Span> reporter = this.reporterConfig.getReporter();
        Sampler sampler = this.samplerConfig.createSampler();
        Tracing.Builder builder = this.createTracerBuilder(this.serviceName).sampler(sampler).spanReporter(reporter);
        if (this.useTraceId128Bit) {
            builder.traceId128Bit(true);
        }
        return builder;
    }


    private static String stringOrDefault(String value, String defaultValue) {
        return value != null && value.length() > 0 ? value : defaultValue;
    }

    private static Number numberOrDefault(Number value, Number defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static String getProperty(String name) {
        return System.getProperty(name, System.getenv(name));
    }

    private static String getProperty(String name, String defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            return value;
        }
        return defaultValue;
    }

    private static Integer getPropertyAsInt(String name) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException var3) {
                logger.log(Level.SEVERE, "Failed to parse integer for property '" + name + "' with value '" + value + "'", var3);
            }
        }
        return null;
    }

    private static Integer getPropertyAsInt(String name, Integer defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException var3) {
                logger.log(Level.WARNING, "Failed to parse integer for property '" + name + "' with value '" + value + "'", var3);
            }
        }
        return defaultValue;
    }

    private static Long getPropertyAsLong(String name) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException var3) {
                logger.log(Level.SEVERE, "Failed to parse long for property '" + name + "' with value '" + value + "'", var3);
            }
        }
        return null;
    }


    private static Long getPropertyAsLong(String name, Long defaultValue) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException var3) {
                logger.log(Level.WARNING, "Failed to parse long for property '" + name + "' with value '" + value + "'", var3);
            }
        }
        return defaultValue;
    }

    private static Number getPropertyAsNum(String name) {
        String value = getProperty(name);
        if (value != null) {
            try {
                return NumberFormat.getInstance().parse(value);
            } catch (ParseException var3) {
                logger.log(Level.SEVERE, "Failed to parse number for property '" + name + "' with value '" + value + "'", var3);
            }
        }
        return null;
    }

    private static boolean getPropertyAsBool(String name) {
        return Boolean.valueOf(getProperty(name));
    }


    private Configuration withReporter(Configuration.ReporterConfiguration reporterConfig) {
        this.reporterConfig = reporterConfig;
        return this;
    }

    private Configuration withSampler(Configuration.SamplerConfiguration samplerConfig) {
        this.samplerConfig = samplerConfig;
        return this;
    }


    private Configuration withTraceId128Bit(boolean useTraceId128Bit) {
        this.useTraceId128Bit = useTraceId128Bit;
        return this;
    }


    public static class SenderConfiguration {
        private Sender sender;
        private String type;
        private String address;

        SenderConfiguration() {
        }

        Configuration.SenderConfiguration withType(String type) {
            this.type = type;
            return this;
        }

        Configuration.SenderConfiguration withAddress(String address) {
            this.address = address;
            return this;
        }

        @SuppressWarnings("UnusedAssignment")
        Sender getSender() {
            String senderType = Configuration.stringOrDefault(this.type, "URLConnection");
            String senderAddress = Configuration.stringOrDefault(this.address, "http://127.0.0.1:9411/api/v2/spans");
            Class clazz;
            if (this.sender == null) {
                try {
                    switch (senderType) {
                        case "OkHttp":
                            clazz = Class.forName("zipkin2.reporter.okhttp3.OkHttpSender");
                        case "Kafka08":
                            clazz = Class.forName("zipkin2.reporter.kafka08.KafkaSender");
                        case "Kafka11":
                            clazz = Class.forName("zipkin2.reporter.kafka11.KafkaSender");
                        case "RabbitMQ":
                            clazz = Class.forName("zipkin2.reporter.amqp.RabbitMQSender");
                        default:
                            clazz = Class.forName("zipkin2.reporter.urlconnection.URLConnectionSender");
                    }
                    Method method = clazz.getMethod("create", String.class);
                    sender = (Sender) method.invoke(null, senderAddress);
                } catch (ClassNotFoundException e) {
                    logger.log(Level.SEVERE, e.toString());
                } catch (NoSuchMethodException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    logger.log(Level.SEVERE, e.getMessage());
                }
            }
            return this.sender;
        }

        static Configuration.SenderConfiguration fromEnv() {
            String type = Configuration.getProperty(BraveConstant.BRAVE_SENDER_TYPE);
            String address = Configuration.getProperty(BraveConstant.BRAVE_SENDER_ADDRESS);
            return (new Configuration.SenderConfiguration()).withType(type).withAddress(address);
        }

    }

    public static class ReporterConfiguration {
        private Integer messageMaxBytes;
        private Long messageTimeoutNanos;
        private Long closeTimeoutNanos;
        private Integer queuedMaxSpans;
        private Integer queuedMaxBytes;
        private String spanBytesEncoder;
        private Configuration.SenderConfiguration senderConfiguration = new Configuration.SenderConfiguration();

        ReporterConfiguration() {
        }

        static Configuration.ReporterConfiguration fromEnv() {
            SenderConfiguration senderConfiguration = SenderConfiguration.fromEnv();
            return (new Configuration.ReporterConfiguration())
                    .withMessageMaxBytes(getPropertyAsInt(BraveConstant.BRAVE_MESSAGE_MAX_BYTES, 5242880))
                    .withCloseTimeoutNanos(getPropertyAsLong(BraveConstant.BRAVE_CLOSE_TIMEOUT_NANOS, 1L))
                    .withMessageTimeoutNanos(getPropertyAsLong(BraveConstant.BRAVE_MESSAGE_TIMEOUT_NANOS, 1L))
                    .withQueuedMaxBytes(getPropertyAsInt(BraveConstant.BRAVE_QUEUE_MAX_BYTES, onePercentOfMemory()))
                    .withQueuedMaxSpans(getPropertyAsInt(BraveConstant.BRAVE_QUEUE_MAX_SPANS, 10000))
                    .withSpanBytesEncoder(getProperty(BraveConstant.BRAVE_SPAN_BYTES_ENCODER, "JSON_V1"))
                    .withSender(senderConfiguration);
        }

        Configuration.ReporterConfiguration withMessageMaxBytes(Integer messageMaxBytes) {
            this.messageMaxBytes = messageMaxBytes;
            return this;
        }

        Configuration.ReporterConfiguration withMessageTimeoutNanos(Long messageTimeoutNanos) {
            this.messageTimeoutNanos = messageTimeoutNanos;
            return this;
        }

        Configuration.ReporterConfiguration withCloseTimeoutNanos(Long closeTimeoutNanos) {
            this.closeTimeoutNanos = closeTimeoutNanos;
            return this;
        }

        Configuration.ReporterConfiguration withQueuedMaxSpans(Integer queuedMaxSpans) {
            this.queuedMaxSpans = queuedMaxSpans;
            return this;
        }


        Configuration.ReporterConfiguration withQueuedMaxBytes(Integer queuedMaxBytes) {
            this.queuedMaxBytes = queuedMaxBytes;
            return this;
        }

        Configuration.ReporterConfiguration withSpanBytesEncoder(String spanBytesEncoder) {
            this.spanBytesEncoder = spanBytesEncoder;
            return this;
        }

        Configuration.ReporterConfiguration withSender(Configuration.SenderConfiguration senderConfiguration) {
            this.senderConfiguration = senderConfiguration;
            return this;
        }

        static int onePercentOfMemory() {
            long result = (long) ((double) Runtime.getRuntime().totalMemory() * 0.01D);
            return (int) Math.max(Math.min(2147483647L, result), -2147483648L);
        }

        private Reporter<Span> getReporter() {
            Reporter<Span> reporter = builder(this.senderConfiguration.getSender()).messageMaxBytes(messageMaxBytes)
                    .messageTimeout(messageTimeoutNanos, TimeUnit.NANOSECONDS)
                    .closeTimeout(closeTimeoutNanos, TimeUnit.NANOSECONDS)
                    .queuedMaxSpans(queuedMaxSpans)
                    .queuedMaxBytes(queuedMaxBytes)
                    .build(SpanBytesEncoder.valueOf(spanBytesEncoder));
            return reporter;
        }
    }


    public static class SamplerConfiguration {
        private String type;
        private Number param;

        SamplerConfiguration() {
        }

        static Configuration.SamplerConfiguration fromEnv() {
            return (new Configuration.SamplerConfiguration()).withType(Configuration.getProperty(BraveConstant.BRAVE_SAMPLER_TYPE)).withParam(Configuration.getPropertyAsNum(BraveConstant.BRAVE_SAMPLER_PARAM));
        }

        Sampler createSampler() {
            String samplerType = Configuration.stringOrDefault(this.getType(), "boundary");
            Number samplerParam = Configuration.numberOrDefault(this.getParam(), 0.001f);
            switch (samplerType) {
                case "boundary":
                    return BoundarySampler.create(samplerParam.floatValue());
                case "counting":
                    return CountingSampler.create(samplerParam.floatValue());
                case "rateLimiting":
                    return RateLimitingSampler.create(samplerParam.floatValue());
                default:
                    throw new IllegalStateException(String.format("Invalid sampling strategy %s", samplerType));
            }
        }

        String getType() {
            return this.type;
        }

        Number getParam() {
            return this.param;
        }

        Configuration.SamplerConfiguration withType(String type) {
            this.type = type;
            return this;
        }

        Configuration.SamplerConfiguration withParam(Number param) {
            this.param = param;
            return this;
        }
    }

}
