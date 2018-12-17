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
import com.enonic.xp.impl.server.rest.task.DumpRunnableTask;
import com.enonic.xp.impl.server.rest.task.LoadRunnableTask;
import com.enonic.xp.impl.server.rest.task.UpgradeRunnableTask;
import com.enonic.xp.impl.server.rest.task.VacuumRunnableTask;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.vacuum.VacuumService;

@Path("/api/system")
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

    private VacuumService vacuumService;

    private TaskService taskService;

    @POST
    @Path("dump")
    public TaskResultJson systemDump( final SystemDumpRequestJson request )
    {
        return DumpRunnableTask.create().
            description( "dump" ).
            taskService( taskService ).
            dumpService( dumpService ).
            params( request ).
            build().
            createTaskResult();
    }

    @POST
    @Path("load")
    public TaskResultJson load( final SystemLoadRequestJson request )
    {
        return LoadRunnableTask.create().
            description( "load" ).
            taskService( taskService ).
            dumpService( dumpService ).
            exportService( exportService ).
            nodeRepositoryService( nodeRepositoryService ).
            repositoryService( repositoryService ).
            params( request ).
            build().
            createTaskResult();
    }

    @POST
    @Path("vacuum")
    public TaskResultJson vacuum()
    {
        return VacuumRunnableTask.create().
            description( "vacuum" ).
            taskService( taskService ).
            vacuumService( vacuumService ).
            build().
            createTaskResult();
    }

    @POST
    @Path("upgrade")
    public TaskResultJson upgrade( final SystemDumpUpgradeRequestJson params )
    {
        return UpgradeRunnableTask.create().
            description( "upgrade" ).
            taskService( taskService ).
            dumpService( dumpService ).
            params( params ).
            build().
            createTaskResult();
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
    public void setVacuumService( final VacuumService vacuumService )
    {
        this.vacuumService = vacuumService;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }


}
