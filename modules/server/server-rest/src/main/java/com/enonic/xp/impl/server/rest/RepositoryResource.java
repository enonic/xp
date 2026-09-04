package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.RepositoriesJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.task.SystemTasks;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.SubmitTaskParams;
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

    private TaskService taskService;

    @POST
    @Path("export")
    public TaskResultJson exportNodes( final ExportNodesRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "repository", params.getSourceRepoPath().getRepositoryId().toString() );
        data.addString( "branch", params.getSourceRepoPath().getBranch().getValue() );
        data.addString( "nodePath", params.getSourceRepoPath().getNodePath().toString() );
        data.addString( "exportName", params.getExportName() );
        if ( params.getBatchSize() != null )
        {
            data.addLong( "batchSize", params.getBatchSize().longValue() );
        }
        return new TaskResultJson( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.EXPORT ).data( data ).build() ) );
    }

    @POST
    @Path("import")
    public TaskResultJson importNodes( final ImportNodesRequestJson params )
    {
        final PropertyTree data = new PropertyTree();
        data.addString( "exportName", params.getExportName() );
        data.addString( "repository", params.getTargetRepoPath().getRepositoryId().toString() );
        data.addString( "branch", params.getTargetRepoPath().getBranch().getValue() );
        data.addString( "nodePath", params.getTargetRepoPath().getNodePath().toString() );
        data.addBoolean( "importWithIds", params.isImportWithIds() );
        data.addBoolean( "importWithPermissions", params.isImportWithPermissions() );
        return new TaskResultJson( taskService.submitTask( SubmitTaskParams.create().descriptorKey( SystemTasks.IMPORT ).data( data ).build() ) );
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
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
