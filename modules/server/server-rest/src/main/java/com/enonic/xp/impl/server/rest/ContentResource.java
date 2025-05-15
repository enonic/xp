package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.task.ProjectsSyncTask;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

@Path("/content")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ContentResource
    implements JaxRsComponent
{
    private static final Logger LOG = LoggerFactory.getLogger( ContentResource.class );

    private ContentService contentService;

    private TaskService taskService;

    private ProjectService projectService;

    private SyncContentService syncContentService;

    @POST
    @Path("syncAll")
    public TaskResultJson syncAll()
    {
        RunnableTask runnable = ProjectsSyncTask.create().projectService( projectService ).syncContentService( syncContentService ).build();
        final TaskId taskId = taskService.submitLocalTask(
            SubmitLocalTaskParams.create().runnableTask( runnable ).name( "sync-all-projects" ).description( "Sync all projects" ).build() );

        return new TaskResultJson( taskId );
    }

    @SuppressWarnings("UnusedDeclaration")
    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }

    @Reference
    public void setSyncContentService( final SyncContentService syncContentService )
    {
        this.syncContentService = syncContentService;
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }

}
