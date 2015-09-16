package com.enonic.xp.admin.impl.rest.resource.repo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.JaxRsResource;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.snapshot.DeleteSnapshotParams;
import com.enonic.xp.snapshot.DeleteSnapshotsResult;
import com.enonic.xp.snapshot.RestoreParams;
import com.enonic.xp.snapshot.RestoreResult;
import com.enonic.xp.snapshot.SnapshotParams;
import com.enonic.xp.snapshot.SnapshotResult;
import com.enonic.xp.snapshot.SnapshotResults;

@Path(ResourceConstants.REST_ROOT + "repo")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public class SnapshotResource
    implements JaxRsResource
{
    private NodeService nodeService;

    private static String createSnapshotName( final RepositoryId repositoryId )
    {
        return ( repositoryId + getDateTimeFormatter().format( Instant.now() ) ).toLowerCase();
    }

    private static DateTimeFormatter getDateTimeFormatter()
    {
        return DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH-mm-ss.SSS'z'" ).withZone( ZoneId.of( "UTC" ) );
    }

    @POST
    @Path("snapshot")
    public SnapshotResultJson snapshot( final SnapshotRequestJson params )
        throws Exception
    {
        final SnapshotResult result = this.nodeService.snapshot( SnapshotParams.create().
            snapshotName( createSnapshotName( params.getRepositoryId() ) ).
            setIncludeIndexedData( !params.isSkipIndexedData() ).
            repositoryId( params.getRepositoryId() ).
            build() );

        return SnapshotResultJson.from( result );
    }

    @POST
    @Path("restore")
    public RestoreResultJson restore( final RestoreRequestJson params )
        throws Exception
    {
        final RestoreResult result = this.nodeService.restore( RestoreParams.create().
            snapshotName( params.getSnapshotName() ).
            setIncludeIndexedData( !params.isSkipIndexedData() ).
            repositoryId( params.getRepositoryId() ).
            build() );

        return RestoreResultJson.from( result );
    }

    @POST
    @Path("delete")
    public DeleteSnapshotsResultJson delete( final DeleteSnapshotRequestJson params )
        throws Exception
    {
        final DeleteSnapshotsResult result = this.nodeService.deleteSnapshot( DeleteSnapshotParams.create().
            before( params.getBefore() ).
            addAll( params.getSnapshotNames() ).
            build() );

        return DeleteSnapshotsResultJson.from( result );
    }

    @GET
    @Path("list")
    public SnapshotResultsJson list()
        throws Exception
    {
        final SnapshotResults snapshotResults = this.nodeService.listSnapshots();

        return SnapshotResultsJson.from( snapshotResults );
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }

}
