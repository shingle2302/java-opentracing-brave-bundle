package io.opentracing.contrib.specialagent.brave;

import io.opentracing.Tracer;
import io.opentracing.contrib.specialagent.common.Configuration;
import io.opentracing.contrib.tracerresolver.TracerFactory;

import javax.annotation.Priority;

@Priority(1) // Higher priority than the original factory (which we wrap).
public class BraveTracerFactory implements TracerFactory {

    public BraveTracerFactory() {
        TracerParameters.loadParameters();

    }

    @Override
    public Tracer getTracer() {
        return Configuration.fromEnv().getTracer();
    }

}
