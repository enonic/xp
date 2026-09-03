package com.enonic.xp.impl.server.rest;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.impl.server.rest.model.WebappJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.security.RoleKeys;

@Path("/webapps")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class WebappResource
    implements JaxRsComponent
{
    private final WebappJsonFactory webappJsonFactory;

    @Activate
    public WebappResource( @Reference final ApplicationService applicationService, @Reference final ResourceService resourceService )
    {
        this.webappJsonFactory = new WebappJsonFactory( applicationService, resourceService );
    }

    @GET
    @Path("list")
    public List<WebappJson> list()
    {
        return webappJsonFactory.list();
    }
}
