package com.enonic.xp.admin.impl.rest.resource.project;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectReadAccess;
import com.enonic.xp.project.ProjectReadAccessType;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectResourceTest
    extends AdminResourceTestSupport
{
    private ProjectService projectService;

    @Override
    protected ProjectResource getResourceInstance()
    {
        projectService = Mockito.mock( ProjectService.class );

        final ProjectResource resource = new ProjectResource();
        resource.setProjectService( projectService );

        return resource;
    }

    @Test
    public void get_project()
        throws Exception
    {
        final Project project = createProject( "project1", "project name", "project description", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.get( project.getName() ) ).thenReturn( project );

        final String jsonString = request().
            path( "project/get" ).
            queryParam( "name", project.getName().toString() ).
            get().
            getAsString();

        assertJson( "get_project.json", jsonString );
    }

    @Test
    public void list_projects()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build(), ProjectPermissions.create().addOwner( RoleKeys.AUTHENTICATED ).build() );

        final Project project2 =
            createProject( "project2", "project2", null, null, ProjectPermissions.create().addEditor( RoleKeys.AUTHENTICATED ).build() );
        final Project project3 =
            createProject( "project3", null, null, null, ProjectPermissions.create().addContributor( RoleKeys.AUTHENTICATED ).build() );
        final Project project4 =
            createProject( "project4", "project4", null, null, ProjectPermissions.create().addAuthor( RoleKeys.AUTHENTICATED ).build() );

        Mockito.when( projectService.list() ).thenReturn(
            Projects.create().addAll( List.of( project1, project2, project3, project4 ) ).build() );

        String jsonString = request().path( "project/list" ).get().getAsString();

        assertJson( "list_projects.json", jsonString );
    }

    @Test
    public void create_project_exception()
        throws Exception
    {
        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenThrow( e );

        createForm();

        assertThrows( IllegalArgumentException.class, () -> {
            request().path( "project/create" ).
                entity( readFromFile( "create_project_params.json" ), MediaType.MULTIPART_FORM_DATA_TYPE ).
                post().getAsString();
        } );
    }

    @Test
    public void create_project_success()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenReturn( project1 );

        createForm();

        String jsonString = request().path( "project/create" ).
            multipart( "icon", "logo.png", readFromFile( "create_project_params.json" ).getBytes(), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void modify_project_success()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.modify( Mockito.isA( ModifyProjectParams.class ) ) ).thenReturn( project1 );

        createForm();

        String jsonString = request().path( "project/modify" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void modify_project_read_access_public()
        throws Exception
    {
        final ProjectReadAccess readAccess = new ProjectReadAccess( ProjectReadAccessType.PUBLIC );
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build(), null, readAccess );

        final ArgumentCaptor<ModifyProjectParams> modifyParamsCaptor = ArgumentCaptor.forClass( ModifyProjectParams.class );
        Mockito.when( projectService.modify( Mockito.isA( ModifyProjectParams.class ) ) ).thenReturn( project1 );

        createForm( readAccess );

        String jsonString = request().path( "project/modify" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        Mockito.verify( projectService ).modify( modifyParamsCaptor.capture() );

        assertTrue( modifyParamsCaptor.getValue().getReadAccess().getType() == ProjectReadAccessType.PUBLIC );
        assertJson( "modify_project_read_access_public.json", jsonString );
    }

    @Test
    public void modify_project_read_access_custom()
        throws Exception
    {
        final PrincipalKeys principalKeys = PrincipalKeys.from( PrincipalKey.ofSuperUser(), PrincipalKey.ofAnonymous() );
        final ProjectReadAccess readAccess = new ProjectReadAccess( ProjectReadAccessType.CUSTOM, principalKeys );
        final Project project1 = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build(), null, readAccess );

        final ArgumentCaptor<ModifyProjectParams> modifyParamsCaptor = ArgumentCaptor.forClass( ModifyProjectParams.class );
        Mockito.when( projectService.modify( Mockito.isA( ModifyProjectParams.class ) ) ).thenReturn( project1 );

        createForm( readAccess );

        String jsonString = request().path( "project/modify" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        Mockito.verify( projectService ).modify( modifyParamsCaptor.capture() );

        assertTrue( modifyParamsCaptor.getValue().getReadAccess().getType() == ProjectReadAccessType.CUSTOM );
        assertTrue( modifyParamsCaptor.getValue().getReadAccess().getPrincipals().equals( principalKeys ) );
        assertJson( "modify_project_read_access_custom.json", jsonString );
    }

    @Test
    public void no_read_access_set_returns_private_on_create_modify()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", null, null );

        final ArgumentCaptor<ModifyProjectParams> modifyParamsCaptor = ArgumentCaptor.forClass( ModifyProjectParams.class );
        Mockito.when( projectService.modify( Mockito.isA( ModifyProjectParams.class ) ) ).thenReturn( project1 );

        createForm();

        request().path( "project/modify" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        Mockito.verify( projectService ).modify( modifyParamsCaptor.capture() );

        assertTrue( modifyParamsCaptor.getValue().getReadAccess().getType() == ProjectReadAccessType.PRIVATE );
    }

    @Test
    public void delete_project()
        throws Exception
    {
        Mockito.when( projectService.delete( ProjectName.from( "project1" ) ) ).thenReturn( true );

        final String jsonString = request().
            path( "project/delete" ).
            entity( "{\"name\" : \"project1\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().
            getAsString();

        assertEquals( "true", jsonString );
    }

    @Test
    public void delete_default_project_fails()
        throws Exception
    {
        MockRestResponse mockRestResponse = request().
            path( "project/delete" ).
            entity( "{\"name\" : \"" + ProjectConstants.PROJECT_REPO_ID_DEFAULT + "\"}", MediaType.APPLICATION_JSON_TYPE ).
            post();

        assertEquals( 405, mockRestResponse.getStatus() );
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon )
    {
        return createProject( name, displayName, description, icon, null,  new ProjectReadAccess( ProjectReadAccessType.PRIVATE ));
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon,
                                   final ProjectPermissions projectPermissions )
    {
        return createProject( name, displayName, description, icon, projectPermissions,  new ProjectReadAccess( ProjectReadAccessType.PRIVATE ));
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon,
                                   final ProjectPermissions projectPermissions, final ProjectReadAccess readAccess )
    {
        return Project.create().
            name( ProjectName.from( name ) ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            addPermissions( projectPermissions ).
            readAccess( readAccess ).
            build();
    }

    private MultipartForm createForm()
    {
        return createForm( null );
    }

    private MultipartForm createForm( final ProjectReadAccess readAccess )
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem file = createItem( "icon", "logo.png", 10, "png", "image/png" );

        Mockito.when( form.iterator() ).thenReturn( Lists.newArrayList( file ).iterator() );
        Mockito.when( form.get( "icon" ) ).thenReturn( file );
        Mockito.when( form.getAsString( "name" ) ).thenReturn( "projname" );
        Mockito.when( form.getAsString( "displayName" ) ).thenReturn( "Project Display Name" );
        Mockito.when( form.getAsString( "description" ) ).thenReturn( "project Description" );

        if ( readAccess != null )
        {
            final String type = readAccess.getType().toString().toLowerCase();
            final List<String> principals =
                readAccess.getPrincipals().stream().map( principalKey -> "\"" + principalKey + "\"" ).collect( Collectors.toList() );
            final String principalsAsString = String.join( ",", principals );

            Mockito.
                when( form.getAsString( "readAccess" ) ).
                thenReturn( "{ \"type\":\"" + type + "\", \"principals\":[" + principalsAsString + "]}" );
        }

        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );

        return form;
    }

    private MultipartItem createItem( final String name, final String fileName, final long size, final String ext, final String type )
    {
        final MultipartItem item = Mockito.mock( MultipartItem.class );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getFileName() ).thenReturn( fileName + "." + ext );
        Mockito.when( item.getContentType() ).thenReturn( com.google.common.net.MediaType.parse( type ) );
        Mockito.when( item.getSize() ).thenReturn( size );
        Mockito.when( item.getBytes() ).thenReturn( ByteSource.wrap( name.getBytes() ) );
        return item;
    }
}
