package com.enonic.wem.portal.internal.v2;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.api.content.ContentPath;

public final class PageResource
    extends RenderResource
{
    @Path("_")
    public UnderscoreResource underscore()
    {
        return newResource( UnderscoreResource.class );
    }

    @Path("{name}")
    public PageResource subPage( @PathParam("name") final String name )
    {
        final PageResource resource = newResource( PageResource.class );
        resource.contentPath = ContentPath.from( this.contentPath, name );
        return resource;
    }
}
