package com.enonic.xp.web.impl.dispatch;

import java.util.Collections;
import java.util.Hashtable;

import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.web.dispatch.DispatchConstants;

@Component(immediate = true)
public class DispatchServletActivator
{
    private ComponentFactory factory;

    @Activate
    public void activate()
    {
        DispatchConstants.CONNECTORS.forEach( connector -> factory.newInstance(
            new Hashtable<String, String>( Collections.singletonMap( DispatchConstants.CONNECTOR_PROPERTY, connector ) ) ) );
    }

    @Reference(target = "(component.factory=dispatchServlet)")
    public void setFactory( final ComponentFactory factory )
    {
        this.factory = factory;
    }
}