package com.enonic.xp.impl.server.rest.api;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.impl.server.rest.model.DeleteSnapshotsResultJson;
import com.enonic.xp.impl.server.rest.model.RestoreRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotResultsJson;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.portal.universalapi.UniversalApiHandler;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.util.DateTimeHelper;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

/**
 * {@code server:snapshot} - repository snapshots: list, create, restore and prune.
 */
@Component(service = UniversalApiHandler.class, property = {"key=server:snapshot", "title=Snapshot API", "mount=management",
    "allowedPrincipals=role:system.admin"})
public class SnapshotApiHandler
    extends ManagementApiHandler
{
    static final String KEY = "server:snapshot";

    private static final DescriptorKey SNAPSHOT_TASK = DescriptorKey.from( "com.enonic.xp.app.system:snapshot" );

    private static final DescriptorKey RESTORE_TASK = DescriptorKey.from( "com.enonic.xp.app.system:restore" );

    private final SnapshotService snapshotService;

    private final TaskService taskService;

    @Activate
    public SnapshotApiHandler( @Reference final SnapshotService snapshotService, @Reference final TaskService taskService )
    {
        super( KEY );
        this.snapshotService = snapshotService;
        this.taskService = taskService;

        route( HttpMethod.GET, "/", "list", this::list );
        route( HttpMethod.POST, "/", "create", this::create );
        route( HttpMethod.POST, "/{name}/restore", "restore", this::restore );
        route( HttpMethod.POST, "/restore", "restore", this::restoreLatest );
        route( HttpMethod.DELETE, "/{name}", "prune", this::prune );
        route( HttpMethod.DELETE, "/", "prune", this::pruneBefore );
    }

    private WebResponse list( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( SnapshotResultsJson.from( snapshotService.list() ) );
    }

    private WebResponse create( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        final SnapshotRequestJson snapshot =
            body == null || body.isBlank() ? new SnapshotRequestJson( null, null ) : MAPPER.readValue( body, SnapshotRequestJson.class );

        final PropertyTree data = new PropertyTree();
        if ( snapshot.getSnapshotName() != null )
        {
            data.addString( "snapshotName", snapshot.getSnapshotName() );
        }
        if ( snapshot.getRepositoryId() != null )
        {
            data.addString( "repositoryId", snapshot.getRepositoryId().toString() );
        }

        final TaskId taskId = taskService.submitTask( SubmitTaskParams.create().descriptorKey( SNAPSHOT_TASK ).data( data ).build() );
        return accepted( taskId );
    }

    private WebResponse restore( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return submitRestore( params.get( "name" ), false, request );
    }

    private WebResponse restoreLatest( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final RestoreRequestJson restore = body( request, RestoreRequestJson.class );
        if ( !restore.isLatest() )
        {
            throw new IllegalArgumentException( "Name the snapshot in the path, or set [latest] to true" );
        }
        return submitRestore( null, true, request );
    }

    private WebResponse submitRestore( final String name, final boolean latest, final WebRequest request )
        throws JsonProcessingException
    {
        final String body = request.getBodyAsString();
        final RestoreRequestJson restore = body == null || body.isBlank()
            ? new RestoreRequestJson( null, false, null, latest, false )
            : MAPPER.readValue( body, RestoreRequestJson.class );

        final PropertyTree data = new PropertyTree();
        if ( name != null )
        {
            data.addString( "snapshotName", name );
        }
        if ( restore.getRepositoryId() != null )
        {
            data.addString( "repositoryId", restore.getRepositoryId().toString() );
        }
        data.addBoolean( "latest", latest );
        data.addBoolean( "force", restore.isForce() );

        final TaskId taskId = taskService.submitTask( SubmitTaskParams.create().descriptorKey( RESTORE_TASK ).data( data ).build() );
        return accepted( taskId );
    }

    private WebResponse prune( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        return json( DeleteSnapshotsResultJson.from(
            snapshotService.delete( DeleteSnapshotParams.create().addAll( List.of( params.get( "name" ) ) ).build() ) ) );
    }

    private WebResponse pruneBefore( final WebRequest request, final Map<String, String> params )
        throws JsonProcessingException
    {
        final String before = param( request, "before" );
        if ( before == null )
        {
            throw new IllegalArgumentException( "Name the snapshot in the path, or pass [before] as an ISO date-time" );
        }
        return json( DeleteSnapshotsResultJson.from(
            snapshotService.delete( DeleteSnapshotParams.create().before( DateTimeHelper.parseIsoDateTime( before ) ).build() ) ) );
    }
}
