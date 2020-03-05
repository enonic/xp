package com.enonic.xp.admin.impl.rest.resource.project;

import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
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
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.project.layer.ContentLayer;
import com.enonic.xp.project.layer.ContentLayerKey;
import com.enonic.xp.project.layer.CreateLayerParams;
import com.enonic.xp.project.layer.ModifyLayerParams;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
            createProject( "project2", "project2", null, null, ProjectPermissions.create().addExpert( RoleKeys.AUTHENTICATED ).build() );
        final Project project3 =
            createProject( "project3", null, null, null, ProjectPermissions.create().addContributor( RoleKeys.AUTHENTICATED ).build() );

        Mockito.when( projectService.list() ).thenReturn( Projects.create().addAll( List.of( project1, project2, project3 ) ).build() );

        String jsonString = request().path( "project/list" ).get().getAsString();

        assertJson( "list_projects.json", jsonString );
    }

    @Test
    public void create_project_exception()
        throws Exception
    {
        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenThrow( e );

        createProjectForm();

        assertThrows( IllegalArgumentException.class, () -> {
            request().path( "project/create" ).
                entity( "".getBytes(), MediaType.MULTIPART_FORM_DATA_TYPE ).
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

        createProjectForm();

        String jsonString = request().path( "project/create" ).
            multipart( "icon", "logo.png", "".getBytes(), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void create_layer_success()
        throws Exception
    {
        final ContentLayer layer = createLayer( "default:layer1", "layer name 1", "layer description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.createLayer( Mockito.isA( CreateLayerParams.class ) ) ).thenReturn( layer );

        createLayerForm();

        String jsonString = request().path( "project/createLayer" ).
            multipart( "icon", "logo.png", "".getBytes(), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "create_layer_success.json", jsonString );
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

        createProjectForm();

        String jsonString = request().path( "project/modify" ).
            entity( "".getBytes(), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void modify_layer_success()
        throws Exception
    {
        final ContentLayer layer = createLayer( "default:layer1", "layer name 1", "layer description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        Mockito.when( projectService.modifyLayer( Mockito.isA( ModifyLayerParams.class ) ) ).thenReturn( layer );

        createLayerForm();

        String jsonString = request().path( "project/modifyLayer" ).
            entity( "".getBytes(), MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();

        assertJson( "create_layer_success.json", jsonString );
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

    @Test
    public void delete_layer()
        throws Exception
    {
        Mockito.when( projectService.deleteLayer( ContentLayerKey.from( "default:layer" ) ) ).thenReturn( true );

        final String jsonString = request().
            path( "project/deleteLayer" ).
            entity( "{\"key\" : \"default:layer\"}", MediaType.APPLICATION_JSON_TYPE ).
            post().
            getAsString();

        assertEquals( "true", jsonString );
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon )
    {
        return createProject( name, displayName, description, icon, null );
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon,
                                   final ProjectPermissions projectPermissions )
    {
        return Project.create().
            name( ProjectName.from( name ) ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            addPermissions( projectPermissions ).
            build();
    }

    private ContentLayer createLayer( final String key, final String displayName, final String description, final Attachment icon )
    {
        final ContentLayerKey contentLayerKey = ContentLayerKey.from( key );

        return ContentLayer.create().
            key( contentLayerKey ).
            displayName( displayName ).
            description( description ).
            addParentKey( ContentLayerKey.from( "default:base" ) ).
            locale( Locale.ENGLISH ).
            icon( icon ).
            build();
    }

    private MultipartForm createProjectForm()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem file = createItem( "icon", "logo.png", 10, "png", "image/png" );

        Mockito.when( form.iterator() ).thenReturn( Lists.newArrayList( file ).iterator() );
        Mockito.when( form.get( "icon" ) ).thenReturn( file );
        Mockito.when( form.getAsString( "name" ) ).thenReturn( "projname" );
        Mockito.when( form.getAsString( "displayName" ) ).thenReturn( "Project Display Name" );
        Mockito.when( form.getAsString( "description" ) ).thenReturn( "project Description" );

        Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );

        return form;
    }

    private MultipartForm createLayerForm()
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        final MultipartItem file = createItem( "icon", "logo.png", 10, "png", "image/png" );

        Mockito.when( form.iterator() ).thenReturn( Lists.newArrayList( file ).iterator() );
        Mockito.when( form.get( "icon" ) ).thenReturn( file );
        Mockito.when( form.getAsString( "key" ) ).thenReturn( "default:layer1" );
        Mockito.when( form.getAsString( "displayName" ) ).thenReturn( "Layer Display Name" );
        Mockito.when( form.getAsString( "description" ) ).thenReturn( "Layer Description" );
        Mockito.when( form.getAsString( "locale" ) ).thenReturn( "en" );
        Mockito.when( form.getAsString( "parentKeys" ) ).thenReturn( "default:parent,default:otherParent" );

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
