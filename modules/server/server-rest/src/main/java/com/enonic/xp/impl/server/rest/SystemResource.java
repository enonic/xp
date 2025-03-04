package com.enonic.xp.impl.server.rest;


import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.impl.server.rest.model.SystemDumpRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemDumpUpgradeRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.model.VacuumRequestJson;
import com.enonic.xp.impl.server.rest.task.DumpRunnableTask;
import com.enonic.xp.impl.server.rest.task.LoadRunnableTask;
import com.enonic.xp.impl.server.rest.task.UpgradeRunnableTask;
import com.enonic.xp.impl.server.rest.task.VacuumCommand;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

@Path("/system")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SystemResource
    implements JaxRsComponent
{
    private final DumpService dumpService;

    private final TaskService taskService;

    @Activate
    public SystemResource( @Reference final DumpService dumpService, @Reference final TaskService taskService )
    {
        this.dumpService = dumpService;
        this.taskService = taskService;
    }

    @POST
    @Path("dump")
    public TaskResultJson dump( final SystemDumpRequestJson params )
    {
        TaskId taskId = DumpRunnableTask.create()
            .name( params.getName() )
            .includeVersions( params.isIncludeVersions() )
            .archive( params.isArchive() )
            .maxAge( params.getMaxAge() )
            .maxVersions( params.getMaxVersions() )
            .taskService( taskService )
            .dumpService( dumpService )
            .build()
            .execute();
        return new TaskResultJson( taskId );
    }

    @POST
    @Path("load")
    public TaskResultJson load( final SystemLoadRequestJson params )
    {
        final LoadRunnableTask task = LoadRunnableTask.create()
            .name( params.getName() )
            .upgrade( params.isUpgrade() )
            .archive( params.isArchive() )
            .taskService( taskService )
            .dumpService( dumpService )
            .build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).name( "load" ).description( "Load " + params.getName() ).build() );
        return new TaskResultJson( taskId );
    }


    @POST
    @Path("vacuum")
    public TaskResultJson vacuum( final VacuumRequestJson params )
    {
        final TaskId taskId = VacuumCommand.create()
            .ageThreshold( params.getAgeThreshold() )
            .tasks( params.getTasks() )
            .taskService( taskService )
            .build()
            .execute();
        return new TaskResultJson( taskId );
    }

    @POST
    @Path("upgrade")
    public TaskResultJson upgrade( final SystemDumpUpgradeRequestJson params )
    {
        final UpgradeRunnableTask task =
            UpgradeRunnableTask.create().dumpService( dumpService ).name( params.getName() ).build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).description( "Upgrade dump " + params.getName() ).build() );
        return new TaskResultJson( taskId );
    }
}
