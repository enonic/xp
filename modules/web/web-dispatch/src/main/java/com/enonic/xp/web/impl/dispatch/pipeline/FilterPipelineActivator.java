package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Collections;
import java.util.Hashtable;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.dispatch.DispatchConstants;

@Component(immediate = true)
public class FilterPipelineActivator
{
    private final ComponentFactory<FilterPipeline> factory;

    @Activate
    public FilterPipelineActivator( @Reference(target = "(component.factory=pipeline)") final ComponentFactory<FilterPipeline> factory )
    {
        this.factory = factory;
    }

    @Activate
    public void activate()
    {
        DispatchConstants.CONNECTORS.forEach( connector -> factory.newInstance(
            new Hashtable<>( Collections.singletonMap( DispatchConstants.CONNECTOR_PROPERTY, connector ) ) ) );
    }
}
