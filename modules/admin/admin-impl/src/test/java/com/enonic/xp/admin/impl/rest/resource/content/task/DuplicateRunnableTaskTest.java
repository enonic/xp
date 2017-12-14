package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class DuplicateRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private DuplicateContentJson params;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.params = Mockito.mock( DuplicateContentJson.class );
    }

    @Override
    protected DuplicateRunnableTask createAndRunTask()
    {
        final DuplicateRunnableTask task = DuplicateRunnableTask.create().
            params( params ).
            description( "Duplicate content" ).
            taskService( taskService ).
            contentService( contentService ).
            authInfo( authInfo ).
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
        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenReturn( DuplicateContentsResult.create().addDuplicated( contents.get( 0 ).getId() ).setContentName(
                contents.get( 0 ).getDisplayName() ).build() ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) ).
            thenReturn( DuplicateContentsResult.create().addDuplicated( contents.get( 2 ).getId() ).setContentName(
                contents.get( 2 ).getDisplayName() ).build() );

        final DuplicateRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Duplicate content" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "2 items are duplicated. 1 item failed to be duplicated.", resultMessage );
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
        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenReturn( DuplicateContentsResult.create().addDuplicated( contents.get( 0 ).getId() ).setContentName(
                contents.get( 0 ).getDisplayName() ).build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "\"Content 1\" item is duplicated.", resultMessage );
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
        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenThrow( new ContentAlreadyMovedException( contents.get( 0 ).getDisplayName() ) ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "Content could not be duplicated.", resultMessage );
    }
}
