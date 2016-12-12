package com.enonic.xp.impl.server.rest;

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

import com.enonic.xp.impl.server.rest.model.DeleteSnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.DeleteSnapshotsResultJson;
import com.enonic.xp.impl.server.rest.model.RestoreRequestJson;
import com.enonic.xp.impl.server.rest.model.RestoreResultJson;
import com.enonic.xp.impl.server.rest.model.SnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotResultJson;
import com.enonic.xp.impl.server.rest.model.SnapshotResultsJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.util.DateTimeHelper;

@Path("/api/repo/snapshot")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SnapshotResource
    implements JaxRsComponent
{
    private NodeService nodeService;

    private static String createSnapshotName( final RepositoryId repositoryId )
    {
        return ( ( repositoryId == null ? "" : repositoryId ) + getDateTimeFormatter().format( Instant.now() ) ).toLowerCase();
    }

    private static DateTimeFormatter getDateTimeFormatter()
    {
        return DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH-mm-ss.SSS'z'" ).withZone( ZoneId.of( "UTC" ) );
    }

    @POST
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
            before( DateTimeHelper.parseIsoDateTime( params.getBefore() ) ).
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
