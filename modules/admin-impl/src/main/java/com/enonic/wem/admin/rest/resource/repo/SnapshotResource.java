package com.enonic.wem.admin.rest.resource.repo;

import java.net.URI;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.admin.AdminResource;
import com.enonic.wem.admin.rest.resource.ResourceConstants;
import com.enonic.wem.api.node.NodeService;
import com.enonic.xp.web.servlet.ServletRequestUrlHelper;

@Path(ResourceConstants.REST_ROOT + "repo")
@Component(immediate = true)
public class SnapshotResource
    implements AdminResource
{
    private NodeService nodeService;

    @POST
    @Path("snapshot")
    public Response snapshot( final SnapshotRequestJson snapshotRequestJson )
        throws Exception
    {
        this.nodeService.snapshot( snapshotRequestJson.getSnapshotName() );
        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    @POST
    @Path("restore")
    public Response restore( final RestoreRequestJson restoreRequestJson )
        throws Exception
    {
        this.nodeService.restore( restoreRequestJson.getSnapshotName() );
        final String uri = ServletRequestUrlHelper.createUriWithHost( "/" );
        return Response.temporaryRedirect( new URI( uri ) ).build();
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

}
