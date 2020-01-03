package com.enonic.xp.web.impl.dispatch.pipeline;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.web.dispatch.DispatchConstants;

@Component(immediate = true)
public class ServletPipelineActivator
{
    private final ComponentFactory<ServletPipeline> factory;

    @Activate
    public ServletPipelineActivator( @Reference(target = "(component.factory=servlet)") final ComponentFactory<ServletPipeline> factory )
    {
        this.factory = factory;
    }

    @Activate
    public void activate()
    {
        DispatchConstants.CONNECTORS.forEach(
            connector -> factory.newInstance( Dictionaries.of( DispatchConstants.CONNECTOR_PROPERTY, connector ) ) );
    }
}
