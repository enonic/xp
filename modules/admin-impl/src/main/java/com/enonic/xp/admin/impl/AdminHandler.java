package com.enonic.xp.admin.impl;

import javax.servlet.http.HttpServletRequest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;

import com.enonic.xp.admin.AdminResource;
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

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return !req.getRequestURI().startsWith( "/osgi" ) && super.canHandle( req );
    }

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addResource( final AdminResource resource )
    {
        addSingleton( resource );
    }

    public void removeResource( final AdminResource resource )
    {
        removeSingleton( resource );
    }
}
