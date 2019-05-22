package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.UnpublishContentJson;
import com.enonic.xp.content.CompareContentResult;
import com.enonic.xp.content.CompareContentResults;
import com.enonic.xp.content.CompareContentsParams;
import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.content.UnpublishContentParams;
import com.enonic.xp.content.UnpublishContentsResult;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class UnpublishRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private UnpublishContentJson params;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.params = Mockito.mock( UnpublishContentJson.class );
    }

    @Override
    protected UnpublishRunnableTask createAndRunTask()
    {
        final UnpublishRunnableTask task = UnpublishRunnableTask.create().
            params( params ).
            description( "Unpublish content" ).
            taskService( taskService ).
            contentService( contentService ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    private void mockResult( final Set<String> ids, final UnpublishContentsResult unpublishResult,
                             final CompareContentResults compareResults )
    {
        Mockito.when( params.getIds() ).thenReturn( ids );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( contents ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.from( ids ) ).build() );
        Mockito.when( contentService.unpublishContent( Mockito.isA( UnpublishContentParams.class ) ) ).thenReturn( unpublishResult );
        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).
            thenReturn( compareResults );
    }

    private String mockSingleAndRunTask( final boolean deleted )
    {
        final Set<String> ids = Collections.singleton( contents.get( 0 ).getId().toString() );
        final UnpublishContentsResult.Builder unpublishResultBuilder = UnpublishContentsResult.create().
            addUnpublished( contents.get( 0 ).getId() ).
            setContentPath( contents.get( 0 ).getPath() );
        if ( deleted )
        {
            unpublishResultBuilder.addDeleted( contents.get( 0 ).getId() );
        }
        final UnpublishContentsResult unpublishResult = unpublishResultBuilder.build();
        final CompareContentResults compareResults = CompareContentResults.create().build();

        mockResult( ids, unpublishResult, compareResults );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        return contentQueryArgumentCaptor.getAllValues().get( 1 );
    }

    private String mockMultipleAndRunTask( final boolean deleted, final boolean deleteAll )
    {
        final Set<String> ids = contents.stream().map( content -> content.getId().toString() ).collect( Collectors.toSet() );
        final UnpublishContentsResult.Builder unpublishResultBuilder = UnpublishContentsResult.create().
            addUnpublished( contents.get( 0 ).getId() ).
            addUnpublished( contents.get( 1 ).getId() ).
            addUnpublished( contents.get( 2 ).getId() );
        if ( deleted )
        {
            if ( deleteAll )
            {
                unpublishResultBuilder.addDeleted( contents.get( 0 ).getId() );
                unpublishResultBuilder.addDeleted( contents.get( 1 ).getId() );
            }
            unpublishResultBuilder.addDeleted( contents.get( 2 ).getId() );
            unpublishResultBuilder.setContentPath( contents.get( 2 ).getPath() );
        }
        final UnpublishContentsResult unpublishResult = unpublishResultBuilder.build();
        final CompareContentResults compareResults =
            CompareContentResults.create().add( new CompareContentResult( CompareStatus.EQUAL, ContentId.from( "id4" ) ) ).build();

        final ArgumentCaptor<Integer> progressArgumentCaptor = ArgumentCaptor.forClass( Integer.class );

        mockResult( ids, unpublishResult, compareResults );

        final UnpublishRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( progressReporter, Mockito.times( 1 ) ).progress( Mockito.anyInt(), progressArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Unpublish content" ) );

        Assert.assertEquals( 4, progressArgumentCaptor.getValue().intValue() );

        return contentQueryArgumentCaptor.getAllValues().get( 1 );
    }

    private String mockMultipleAndRunTask( final boolean deleted )
    {
        return mockMultipleAndRunTask( deleted, false );
    }

    @Test
    public void create_message_multiple()
        throws Exception
    {
        final String resultMessage = mockMultipleAndRunTask( false );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"3 items are unpublished.\"}", resultMessage );
    }

    @Test
    public void create_message_multiple_and_single_deleted()
        throws Exception
    {
        final String resultMessage = mockMultipleAndRunTask( true );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"3 items are unpublished ( \\\"content3\\\" deleted ).\"}",
                             resultMessage );
    }

    @Test
    public void create_message_multiple_and_all_deleted()
        throws Exception
    {
        final String resultMessage = mockMultipleAndRunTask( true, true );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"3 items are unpublished ( 3 deleted ).\"}", resultMessage );
    }

    @Test
    public void create_message_single()
        throws Exception
    {
        final String resultMessage = mockSingleAndRunTask( false );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Item \\\"content1\\\" is unpublished.\"}", resultMessage );
    }

    @Test
    public void create_message_single_deleted()
        throws Exception
    {
        final String resultMessage = mockSingleAndRunTask( true );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Item \\\"content1\\\" is deleted.\"}", resultMessage );
    }

    @Test
    public void create_message_none()
        throws Exception
    {
        final Set<String> ids = Collections.emptySet();
        final UnpublishContentsResult unpublishResult = UnpublishContentsResult.create().build();
        final CompareContentResults compareResults = CompareContentResults.create().build();

        mockResult( ids, unpublishResult, compareResults );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "{\"state\":\"WARNING\",\"message\":\"Nothing to unpublish.\"}", resultMessage );
    }

}
