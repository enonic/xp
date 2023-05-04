package com.enonic.xp.impl.server.rest;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.WebappJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;

@Path("/content/projects")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class WebappResource
    implements JaxRsComponent
{
    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    @Activate
    public WebappResource( @Reference final ApplicationService applicationService, @Reference final ResourceService resourceService )
    {
        this.applicationService = applicationService;
        this.resourceService = resourceService;
    }

    @GET
    @Path("list")
    public List<WebappJson> list()
    {
        return applicationService.getInstalledApplications()
            .stream()
            .map( Application::getKey )
            .map( key -> ResourceKey.from( key, "/webapp/webapp.js" ) )
            .map( resourceService::getResource )
            .filter( Resource::exists )
            .map( Resource::getKey )
            .map( ResourceKey::getApplicationKey )
            .map( WebappJson::from )
            .collect( Collectors.toList() );
    }
}
