package com.enonic.xp.server.impl.status;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.net.MediaType;

import com.enonic.xp.status.StatusContext;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public final class MetricsReporter
    implements StatusReporter
{
    private final ObjectMapper mapper;

    public MetricsReporter()
    {
        //final MetricsModule module = new MetricsModule( TimeUnit.SECONDS, TimeUnit.SECONDS, false, MetricFilter.ALL );
        this.mapper = new ObjectMapper();//.registerModule( module );
    }

    @Override
    public String getName()
    {
        return "metrics";
    }

    @Override
    public MediaType getMediaType()
    {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report( final StatusContext context )
        throws IOException
    {
        this.doReport( context.getOutputStream(), context.getParameter( "filter" ).orElse( "" ) );
    }

    @Override
    public void report( final OutputStream outputStream )
        throws IOException
    {
        this.doReport( outputStream, "" );
    }

    private void doReport( final OutputStream outputStream, final String filterParam )
        throws IOException
    {
        final Map<String, Object> map = toMap( filterParam );
        this.mapper.writerWithDefaultPrettyPrinter().writeValue( outputStream, map );
    }

    private Map<String, Object> toMap( final String filter )
    {
        //final MetricFilter metricFilter = ( name, metric ) -> name.getKey().contains( filter );
        //final MetricRegistry registry = OldMetrics.registry();

        final Map<String, Object> map = new HashMap<>();
        //map.put( "gauges", registry.getGauges( metricFilter ) );
        //map.put( "counters", registry.getCounters( metricFilter ) );
        //map.put( "histograms", registry.getHistograms( metricFilter ) );
        //map.put( "meters", registry.getMeters( metricFilter ) );
        //map.put( "timers", registry.getTimers( metricFilter ) );
        return map;
    }
}
