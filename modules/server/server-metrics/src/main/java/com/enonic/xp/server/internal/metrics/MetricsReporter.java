package com.enonic.xp.server.internal.metrics;

import java.io.IOException;
import java.io.OutputStream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.google.common.net.MediaType;

import io.micrometer.core.instrument.Metrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import io.prometheus.metrics.expositionformats.OpenMetricsTextFormatWriter;

import com.enonic.xp.status.StatusContext;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class MetricsReporter
    implements StatusReporter
{
    private final PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry( PrometheusConfig.DEFAULT );

    public MetricsReporter()
    {
        Metrics.addRegistry( prometheusRegistry );
    }

    @Deactivate
    public void deactivate()
    {
        Metrics.removeRegistry( prometheusRegistry );
    }

    @Override
    public String getName()
    {
        return "metrics";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.parse( OpenMetricsTextFormatWriter.CONTENT_TYPE );
    }

    @Override
    public void report( final StatusContext context )
        throws IOException
    {
        report( context.getOutputStream() );
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        final PrometheusMeterRegistry prometheusMeterRegistry = Metrics.globalRegistry.getRegistries()
            .stream()
            .filter( registry -> registry instanceof PrometheusMeterRegistry )
            .map( r -> (PrometheusMeterRegistry) r )
            .findFirst()
            .orElseThrow();
        prometheusMeterRegistry.scrape( outputStream, OpenMetricsTextFormatWriter.CONTENT_TYPE );
    }
}
