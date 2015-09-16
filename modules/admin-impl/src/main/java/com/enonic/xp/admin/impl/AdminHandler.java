package com.enonic.xp.admin.impl;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.admin.JaxRsResource;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.jaxrs.JaxRsHandler;

@Component(immediate = true, service = WebHandler.class)
public final class AdminHandler
    extends JaxRsHandler
{
    public AdminHandler()
    {
        setOrder( MAX_ORDER - 20 );
        setPath( "/" );
        addSingleton( new AdminJaxRsFeature() );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addResource( final JaxRsResource resource )
    {
        addSingleton( resource );
    }

    public void removeResource( final JaxRsResource resource )
    {
        removeSingleton( resource );
    }
}
