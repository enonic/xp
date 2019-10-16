package com.enonic.xp.admin.impl.rest.resource.project;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.project.json.DeleteProjectParamsJson;
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectJson;
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectParamsJson;
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectsJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.RoleKeys;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "project")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_ID, RoleKeys.CONTENT_MANAGER_ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public final class ProjectResource
    implements JaxRsComponent
{
    private ProjectService projectService;

    @POST
    @Path("create")
    public ProjectJson create( final ProjectParamsJson params )
    {
        final Project project = projectService.create( params.getCreateParams() );
        return new ProjectJson( project );
    }

    @POST
    @Path("modify")
    public ProjectJson modify( final ProjectParamsJson params )
    {
        final Project modifiedProject = this.projectService.modify( params.getModifyParams() );
        return new ProjectJson( modifiedProject );
    }

    @POST
    @Path("delete")
    public Boolean delete( final DeleteProjectParamsJson params )
    {
        return this.projectService.delete( params.getName() );
    }

    @GET
    @Path("list")
    public ProjectsJson list()
    {
        return new ProjectsJson( this.projectService.list() );
    }

    @GET
    @Path("get")
    public ProjectJson get( final @QueryParam("name") String projectName )
    {
        final Project project = this.projectService.get( ProjectName.from( projectName ) );
        return new ProjectJson( project );
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }
}
