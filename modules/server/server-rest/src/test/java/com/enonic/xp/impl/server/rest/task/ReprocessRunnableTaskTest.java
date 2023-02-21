package com.enonic.xp.impl.server.rest.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentByParentResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.impl.server.rest.model.ReprocessContentRequestJson;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReprocessRunnableTaskTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    private ContentService contentService;

    @BeforeEach
    void setUp()
    {
        this.contentService = mock( ContentService.class );
    }

    private ReprocessRunnableTask createTask( final ReprocessContentRequestJson params )
    {

        return ReprocessRunnableTask.create()
            .contentService( contentService )
            .branch( params.getBranch() )
            .contentPath( params.getContentPath() )
            .skipChildren( params.isSkipChildren() )
            .build();
    }

    @Test
    void reprocess_skip_children()
    {
        final Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        final Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );

        final ReprocessRunnableTask task = createTask( new ReprocessContentRequestJson( "branch:/path/to/content", true ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "skip_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    void reprocess_invalid()
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenThrow( new RuntimeException( "exceptionMessage" ) );

        final ReprocessRunnableTask task = createTask( new ReprocessContentRequestJson( "branch:/path/to/content", true ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "skip_children_error_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    void reprocess_root_incl_children()
    {
        Content content = createContent( "content-id", ContentPath.from( "/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        when( this.contentService.getByPath( ContentPath.ROOT ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );
        when( this.contentService.find( any( ContentQuery.class ) ) ).thenReturn( countResult );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( content.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( childContent.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createTask( new ReprocessContentRequestJson( "branch:/", false ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "root_incl_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    void reprocess_incl_children()
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );
        when( this.contentService.find( any( ContentQuery.class ) ) ).thenReturn( countResult );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( content.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( childContent.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createTask( new ReprocessContentRequestJson( "branch:/path/to/content", false ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "incl_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    void reprocess_content_incl_children()
    {
        Content content = createContent( "content-id", ContentPath.from( "/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );
        when( this.contentService.find( any( ContentQuery.class ) ) ).thenReturn( countResult );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( content.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( childContent.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createTask( new ReprocessContentRequestJson( "branch:/content", false ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "content_incl_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    void reprocess_incl_invalid_children()
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        when( this.contentService.reprocess( childContent.getId() ) ).thenThrow( new RuntimeException( "childException" ) );
        when( this.contentService.find( any( ContentQuery.class ) ) ).thenReturn( countResult );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( content.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        when( this.contentService.findByParent(
            FindContentByParentParams.create().parentId( childContent.getId() ).from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createTask( new ReprocessContentRequestJson( "branch:/path/to/content", false ) );

        ProgressReporter progressReporter = mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        final ArgumentCaptor<String> progressReporterCaptor = ArgumentCaptor.forClass( String.class );
        verify( progressReporter, times( 1 ) ).info( progressReporterCaptor.capture() );

        final String result = progressReporterCaptor.getValue();
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "incl_children_error_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
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
