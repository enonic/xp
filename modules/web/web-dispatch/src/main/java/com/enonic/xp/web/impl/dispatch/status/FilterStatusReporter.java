package com.enonic.xp.web.impl.dispatch.status;


import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;

@Component(immediate = true, service = StatusReporter.class)
public final class FilterStatusReporter
    extends ResourceStatusReporter
{
    @Activate
    public FilterStatusReporter( @Reference final FilterPipeline filterPipeline )
    {
        super( "http.filter", filterPipeline );
    }
}
