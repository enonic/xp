package com.enonic.xp.impl.server.rest.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

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
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class ReprocessRunnableTaskTest
    extends AbstractRunnableTaskTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
    }

    protected ReprocessRunnableTask createAndRunTask()
    {
        return null;
    }

    protected ReprocessRunnableTask createAndRunTask( final ReprocessContentRequestJson params )
    {
        final ReprocessRunnableTask task = ReprocessRunnableTask.create().
            description( "reprocess" ).
            taskService( taskService ).
            contentService( contentService ).
            params( params ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void reprocess_skip_children()
    {
        final Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        final Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );

        final ReprocessRunnableTask task = createAndRunTask( new ReprocessContentRequestJson( "branch:/path/to/content", true ) );
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "reprocess" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "skip_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    public void reprocess_invalid()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenThrow( new RuntimeException( "exceptionMessage" ) );

        final ReprocessRunnableTask task = createAndRunTask( new ReprocessContentRequestJson( "branch:/path/to/content", true ) );
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "reprocess" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "skip_children_error_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    public void reprocess_root_incl_children()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        Mockito.when( this.contentService.getByPath( ContentPath.ROOT ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        Mockito.when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( countResult );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( content.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( childContent.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn( FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createAndRunTask( new ReprocessContentRequestJson( "branch:/", false ) );
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "reprocess" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "root_incl_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    public void reprocess_incl_children()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        Mockito.when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( countResult );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( content.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( childContent.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn( FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createAndRunTask( new ReprocessContentRequestJson( "branch:/path/to/content", false ) );
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "reprocess" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "incl_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    public void reprocess_content_incl_children()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        Content reprocessedChildContent = Content.create( childContent ).displayName( "new name" ).build();
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        Mockito.when( this.contentService.reprocess( childContent.getId() ) ).thenReturn( reprocessedChildContent );
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( countResult );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( content.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( childContent.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn( FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createAndRunTask( new ReprocessContentRequestJson( "branch:/content", false ) );
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "reprocess" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "content_incl_children_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    @Test
    public void reprocess_incl_invalid_children()
        throws Exception
    {
        Content content = createContent( "content-id", ContentPath.from( "/path/to/content" ) );
        Content reprocessedContent = Content.create( content ).displayName( "new name" ).build();
        Content childContent = createContent( "child-id", ContentPath.from( content.getPath(), "child" ) );
        FindContentIdsByQueryResult countResult = FindContentIdsByQueryResult.create().totalHits( 1 ).build();

        Mockito.when( this.contentService.getByPath( content.getPath() ) ).thenReturn( content );
        Mockito.when( this.contentService.reprocess( content.getId() ) ).thenReturn( reprocessedContent );
        Mockito.when( this.contentService.reprocess( childContent.getId() ) ).thenThrow( new RuntimeException( "childException" ) );
        Mockito.when( this.contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn( countResult );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( content.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn(
            FindContentByParentResult.create().contents( Contents.create().add( childContent ).build() ).build() );
        Mockito.when( this.contentService.findByParent( FindContentByParentParams.create().parentId( childContent.getId() ).
            from( 0 ).size( 5 ).build() ) ).thenReturn( FindContentByParentResult.create().contents( Contents.create().build() ).build() );

        final ReprocessRunnableTask task = createAndRunTask( new ReprocessContentRequestJson( "branch:/path/to/content", false ) );
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "reprocess" ) );

        final String result = contentQueryArgumentCaptor.getAllValues().get( 0 );
        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( "incl_children_error_result.json" ),
                                         jsonTestHelper.stringToJson( result ) );
    }

    private Content createContent( final String contentId, final ContentPath contentPath )
    {
        return Content.create().
            id( ContentId.from( contentId ) ).
            type( ContentTypeName.folder() ).
            displayName( "Content display name" ).
            name( "content-name" ).
            path( contentPath ).
            build();
    }

}
