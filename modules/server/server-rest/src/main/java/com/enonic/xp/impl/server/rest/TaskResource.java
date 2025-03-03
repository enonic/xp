package com.enonic.xp.impl.server.rest;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.enonic.xp.impl.server.rest.model.TaskInfoJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

@Path("/task")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class TaskResource
    implements JaxRsComponent
{
    private TaskService taskService;

    @GET
    @Path("/{taskId}")
    public TaskInfoJson getTask( @PathParam("taskId") final String taskId )
    {
        final TaskInfo taskInfo = taskService.getTaskInfo( TaskId.from( taskId ) );

        if ( taskInfo == null )
        {
            throw new WebApplicationException( String.format( "Task [%s] was not found", taskId ), Response.Status.NOT_FOUND );
        }

        return new TaskInfoJson( taskInfo );
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
