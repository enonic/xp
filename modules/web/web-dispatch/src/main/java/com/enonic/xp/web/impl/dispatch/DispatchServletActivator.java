package com.enonic.xp.web.impl.dispatch;

import java.util.Collections;
import java.util.Hashtable;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;

@Component(immediate = true)
public class DispatchServletActivator
{
    private final ComponentFactory<DispatchServlet> factory;

    @Activate
    public DispatchServletActivator(
        @Reference(target = "(component.factory=dispatchServlet)") final ComponentFactory<DispatchServlet> factory )
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
