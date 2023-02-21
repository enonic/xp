package com.enonic.xp.impl.server.rest;


import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.dump.DumpService;
import com.enonic.xp.export.ExportService;
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
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryService;
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
    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    private DumpService dumpService;

    private TaskService taskService;

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
            .exportService( exportService )
            .nodeRepositoryService( nodeRepositoryService )
            .repositoryService( repositoryService )
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

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setNodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
    {
        this.nodeRepositoryService = nodeRepositoryService;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setDumpService( final DumpService dumpService )
    {
        this.dumpService = dumpService;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }


}
