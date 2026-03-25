package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.impl.server.rest.model.DeleteSnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.DeleteSnapshotsResultJson;
import com.enonic.xp.impl.server.rest.model.RestoreRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotRequestJson;
import com.enonic.xp.impl.server.rest.model.SnapshotResultsJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.util.DateTimeHelper;

@Path("/repo/snapshot")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SnapshotResource
    implements JaxRsComponent
{
    private final SnapshotService snapshotService;

    private final TaskService taskService;

    private static final DescriptorKey SNAPSHOT_TASK_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.system:snapshot" );

    private static final DescriptorKey RESTORE_TASK_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.system:restore" );


    @Activate
    public SnapshotResource( @Reference final SnapshotService snapshotService, @Reference final TaskService taskService )
    {
        this.snapshotService = snapshotService;
        this.taskService = taskService;
    }

    @POST
    public TaskResultJson snapshot( final SnapshotRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        if ( params.getSnapshotName() != null )
        {
            data.addString( "snapshotName", params.getSnapshotName() );
        }
        if ( params.getRepositoryId() != null )
        {
            data.addString( "repositoryId", params.getRepositoryId().toString() );
        }

        final TaskId taskId =
            taskService.submitTask( SubmitTaskParams.create().descriptorKey( SNAPSHOT_TASK_DESCRIPTOR_KEY ).data( data ).build() );

        return new TaskResultJson( taskId );
    }

    @POST
    @Path("restore")
    public TaskResultJson restore( final RestoreRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        if ( params.getSnapshotName() != null )
        {
            data.addString( "snapshotName", params.getSnapshotName() );
        }
        if ( params.getRepositoryId() != null )
        {
            data.addString( "repositoryId", params.getRepositoryId().toString() );
        }
        data.addBoolean( "latest", params.isLatest() );
        data.addBoolean( "force", params.isForce() );

        final TaskId taskId =
            taskService.submitTask( SubmitTaskParams.create().descriptorKey( RESTORE_TASK_DESCRIPTOR_KEY ).data( data ).build() );

        return new TaskResultJson( taskId );
    }

    @POST
    @Path("delete")
    public DeleteSnapshotsResultJson delete( final DeleteSnapshotRequestJson params )
    {
        final DeleteSnapshotsResult result = this.snapshotService.delete( DeleteSnapshotParams.create()
                                                                              .before(
                                                                                  DateTimeHelper.parseIsoDateTime( params.getBefore() ) )
                                                                              .addAll( params.getSnapshotNames() )
                                                                              .build() );

        return DeleteSnapshotsResultJson.from( result );
    }

    @GET
    @Path("list")
    public SnapshotResultsJson list()
    {
        final SnapshotResults snapshotResults = this.snapshotService.list();

        return SnapshotResultsJson.from( snapshotResults );
    }
}
