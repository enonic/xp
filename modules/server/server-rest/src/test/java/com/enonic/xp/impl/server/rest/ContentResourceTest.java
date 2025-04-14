package com.enonic.xp.impl.server.rest;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.ws.rs.core.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.jaxrs.impl.JaxRsResourceTestSupport;
import com.enonic.xp.jaxrs.impl.MockRestResponse;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.task.SubmitLocalTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ContentResourceTest
    extends JaxRsResourceTestSupport
{
    private ContentService contentService;

    private TaskService taskService;

    @Test
    public void reprocess_skip_children()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );

        final String result = request().path( "content/reprocess" )
            .entity( readFromFile( "reprocess_params_skip_children.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals( "{\"errors\":[],\"updatedContent\":[\"/path/to/content\"]}", result );
    }

    @Test
    public void reprocess_child()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );

        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( content.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );

        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( childContent.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );

        final MockRestResponse result =
            request().path( "content/reprocess" ).entity( readFromFile( "reprocess_params.json" ), MediaType.APPLICATION_JSON_TYPE ).post();

        assertEquals( "{\"errors\":[],\"updatedContent\":[\"/path/to/content\",\"/path/to/content/child\"]}", result.getAsString() );
    }

    @Test
    public void reprocess_invalid()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenThrow( new RuntimeException( "exceptionMessage" ) );

        final String result = request().path( "content/reprocess" )
            .entity( readFromFile( "reprocess_params_skip_children.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals( "{\"errors\":[\"Content '/path/to/content' - java.lang.RuntimeException: exceptionMessage\"],\"updatedContent\":[]}",
                      result );
    }

    @Test
    public void reprocess_child_with_error()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content child = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );

        when( this.contentService.reprocess( child.getId() ) ).thenThrow( new RuntimeException( "errorMessage" ) );

        final FindContentByParentParams findParams =
            FindContentByParentParams.create().parentId( content.getId() ).from( 0 ).size( 5 ).build();

        when( this.contentService.findByParent( findParams ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( child ).build() ).build() );

        final String result = request().path( "content/reprocess" )
            .entity( readFromFile( "reprocess_params.json" ), MediaType.APPLICATION_JSON_TYPE )
            .post()
            .getAsString();

        assertEquals(
            "{\"errors\":[\"Content '/path/to/content/child' - java.lang.RuntimeException: errorMessage\"],\"updatedContent\":[\"/path/to/content\"]}",
            result );
    }

    @Test
    public void sync()
        throws Exception
    {
        when( this.taskService.submitLocalTask( any() ) ).thenReturn( TaskId.from( "task-id" ) );

        final MockRestResponse result = request().path( "content/syncAll" ).
            entity( "", MediaType.APPLICATION_JSON_TYPE ).
            post();

        ArgumentCaptor<SubmitLocalTaskParams> submitLocalTaskParamsCaptor = ArgumentCaptor.forClass( SubmitLocalTaskParams.class );
        verify( taskService, times( 1 ) ).submitLocalTask( submitLocalTaskParamsCaptor.capture() );
        assertThat( submitLocalTaskParamsCaptor.getValue() ).extracting( SubmitLocalTaskParams::getName,
                                                                         SubmitLocalTaskParams::getDescription )
            .containsExactly( "sync-all-projects", "Sync all projects" );

        assertEquals( "{\"taskId\":\"task-id\"}", result.getDataAsString() );
    }

    @Override
    protected Object getResourceInstance()
    {
        this.contentService = mock( ContentService.class );
        this.taskService = mock( TaskService.class );

        final ProjectService projectService = mock( ProjectService.class );
        final SyncContentService syncContentService = mock( SyncContentService.class );

        final ContentResource resource = new ContentResource();
        resource.setContentService( contentService );
        resource.setTaskService( taskService );
        resource.setProjectService( projectService );
        resource.setSyncContentService( syncContentService );
        return resource;
    }

    private Content createContent( final String contentId, final ContentPath contentPath )
    {
        return Content.create()
            .id( ContentId.from( contentId ) )
            .type( ContentTypeName.folder() )
            .displayName( "Content display name" )
            .name( "content-name" )
            .path( contentPath )
            .build();
    }
}
