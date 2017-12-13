package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.MoveContentJson;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.MoveContentParams;
import com.enonic.xp.content.MoveContentsResult;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class MoveRunnableTaskTest
    extends CommonRunnableTaskTest
{
    private MoveContentJson params;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.params = Mockito.mock( MoveContentJson.class );
    }

    @Override
    protected MoveRunnableTask createAndRunTask()
    {
        final MoveRunnableTask task = MoveRunnableTask.create().
            params( params ).
            description( "Move content" ).
            taskService( taskService ).
            contentService( contentService ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void create_message_multiple()
        throws Exception
    {
        Mockito.when( params.getContentIds() ).thenReturn(
            contents.stream().map( content -> content.getId().toString() ).collect( Collectors.toList() ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( contents ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 3 ).build() );
        Mockito.when( contentService.move( Mockito.isA( MoveContentParams.class ) ) ).
            thenReturn( MoveContentsResult.create().addMoved( contents.get( 0 ).getId() ).setContentName(
                contents.get( 0 ).getDisplayName() ).build() ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) ).
            thenReturn( MoveContentsResult.create().addMoved( contents.get( 2 ).getId() ).setContentName(
                contents.get( 2 ).getDisplayName() ).build() );

        final MoveRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "Move content" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "2 items are moved. 1 item failed to be moved.", resultMessage );
    }

    @Test
    public void create_message_single()
        throws Exception
    {
        Mockito.when( params.getContentIds() ).thenReturn(
            contents.subList( 0, 1 ).stream().map( content -> content.getId().toString() ).collect( Collectors.toList() ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn(
            Contents.from( contents.subList( 0, 1 ) ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 1 ).build() );
        Mockito.when( contentService.move( Mockito.isA( MoveContentParams.class ) ) ).
            thenReturn( MoveContentsResult.create().addMoved( contents.get( 0 ).getId() ).setContentName(
                contents.get( 0 ).getDisplayName() ).build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "\"Content 1\" item is moved.", resultMessage );
    }

    @Test
    public void create_message_failed()
        throws Exception
    {
        Mockito.when( params.getContentIds() ).thenReturn(
            contents.subList( 0, 2 ).stream().map( content -> content.getId().toString() ).collect( Collectors.toList() ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn(
            Contents.from( contents.subList( 0, 2 ) ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 1 ).build() );
        Mockito.when( contentService.move( Mockito.isA( MoveContentParams.class ) ) ).
            thenThrow( new ContentAlreadyMovedException( contents.get( 0 ).getDisplayName() ) ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "Content could not be moved.", resultMessage );
    }
}
