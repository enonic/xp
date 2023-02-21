package com.enonic.xp.impl.server.rest;


import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.impl.server.rest.model.CleanUpAuditLogRequestJson;
import com.enonic.xp.impl.server.rest.model.TaskResultJson;
import com.enonic.xp.impl.server.rest.task.CleanUpAuditLogCommand;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.task.TaskService;

@Path("/auditlog")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class AuditLogResource
    implements JaxRsComponent
{
    private TaskService taskService;

    @POST
    @Path("cleanup")
    public TaskResultJson cleanup( final CleanUpAuditLogRequestJson params )
    {
        return new TaskResultJson(
            CleanUpAuditLogCommand.create().taskService( taskService ).ageThreshold( params.getAgeThreshold() ).build().execute() );
    }

    @SuppressWarnings("WeakerAccess")
    @Reference
    public void setTaskService( final TaskService taskService )
    {
        this.taskService = taskService;
    }


}
