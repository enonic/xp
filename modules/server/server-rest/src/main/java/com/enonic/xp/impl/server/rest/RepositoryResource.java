package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.export.ExportService;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.RepositoriesJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.task.ExportRunnableTask;
import com.enonic.xp.impl.server.rest.task.ImportRunnableTask;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

@Path("/repo")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class RepositoryResource
    implements JaxRsComponent
{
    private ExportService exportService;

    private RepositoryService repositoryService;

    private NodeRepositoryService nodeRepositoryService;

    private TaskService taskService;

    @POST
    @Path("export")
    public TaskResultJson exportNodes( final ExportNodesRequestJson params )
    {
        final ExportRunnableTask task = ExportRunnableTask.create()
            .repositoryId( params.getSourceRepoPath().getRepositoryId() )
            .branch( params.getSourceRepoPath().getBranch() )
            .nodePath( params.getSourceRepoPath().getNodePath() )
            .exportName( params.getExportName() )
            .includeVersions( params.isIncludeVersions() )
            .exportWithIds( params.isExportWithIds() )
            .dryRun( params.isDryRun() )
            .exportService( exportService )
            .build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).description( "Export " + params.getExportName() ).build() );

        return new TaskResultJson( taskId );
    }

    @POST
    @Path("import")
    public TaskResultJson importNodes( final ImportNodesRequestJson params )
    {
        final ImportRunnableTask task = ImportRunnableTask.create()
            .repositoryId( params.getTargetRepoPath().getRepositoryId() )
            .branch( params.getTargetRepoPath().getBranch() )
            .nodePath( params.getTargetRepoPath().getNodePath() )
            .exportName( params.getExportName() )
            .importWithIds( params.isImportWithIds() )
            .importWithPermissions( params.isImportWithPermissions() )
            .xslSource( params.getXslSource() )
            .xslParams( params.getXslParams() )
            .nodeRepositoryService( nodeRepositoryService )
            .exportService( exportService )
            .repositoryService( repositoryService )
            .build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( task ).description( "Import " + params.getExportName() ).build() );

        return new TaskResultJson( taskId );
    }

    @GET
    @Path("list")
    public RepositoriesJson listRepositories()
    {
        Repositories repos = this.repositoryService.list();
        return RepositoriesJson.create( repos );
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setExportService( final ExportService exportService )
    {
        this.exportService = exportService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setNodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
    {
        this.nodeRepositoryService = nodeRepositoryService;
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
