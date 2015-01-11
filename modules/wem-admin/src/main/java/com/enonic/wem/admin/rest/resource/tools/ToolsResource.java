package com.enonic.wem.admin.rest.resource.tools;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.core.initializer.StartupInitializer;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Path(ResourceConstants.REST_ROOT + "tools")
public final class ToolsResource
    implements AdminResource
{
    private StartupInitializer startupInitializer;

    private NodeService nodeService;

    @GET
    @Path("cleanData")
    public Response cleanData()
        throws Exception
    {
        this.startupInitializer.cleanData();
        return redirectToIndex();
    }

    @GET
    @Path("snapshot")
    public Response snapshot()
        throws Exception
    {
        this.nodeService.snapshot();
        return redirectToIndex();
    }

    @GET
    @Path("initializeData")
    public Response initializeData()
        throws Exception
    {
        this.startupInitializer.initializeData();
        return redirectToIndex();
    }

    private Response redirectToIndex()
        throws Exception
    {
        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    public void setStartupInitializer( final StartupInitializer startupInitializer )
    {
        this.startupInitializer = startupInitializer;
    }

    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}
