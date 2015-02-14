package com.enonic.xp.portal.impl.resource;

import com.enonic.xp.portal.impl.services.PortalServices;
import com.enonic.xp.web.jaxrs.JaxRsResourceFactory;

public final class RootResourceFactory
    implements JaxRsResourceFactory<RootResource>
{
    private PortalServices services;

    @Override
    public Class<RootResource> getType()
    {
        return RootResource.class;
    }

    @Override
    public RootResource newResource()
    {
        final RootResource root = new RootResource();
        root.setServices( this.services );
        return root;
    }

    public final void setServices( final PortalServices services )
    {
        this.services = services;
    }
}
