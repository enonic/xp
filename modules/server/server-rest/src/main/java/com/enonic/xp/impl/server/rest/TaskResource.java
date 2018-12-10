package com.enonic.xp.impl.server.rest;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskInfoJson;
import com.enonic.xp.task.TaskService;

@Path("/api/task")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class TaskResource
    implements JaxRsComponent
{
    private TaskService taskService;

    @GET
    @Path("info")
    public TaskInfoJson info( @QueryParam("id") final String id )
    {
        final TaskId taskId = TaskId.from( id );
        final TaskInfo taskInfo = taskService.getTaskInfo( taskId );

        if ( taskInfo == null )
        {
            throw JaxRsExceptions.notFound( String.format( "Task [%s] was not found", id ) );
        }

        return new TaskInfoJson( taskInfo );
    }

    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }
}
