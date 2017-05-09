package com.enonic.xp.web.impl.dispatch.status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;

@Component(immediate = true, service = StatusReporter.class)
public final class FilterStatusReporter
    extends ResourceStatusReporter
{
    private FilterPipeline pipeline;

    public FilterStatusReporter()
    {
        super( "http.filter" );
    }

    @Reference
    public void setPipeline( final FilterPipeline pipeline )
    {
        this.pipeline = pipeline;
    }

    @Override
    Iterable<? extends ResourceDefinition> getDefinitions()
    {
        return this.pipeline;
    }
}
