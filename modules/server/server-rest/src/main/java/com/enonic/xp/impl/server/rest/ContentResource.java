package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.impl.server.rest.model.ReprocessContentRequestJson;
import com.enonic.xp.impl.server.rest.task.ReprocessRunnableTask;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.TaskResultJson;
import com.enonic.xp.task.TaskService;

@Path("/content")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ContentResource
    implements JaxRsComponent
{
    private ContentService contentService;

    private TaskService taskService;

    @POST
    @Path("reprocess")
    public TaskResultJson reprocess( final ReprocessContentRequestJson request )
    {
        return ReprocessRunnableTask.create().
            description( "reprocess" ).
            contentService( contentService ).
            taskService( taskService ).
            params( request ).
            build().
            createTaskResult();
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
}
