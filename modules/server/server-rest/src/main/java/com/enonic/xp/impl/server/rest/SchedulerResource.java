package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.impl.server.rest.model.ScheduledJobJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.RoleKeys;

@Path("/scheduler")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class SchedulerResource
    implements JaxRsComponent
{
    private final SchedulerService schedulerService;

    @Activate
    public SchedulerResource( @Reference final SchedulerService schedulerService )
    {
        this.schedulerService = schedulerService;
    }

    @GET
    @Path("list")
    public List<ScheduledJobJson> list()
    {
        return schedulerService.list().
            stream().
            map( ScheduledJobJson::new ).
            collect( Collectors.toList() );
    }
}
