package com.enonic.xp.web.impl.dispatch.status;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

@Component(immediate = true, service = StatusReporter.class)
public final class ServletStatusReporter
    extends ResourceStatusReporter
{
    private ServletPipeline pipeline;

    public ServletStatusReporter()
    {
        super( "http.servlet" );
    }

    @Reference
    public void setPipeline( final ServletPipeline pipeline )
    {
        this.pipeline = pipeline;
    }

    @Override
    Iterable<? extends ResourceDefinition> getDefinitions()
    {
        return this.pipeline;
    }
}
