package com.enonic.xp.portal.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.impl.resource.RootResourceFactory;
import com.enonic.xp.portal.impl.services.PortalServices;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.jaxrs.JaxRsHandler;

@Component(immediate = true, service = WebHandler.class)
public final class PortalHandler
    extends JaxRsHandler
{
    private final RootResourceFactory root;

    public PortalHandler()
    {
        setOrder( MAX_ORDER - 30 );
        setPath( "/portal/" );

        this.root = new RootResourceFactory();
        addSingleton( new PortalJaxRsFeature() );
        addSingleton( this.root );
    }

    @Reference
    public void setServices( final PortalServices services )
    {
        this.root.setServices( services );
    }
}
