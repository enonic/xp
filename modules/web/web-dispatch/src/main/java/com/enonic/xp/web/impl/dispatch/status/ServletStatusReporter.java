package com.enonic.xp.web.impl.dispatch.status;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

@Component(immediate = true, service = StatusReporter.class)
public final class ServletStatusReporter
    extends ResourceStatusReporter
{
    @Activate
    public ServletStatusReporter( @Reference final ServletPipeline servletPipeline )
    {
        super( "http.servlet", servletPipeline );
    }
}
