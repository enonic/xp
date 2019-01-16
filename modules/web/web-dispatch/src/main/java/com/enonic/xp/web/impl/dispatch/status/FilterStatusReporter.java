package com.enonic.xp.web.impl.dispatch.status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;

@Component(immediate = true, service = StatusReporter.class)
public final class FilterStatusReporter
    extends ResourceStatusReporter
{
    private FilterPipeline filterPipeline;

    public FilterStatusReporter()
    {
        super( "http.filter" );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addFilterPipeline( final FilterPipeline pipeline )
    {
        this.filterPipeline = pipeline;
    }

    public void removeFilterPipeline( final FilterPipeline filterPipeline )
    {
        if ( this.filterPipeline != null && this.filterPipeline.equals( filterPipeline ) )
        {
            this.filterPipeline = null;
        }
    }

    @Override
    Iterable<? extends ResourceDefinition> getDefinitions()
    {
        return this.filterPipeline;
    }
}
