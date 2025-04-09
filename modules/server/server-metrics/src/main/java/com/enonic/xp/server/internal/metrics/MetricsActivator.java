package com.enonic.xp.server.internal.metrics;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadDeadlockMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;


@Component(immediate = true)
public class MetricsActivator
{
    @Activate
    public MetricsActivator()
    {
        new ClassLoaderMetrics().bindTo( Metrics.globalRegistry );
        new JvmMemoryMetrics().bindTo( Metrics.globalRegistry );
        new JvmGcMetrics().bindTo( Metrics.globalRegistry );
        new ProcessorMetrics().bindTo( Metrics.globalRegistry );
        new JvmThreadMetrics().bindTo( Metrics.globalRegistry );
        new JvmThreadDeadlockMetrics().bindTo( Metrics.globalRegistry );
    }

    @Deactivate
    public void deactivate()
    {
        Metrics.globalRegistry.getMeters().forEach( Metrics.globalRegistry::remove );
    }

}
