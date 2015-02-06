package com.enonic.xp.portal.impl.resource.base;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

public abstract class BaseSubResource
    extends BaseResource
{
    @Context
    protected ResourceContext resourceContext;

    protected final <T extends BaseResource> T initResource( final T instance )
    {
        final T resource = this.resourceContext.initResource( instance );
        resource.mode = this.mode;
        resource.contentPath = this.contentPath;
        resource.branch = this.branch;
        resource.baseUri = this.baseUri;
        resource.services = this.services;
        return resource;
    }
}
