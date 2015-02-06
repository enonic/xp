package com.enonic.xp.portal.impl.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.enonic.wem.api.branch.Branch;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.context.ContextAccessor;
import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.resource.base.BaseSubResource;
import com.enonic.xp.portal.impl.resource.render.PageResource;

@Path("/")
public final class RootResource
    extends BaseSubResource
{
    @Context
    protected HttpServletRequest rawRequest;

    @Path("{branch}")
    public PageResource rootPage( @PathParam("branch") final String branch )
    {
        final PortalContext parentContext = findParentContext();

        this.mode = findRenderMode( parentContext );
        this.baseUri = findBaseUri( parentContext );
        this.contentPath = ContentPath.from( "/" );
        this.branch = Branch.from( branch );

        ContextAccessor.current().getLocalScope().setAttribute( Branch.from( branch ) );
        return initResource( new PageResource() );
    }

    private PortalContext findParentContext()
    {
        return PortalContextAccessor.get( this.rawRequest );
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
}
