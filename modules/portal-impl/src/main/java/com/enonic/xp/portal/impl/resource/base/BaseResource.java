package com.enonic.xp.portal.impl.resource.base;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.services.PortalServices;

public abstract class BaseResource
{
    protected RenderMode mode;

    protected Branch branch;

    protected ContentPath contentPath;

    protected String baseUri;

    protected PortalServices services;

    public final void setServices( final PortalServices services )
    {
        this.services = services;
    }

    protected final WebApplicationException notFound( final String message, final Object... args )
    {
        return new WebApplicationException( String.format( message, args ), Response.Status.NOT_FOUND );
    }
}
