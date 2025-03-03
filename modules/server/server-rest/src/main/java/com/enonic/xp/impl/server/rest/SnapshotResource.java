package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.util.DateTimeHelper;

@Path("/repo/snapshot")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SnapshotResource
    implements JaxRsComponent
{
    private SnapshotService snapshotService;

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
        final SnapshotResult result = this.snapshotService.snapshot( SnapshotParams.create().
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
        final RestoreResult result = this.snapshotService.restore( RestoreParams.create()
                                                                       .snapshotName( params.getSnapshotName() )
                                                                       .setIncludeIndexedData( !params.isSkipIndexedData() )
                                                                       .repositoryId( params.getRepositoryId() )
                                                                       .latest( params.isLatest() )
                                                                       .force( params.isForce() )
                                                                       .build() );

        return RestoreResultJson.from( result );
    }

    @POST
    @Path("delete")
    public DeleteSnapshotsResultJson delete( final DeleteSnapshotRequestJson params )
        throws Exception
    {
        final DeleteSnapshotsResult result = this.snapshotService.delete( DeleteSnapshotParams.create().
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
        final SnapshotResults snapshotResults = this.snapshotService.list();

        return SnapshotResultsJson.from( snapshotResults );
    }

    @Reference
    public void setSnapshotService( final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }
}
