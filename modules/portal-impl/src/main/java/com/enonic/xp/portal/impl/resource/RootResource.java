package com.enonic.xp.portal.impl.resource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.portal.impl.resource.render.PageResource;

@Path("/")
public final class RootResource
    extends BaseSubResource
{
    @Path("{mode}/{workspace}")
    public PageResource rootPage( @PathParam("mode") final String mode, @PathParam("workspace") final String workspace )
    {
        this.contentPath = ContentPath.from( "/" );
        this.mode = RenderMode.from( mode, RenderMode.LIVE );
        this.workspace = Workspace.from( workspace );

        ContextAccessor.current().getLocalScope().setAttribute( Workspace.from( workspace ) );
        return initResource( new PageResource() );
    }
}
