package com.enonic.xp.admin.impl.rest.resource.project;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import com.enonic.xp.admin.impl.rest.resource.project.json.ProjectsJson;
import com.enonic.xp.admin.impl.rest.resource.project.layer.json.ContentLayerJson;
import com.enonic.xp.admin.impl.rest.resource.project.layer.json.DeleteLayerParamsJson;
import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerConstants;
import com.enonic.xp.project.layer.ContentLayerKey;
import com.enonic.xp.project.layer.CreateLayerParams;
import com.enonic.xp.project.layer.ModifyLayerParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static com.enonic.xp.project.ProjectConstants.PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY;
import static com.enonic.xp.project.ProjectConstants.PROJECT_ACCESS_LEVEL_EXPERT_PROPERTY;
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
        final Project project = projectService.create( createProjectParams( form ) );
        return new ProjectJson( project );
    }

    @POST
    @Path("createLayer")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentLayerJson createLayer( final MultipartForm form )
        throws Exception
    {
        final ContentLayer layer = projectService.createLayer( createLayerParams( form ) );
        return new ContentLayerJson( layer );
    }

    @POST
    @Path("modify")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ProjectJson modify( final MultipartForm form )
        throws Exception
    {
        final Project modifiedProject = this.projectService.modify( ModifyProjectParams.create( createProjectParams( form ) ).build() );
        return new ProjectJson( modifiedProject );
    }

    @POST
    @Path("modifyLayer")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public ContentLayerJson modifyLayer( final MultipartForm form )
        throws Exception
    {
        final ContentLayer modifiedLayer = this.projectService.modifyLayer( ModifyLayerParams.create( createLayerParams( form ) ).build() );
        return new ContentLayerJson( modifiedLayer );
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

    @POST
    @Path("deleteLayer")
    public boolean deleteLayer( final DeleteLayerParamsJson params )
    {
        return this.projectService.deleteLayer( params.getKey() );
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

    private CreateProjectParams createProjectParams( final MultipartForm form )
        throws IOException
    {
        final ProjectPermissions projectPermissions = getPermissionsFromForm( form );

        final CreateProjectParams.Builder builder = CreateProjectParams.create().
            name( ProjectName.from( form.getAsString( "name" ) ) ).
            displayName( form.getAsString( "displayName" ) ).
            description( form.getAsString( "description" ) ).
            permissions( projectPermissions );

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

    private CreateLayerParams createLayerParams( final MultipartForm form )
        throws IOException
    {
        final CreateLayerParams.Builder builder = CreateLayerParams.create().
            key( ContentLayerKey.from( form.getAsString( "key" ) ) ).
            displayName( form.getAsString( "displayName" ) ).
            description( form.getAsString( "description" ) ).
            locale( Locale.forLanguageTag( form.getAsString( "locale" ) ) );

        if ( form.getAsString( "parentKeys" ) != null && form.getAsString( "parentKeys" ).length() > 0 )
        {
            final List<String> parentKeys = Arrays.asList( form.getAsString( "parentKeys" ).split( "," ) );
            parentKeys.forEach( builder::addParentKey );
        }

        final MultipartItem icon = form.get( "icon" );

        if ( icon != null )
        {
            builder.icon( CreateAttachment.create().
                name( ContentLayerConstants.ICON_PROPERTY ).
                mimeType( icon.getContentType().toString() ).
                byteSource( icon.getBytes() ).
                build() );
        }

        return builder.build();
    }

    private ProjectPermissions getPermissionsFromForm( final MultipartForm form )
        throws IOException
    {
        final ProjectPermissions.Builder builder = ProjectPermissions.create();
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

        if ( map.containsKey( PROJECT_ACCESS_LEVEL_EXPERT_PROPERTY ) )
        {
            map.get( PROJECT_ACCESS_LEVEL_EXPERT_PROPERTY ).forEach( builder::addExpert );
        }

        if ( map.containsKey( PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY ) )
        {
            map.get( PROJECT_ACCESS_LEVEL_CONTRIBUTOR_PROPERTY ).forEach( builder::addContributor );
        }

        return builder.build();
    }

    @Reference
    public void setProjectService( final ProjectService projectService )
    {
        this.projectService = projectService;
    }
}
