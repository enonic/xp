package com.enonic.xp.admin.impl.rest.resource.project;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.project.json.DeleteProjectParamsJson;
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectJson;
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectReadAccessJson;
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectsJson;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectReadAccess;
import com.enonic.xp.project.ProjectReadAccessType;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static com.enonic.xp.project.ProjectConstants.PROJECT_ACCESS_LEVEL_AUTHOR_PROPERTY;
import static com.enonic.xp.project.ProjectConstants.PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY;
import static com.enonic.xp.project.ProjectConstants.PROJECT_ACCESS_LEVEL_EDITOR_PROPERTY;
import static com.enonic.xp.project.ProjectConstants.PROJECT_ACCESS_LEVEL_OWNER_PROPERTY;

@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "project")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_ID, RoleKeys.ADMIN_LOGIN_ID})
@Component(immediate = true, property = "group=admin")
public final class ProjectResource
    implements JaxRsComponent
{
    private ProjectService projectService;

    @POST
    @Path("create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ProjectJson create( final MultipartForm form )
        throws Exception
    {
        final Project project = projectService.create( createParams( form ) );
        return new ProjectJson( project );
    }

    @POST
    @Path("modify")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ProjectJson modify( final MultipartForm form )
        throws Exception
    {
        final Project modifiedProject = this.projectService.modify( ModifyProjectParams.create( createParams( form ) ).build() );
        return new ProjectJson( modifiedProject );
    }

    @POST
    @Path("delete")
    public boolean delete( final DeleteProjectParamsJson params )
    {
        if ( ProjectConstants.PROJECT_REPO_ID_DEFAULT.equals( params.getName().toString() ) )
        {
            throw new WebApplicationException( "Default repo is not allowed to be deleted", HttpStatus.METHOD_NOT_ALLOWED.value() );
        }

        return this.projectService.delete( params.getName() );
    }

    @GET
    @RolesAllowed({RoleKeys.ADMIN_ID, RoleKeys.CONTENT_MANAGER_ADMIN_ID, RoleKeys.CONTENT_MANAGER_APP_ID})
    @Path("list")
    public ProjectsJson list()
    {
        return new ProjectsJson( this.projectService.list() );
    }

    @GET
    @RolesAllowed({RoleKeys.ADMIN_ID, RoleKeys.CONTENT_MANAGER_ADMIN_ID, RoleKeys.CONTENT_MANAGER_APP_ID})
    @Path("get")
    public ProjectJson get( final @QueryParam("name") String projectName )
    {
        final Project project = this.projectService.get( ProjectName.from( projectName ) );
        return new ProjectJson( project );
    }

    private CreateProjectParams createParams( final MultipartForm form )
        throws IOException
    {
        final ProjectPermissions projectPermissions = getPermissionsFromForm( form );
        final ProjectReadAccess readAccess = this.getReadAccessFromForm( form );

        final CreateProjectParams.Builder builder = CreateProjectParams.create().
            name( ProjectName.from( form.getAsString( "name" ) ) ).
            displayName( form.getAsString( "displayName" ) ).
            description( form.getAsString( "description" ) ).
            permissions( projectPermissions ).
            readAccess( readAccess );

        final MultipartItem icon = form.get( "icon" );

        if ( icon != null )
        {
            builder.icon( CreateAttachment.create().
                name( ProjectConstants.PROJECT_ICON_PROPERTY ).
                mimeType( icon.getContentType().toString() ).
                byteSource( icon.getBytes() ).
                build() );
        }

        return builder.build();
    }

    private ProjectPermissions getPermissionsFromForm( final MultipartForm form )
        throws IOException
    {
        final ProjectPermissions.Builder builder = new ProjectPermissions.Builder();
        final String permissionsAsString = form.getAsString( "permissions" );
        if ( permissionsAsString == null )
        {
            return builder.build();
        }

        final ObjectMapper permissionsMapper = new ObjectMapper();
        Map<String, List<String>> map = permissionsMapper.readValue( permissionsAsString, new TypeReference<Map<String, List<String>>>()
        {
        } );

        if ( map.containsKey( PROJECT_ACCESS_LEVEL_OWNER_PROPERTY ) )
        {
            map.get( PROJECT_ACCESS_LEVEL_OWNER_PROPERTY ).forEach( builder::addOwner );
        }

        if ( map.containsKey( PROJECT_ACCESS_LEVEL_EDITOR_PROPERTY ) )
        {
            map.get( PROJECT_ACCESS_LEVEL_EDITOR_PROPERTY ).forEach( builder::addEditor );
        }

        if ( map.containsKey( PROJECT_ACCESS_LEVEL_AUTHOR_PROPERTY ) )
        {
            map.get( PROJECT_ACCESS_LEVEL_AUTHOR_PROPERTY ).forEach( builder::addAuthor );
        }

        if ( map.containsKey( PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY ) )
        {
            map.get( PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY ).forEach( builder::addContributor );
        }

        return builder.build();
    }

    private ProjectReadAccess getReadAccessFromForm( final MultipartForm form )
        throws IOException
    {
        final String readAccessAsString = form.getAsString( "readAccess" );

        if ( readAccessAsString == null )
        {
            return new ProjectReadAccess( ProjectReadAccessType.PRIVATE );
        }

        final ProjectReadAccessJson readAccessJson = new ObjectMapper().readValue( readAccessAsString, ProjectReadAccessJson.class );

        return readAccessJsonToReadAccess( readAccessJson );
    }

    private ProjectReadAccess readAccessJsonToReadAccess( final ProjectReadAccessJson json )
    {
        final ProjectReadAccessType type = ProjectReadAccessType.valueOf( json.getType().toUpperCase() );

        if ( type == ProjectReadAccessType.CUSTOM )
        {
            if ( json.getPrincipals() == null || json.getPrincipals().isEmpty() )
            {
                return new ProjectReadAccess( ProjectReadAccessType.PRIVATE );
            }

            return new ProjectReadAccess( type, PrincipalKeys.from(
                json.getPrincipals().stream().map( PrincipalKey::from ).collect( Collectors.toList() ) ) );
        }

        return new ProjectReadAccess( type );
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }
}
