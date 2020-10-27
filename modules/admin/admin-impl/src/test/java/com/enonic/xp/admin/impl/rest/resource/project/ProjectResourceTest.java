package com.enonic.xp.admin.impl.rest.resource.project;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.common.io.ByteSource;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.admin.impl.rest.resource.content.task.ApplyPermissionsRunnableTask;
import com.enonic.xp.attachment.Attachment;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentEditor;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentName;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.EditableContent;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.project.CreateProjectParams;
import com.enonic.xp.project.ModifyProjectIconParams;
import com.enonic.xp.project.ModifyProjectParams;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectPermissions;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.web.multipart.MultipartForm;
import com.enonic.xp.web.multipart.MultipartItem;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProjectResourceTest
    extends AdminResourceTestSupport
{
    private ProjectService projectService;

    private ContentService contentService;

    private TaskService taskService;

    @Override
    protected ProjectResource getResourceInstance()
    {
        projectService = Mockito.mock( ProjectService.class );
        contentService = Mockito.mock( ContentService.class );
        taskService = Mockito.mock( TaskService.class );

        final ProjectResource resource = new ProjectResource();
        resource.setProjectService( projectService );
        resource.setContentService( contentService );
        resource.setTaskService( taskService );

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

        mockProjectPermissions( project.getName() );
        mockRootContent();

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
            build() );

        final Project project2 = createProject( "project2", "project2", null, null, "parent1" );
        final Project project3 = createProject( "project3", null, null, null );
        final Project project4 = createProject( "project4", "project4", null, null, "parent2" );

        mockRootContent();

        Mockito.when( projectService.list() ).thenReturn(
            Projects.create().addAll( List.of( project1, project2, project3, project4 ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project1" ) ) ).
            thenReturn( ProjectPermissions.create().addOwner( PrincipalKey.from( "user:system:owner" ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project2" ) ) ).
            thenReturn( ProjectPermissions.create().addEditor( PrincipalKey.from( "user:system:editor" ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project3" ) ) ).
            thenReturn( ProjectPermissions.create().addAuthor( PrincipalKey.from( "user:system:author" ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project4" ) ) ).
            thenReturn( ProjectPermissions.create().
                addContributor( PrincipalKey.from( "user:system:contributor" ) ).
                addViewer( PrincipalKey.from( "user:system:custom" ) ).
                build() );

        String jsonString = request().path( "project/list" ).get().getAsString();

        assertJson( "list_projects.json", jsonString );
    }

    @Test
    public void fetch_projects_by_content_id()
        throws Exception
    {
        final Project project1 = createProject( "project1", "project name 1", "project description 1", null, "base" );

        final Project project2 = createProject( "project2", "project2", null, null, "parent1" );
        final Project project3 = createProject( "project3", null, null, null );
        final Project project4 = createProject( "project4", "project4", null, null, "parent2" );

        mockRootContent();

        Mockito.when( projectService.list() ).thenReturn(
            Projects.create().addAll( List.of( project1, project2, project3, project4 ) ).build() );

        Mockito.when( contentService.contentExists( ContentId.from( "123" ) ) ).
            thenReturn( true ).
            thenReturn( false ).
            thenReturn( true ).
            thenReturn( false );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project1" ) ) ).
            thenReturn( ProjectPermissions.create().addOwner( PrincipalKey.from( "user:system:owner" ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project2" ) ) ).
            thenReturn( ProjectPermissions.create().addEditor( PrincipalKey.from( "user:system:editor" ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project3" ) ) ).
            thenReturn( ProjectPermissions.create().addAuthor( PrincipalKey.from( "user:system:author" ) ).build() );

        Mockito.when( projectService.getPermissions( ProjectName.from( "project4" ) ) ).
            thenReturn( ProjectPermissions.create().addAuthor( PrincipalKey.from( "user:system:contributor" ) ).build() );

        final String jsonString = request().path( "project/fetchByContentId" ).
            queryParam( "contentId", "123" ).
            get().
            getAsString();

        assertJson( "fetch_by_content_id_projects.json", jsonString );
    }

    @Test
    public void create_project_exception()
        throws Exception
    {
        IllegalArgumentException e = new IllegalArgumentException( "Exception occured." );

        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenThrow( e );

        assertThrows( IllegalArgumentException.class, () -> {
            request().path( "project/create" ).
                entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
                post().getAsString();
        } );
    }

    @Test
    public void create_project_success()
        throws Exception
    {
        final Project project = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        mockRootContent();
        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenReturn( project );
        Mockito.when( projectService.modifyPermissions( Mockito.eq( project.getName() ), Mockito.isA( ProjectPermissions.class ) ) ).
            thenAnswer( i -> i.getArguments()[1] );

        mockProjectPermissions( project.getName() );

        String jsonString = request().path( "project/create" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void create_project_with_parents()
        throws Exception
    {
        final Project project = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build(), "parent1" );

        mockRootContent();
        Mockito.when( projectService.create( Mockito.isA( CreateProjectParams.class ) ) ).thenReturn( project );
        Mockito.when( projectService.modifyPermissions( Mockito.eq( project.getName() ), Mockito.isA( ProjectPermissions.class ) ) ).
            thenAnswer( i -> i.getArguments()[1] );

        mockProjectPermissions( project.getName() );

        String jsonString = request().path( "project/create" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_project_with_parents.json", jsonString );
    }

    @Test
    public void modify_project_success()
        throws Exception
    {
        final Project project = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        mockProjectPermissions( project.getName() );
        mockRootContent();

        Mockito.when( projectService.modify( Mockito.isA( ModifyProjectParams.class ) ) ).thenReturn( project );
        Mockito.when( projectService.modifyPermissions( Mockito.isA( ProjectName.class ), Mockito.isA( ProjectPermissions.class ) ) ).
            thenAnswer( i -> i.getArguments()[1] );
        Mockito.when( projectService.modifyPermissions( Mockito.eq( project.getName() ), Mockito.isA( ProjectPermissions.class ) ) ).
            thenAnswer( i -> i.getArguments()[1] );

        String jsonString = request().path( "project/modify" ).
            entity( readFromFile( "create_project_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_project_success.json", jsonString );
    }

    @Test
    public void modify_language_success()
        throws Exception
    {
        testModifyLanguage( "en" );
    }

    @Test
    public void modify_language_null()
        throws Exception
    {
        testModifyLanguage( null );
    }

    @Test
    public void modify_language_empty()
        throws Exception
    {
        testModifyLanguage( "" );
    }

    private void testModifyLanguage( final String language )
        throws Exception
    {
        createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        final Content rootContent = mockRootContent();

        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).
            then( args -> {
                final UpdateContentParams params = args.getArgument( 0 );
                final ContentEditor contentEditor = params.getEditor();
                final EditableContent editableContent = new EditableContent( rootContent );

                contentEditor.edit( editableContent );
                final Content modifiedContent = editableContent.build();

                assertEquals( language, Optional.ofNullable( modifiedContent.getLanguage() ).
                    map( Locale::toLanguageTag ).
                    orElse( null ) );

                return modifiedContent;
            } );

        request().path( "project/modifyLanguage" ).
            entity( "{\"name\":\"project1\",\"language\":" + language + "}", MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

    }

    @Test
    public void testModifyIcon()
        throws Exception
    {
        final Project project = createProject( "project1", "project name 1", "project description 1", Attachment.create().
            name( "logo.png" ).
            mimeType( "image/png" ).
            label( "small" ).
            build() );

        createIconForm( project.getName(), 150 );

        Mockito.doAnswer( invocation -> {
            final Object[] args = invocation.getArguments();

            ModifyProjectIconParams params = (ModifyProjectIconParams) args[0];
            assertEquals( project.getName(), params.getName() );
            assertEquals( 726l, params.getIcon().getByteSource().size() );
            assertEquals( 150, params.getScaleWidth() );

            return null;
        } ).when( projectService ).modifyIcon( Mockito.any() );

        request().path( "project/modifyIcon" ).
            multipart( "icon", "icon.png", new byte[]{}, MediaType.MULTIPART_FORM_DATA_TYPE ).
            post().getAsString();
    }

    @Test
    public void modify_permissions_success()
        throws Exception
    {
        mockRootContent();

        final ProjectName projectName = ProjectName.from( "project1" );
        Mockito.when( projectService.modifyPermissions( Mockito.eq( projectName ), Mockito.isA( ProjectPermissions.class ) ) ).
            then( args -> {
                final ProjectPermissions projectPermissions = args.getArgument( 1 );
                assertAll( () -> assertTrue( projectPermissions.getOwner().contains( PrincipalKey.from( "user:system:user1" ) ) ),
                           () -> assertTrue( projectPermissions.getEditor().contains( PrincipalKey.from( "user:system:user2" ) ) ),
                           () -> assertTrue( projectPermissions.getAuthor().contains( PrincipalKey.from( "user:system:user3" ) ) ),
                           () -> assertTrue( projectPermissions.getContributor().contains( PrincipalKey.from( "user:system:user4" ) ) ),
                           () -> assertTrue( projectPermissions.getViewer().contains( PrincipalKey.from( "user:system:user5" ) ) ) );

                return projectPermissions;
            } );

        request().path( "project/modifyPermissions" ).
            entity( readFromFile( "modify_permissions_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post();
    }

    @Test
    public void modify_read_access_success()
        throws Exception
    {
        mockRootContent();

        request().path( "project/modifyReadAccess" ).
            entity( readFromFile( "modify_read_access_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post();

        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( ApplyPermissionsRunnableTask.class ),
                                                                      Mockito.eq( "Apply project's content root permissions" ) );
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
        return Project.create().
            name( ProjectName.from( name ) ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            build();
    }

    private Project createProject( final String name, final String displayName, final String description, final Attachment icon,
                                   final String parent )
    {
        return Project.create().
            name( ProjectName.from( name ) ).
            displayName( displayName ).
            description( description ).
            icon( icon ).
            parent( ProjectName.from( parent ) ).
            build();
    }

    private MultipartForm createIconForm( final ProjectName projectName, final int scaleWidth )
        throws IOException
    {
        final MultipartForm form = Mockito.mock( MultipartForm.class );

        try (InputStream stream = this.getClass().getResourceAsStream( "icon/projecticon1.png" ))
        {
            final MultipartItem file = createItem( "icon", "logo.png", 10, "png", "image/png", stream.readAllBytes() );

            Mockito.when( form.iterator() ).thenReturn( Lists.newArrayList( file ).iterator() );
            Mockito.when( form.get( "icon" ) ).thenReturn( file );
            if ( projectName != null )
            {
                Mockito.when( form.getAsString( "name" ) ).thenReturn( projectName.toString() );
            }
            if ( scaleWidth > 0 )
            {

                Mockito.when( form.getAsString( "scaleWidth" ) ).thenReturn( String.valueOf( scaleWidth ) );
            }

            Mockito.when( form.getAsString( "readAccess" ) ).thenReturn( "{\"type\":\"custom\", \"principals\":[\"user:system:custom\"]}" );

            Mockito.when( this.multipartService.parse( Mockito.any() ) ).thenReturn( form );

            return form;
        }

    }

    private MultipartItem createItem( final String name, final String fileName, final long size, final String ext, final String type,
                                      final byte[] bytes )
    {
        final MultipartItem item = Mockito.mock( MultipartItem.class );
        Mockito.when( item.getName() ).thenReturn( name );
        Mockito.when( item.getFileName() ).thenReturn( fileName + "." + ext );
        Mockito.when( item.getContentType() ).thenReturn( com.google.common.net.MediaType.parse( type ) );
        Mockito.when( item.getSize() ).thenReturn( size );
        Mockito.when( item.getBytes() ).thenReturn( ByteSource.wrap( bytes ) );
        return item;
    }

    private void mockProjectPermissions( final ProjectName projectName )
    {
        final ProjectPermissions projectPermissions = ProjectPermissions.create().
            addOwner( PrincipalKey.from( "user:system:owner" ) ).
            addEditor( PrincipalKey.from( "user:system:editor" ) ).
            addAuthor( PrincipalKey.from( "user:system:author" ) ).
            addContributor( PrincipalKey.from( "user:system:contributor" ) ).
            addViewer( PrincipalKey.from( "user:system:custom" ) ).
            build();

        Mockito.when( projectService.getPermissions( projectName ) ).thenReturn( projectPermissions );
    }

    private Content mockRootContent()
    {
        final Content contentRoot = Content.create().id( ContentId.from( "123" ) ).
            name( ContentName.from( "root" ) ).
            parentPath( ContentPath.ROOT ).
            permissions( AccessControlList.empty() ).
            language( Locale.ENGLISH ).
            data( new PropertyTree() ).
            extraDatas( ExtraDatas.empty() ).
            build();

        Mockito.when( contentService.getByPath( ContentPath.ROOT ) ).thenReturn( contentRoot );
        Mockito.when( contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( contentRoot );

        return contentRoot;
    }
}
