package com.enonic.xp.impl.server.rest;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.enonic.xp.impl.server.rest.model.DeleteSnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.DeleteSnapshotsResultJson;
import com.enonic.xp.impl.server.rest.model.RestoreRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotResultsJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.task.RestoreRunnableTask;
import com.enonic.xp.impl.server.rest.task.SnapshotRunnableTask;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.util.DateTimeHelper;

@Path("/repo/snapshot")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SnapshotResource
    implements JaxRsComponent
{
    private static final String SNAPSHOT_TASK_NAME = "snapshot";

    private static final String RESTORE_TASK_NAME = "restore";

    private SnapshotService snapshotService;

    private TaskService taskService;

    private static String createSnapshotName( final RepositoryId repositoryId )
    {
        return ( ( repositoryId == null ? "" : repositoryId ) + getDateTimeFormatter().format( Instant.now() ) ).toLowerCase();
    }

    private static DateTimeFormatter getDateTimeFormatter()
    {
        return DateTimeFormatter.ofPattern( "yyyy-MM-dd'T'HH-mm-ss.SSS'z'" ).withZone( ZoneOffset.UTC );
    }

    private void checkForRunningSnapshotOrRestoreTask()
    {
        final boolean hasRunningTask = taskService.getRunningTasks().stream()
            .anyMatch( taskInfo -> SNAPSHOT_TASK_NAME.equals( taskInfo.getName() ) || RESTORE_TASK_NAME.equals( taskInfo.getName() ) );

        if ( hasRunningTask )
        {
            throw new WebApplicationException( "A snapshot or restore operation is already in progress", Response.Status.CONFLICT );
        }
    }

    @POST
    public TaskResultJson snapshot( final SnapshotRequestJson params )
    {
        checkForRunningSnapshotOrRestoreTask();

        final SnapshotParams snapshotParams = SnapshotParams.create()
            .snapshotName( createSnapshotName( params.getRepositoryId() ) )
            .repositoryId( params.getRepositoryId() )
            .build();

        final SnapshotRunnableTask task = SnapshotRunnableTask.create()
            .snapshotParams( snapshotParams )
            .snapshotService( snapshotService )
            .build();

        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create()
                .runnableTask( task )
                .name( SNAPSHOT_TASK_NAME )
                .description( "Snapshot " + snapshotParams.getSnapshotName() )
                .build() );

        return new TaskResultJson( taskId );
    }

    @POST
    @Path("restore")
    public TaskResultJson restore( final RestoreRequestJson params )
    {
        checkForRunningSnapshotOrRestoreTask();

        final RestoreParams restoreParams = RestoreParams.create()
            .snapshotName( params.getSnapshotName() )
            .repositoryId( params.getRepositoryId() )
            .latest( params.isLatest() )
            .force( params.isForce() )
            .build();

        final RestoreRunnableTask task = RestoreRunnableTask.create()
            .restoreParams( restoreParams )
            .snapshotService( snapshotService )
            .build();

        final String description = params.isLatest() ? "Restore latest snapshot" : "Restore snapshot " + params.getSnapshotName();

        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create()
                .runnableTask( task )
                .name( RESTORE_TASK_NAME )
                .description( description )
                .build() );

        return new TaskResultJson( taskId );
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

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
