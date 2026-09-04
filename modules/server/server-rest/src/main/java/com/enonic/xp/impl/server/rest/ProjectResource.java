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

import com.enonic.xp.content.ContentService;
import com.enonic.xp.impl.server.rest.model.ProjectJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.RoleKeys;

@Path("/content/projects")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true, property = "group=api")
public final class ProjectResource
    implements JaxRsComponent
{
    private final ProjectJsonFactory projectJsonFactory;

    @Activate
    public ProjectResource( @Reference final ProjectService projectService, @Reference final ContentService contentService )
    {
        this.projectJsonFactory = new ProjectJsonFactory( projectService, contentService );
    }

    @GET
    @Path("list")
    public List<ProjectJson> list()
    {
        return projectJsonFactory.list();
    }
}
