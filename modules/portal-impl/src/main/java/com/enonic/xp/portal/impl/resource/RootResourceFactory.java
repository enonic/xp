package com.enonic.xp.portal.impl.resource;

import javax.servlet.http.HttpServletRequest;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.services.PortalServices;
import com.enonic.xp.web.jaxrs2.JaxRsResourceFactory;
import com.enonic.xp.web.servlet.ServletRequestHolder;

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
        final PortalContext parentContext = findParentContext();

        final RootResource root = new RootResource();
        root.setServices( this.services );
        root.setMode( findRenderMode( parentContext ) );
        root.setBaseUri( findBaseUri( parentContext ) );
        return root;
    }

    private PortalContext findParentContext()
    {
        final HttpServletRequest req = ServletRequestHolder.getRequest();
        return req != null ? PortalContextAccessor.get( req ) : null;
    }

    private RenderMode findRenderMode( final PortalContext context )
    {
        final RenderMode mode = context != null ? context.getMode() : null;
        return mode != null ? mode : RenderMode.LIVE;
    }

    private String findBaseUri( final PortalContext context )
    {
        final String uri = context != null ? context.getBaseUri() : null;
        return uri != null ? uri : "/portal";
    }

    public final void setServices( final PortalServices services )
    {
        this.services = services;
    }
}
