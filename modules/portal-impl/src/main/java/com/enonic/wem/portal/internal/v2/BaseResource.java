package com.enonic.wem.portal.internal.v2;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.workspace.Workspace;

public abstract class BaseResource
{
    @Context
    protected ResourceContext resourceContext;

    protected boolean editMode;

    protected Workspace workspace;

    protected ContentPath contentPath;

    protected final <T extends BaseResource> T newResource( final Class<T> type )
    {
        final T resource = this.resourceContext.getResource( type );
        resource.contentPath = this.contentPath;
        resource.workspace = this.workspace;
        resource.editMode = this.editMode;
        return resource;
    }
}
