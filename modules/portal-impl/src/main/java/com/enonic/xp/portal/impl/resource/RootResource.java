package com.enonic.xp.portal.impl.resource;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
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
        final PortalRequest parentPortalRequest = findParentPortalRequest();

        this.mode = findRenderMode( parentPortalRequest );
        this.baseUri = findBaseUri( parentPortalRequest );
        this.contentPath = ContentPath.from( "/" );
        this.branch = Branch.from( branch );

        ContextAccessor.current().getLocalScope().setAttribute( Branch.from( branch ) );
        return initResource( new PageResource() );
    }

    private PortalRequest findParentPortalRequest()
    {
        return PortalRequestAccessor.get( this.rawRequest );
    }

    private RenderMode findRenderMode( final PortalRequest portalRequest )
    {
        final RenderMode mode = portalRequest != null ? portalRequest.getMode() : null;
        return mode != null ? mode : RenderMode.LIVE;
    }

    private String findBaseUri( final PortalRequest portalRequest )
    {
        final String uri = portalRequest != null ? portalRequest.getBaseUri() : null;
        return uri != null ? uri : "/portal";
    }
}
