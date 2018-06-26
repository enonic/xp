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

    @Test
    public void create_message_multiple()
        throws Exception
    {
        final UnpublishContentsResult result = UnpublishContentsResult.create().
            addUnpublished( contents.get( 0 ).getId() ).
            addUnpublished( contents.get( 1 ).getId() ).
            addUnpublished( contents.get( 2 ).getId() ).
            build();

        Set<String> ids = contents.stream().map( content -> content.getId().toString() ).collect( Collectors.toSet() );

        final ArgumentCaptor<Integer> progressArgumentCaptor = ArgumentCaptor.forClass( Integer.class );

        Mockito.when( params.getIds() ).thenReturn( ids );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( contents ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.from( ids ) ).build() );
        Mockito.when( contentService.unpublishContent( Mockito.isA( UnpublishContentParams.class ) ) ).thenReturn( result );
        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).
            thenReturn(
                CompareContentResults.create().add( new CompareContentResult( CompareStatus.EQUAL, ContentId.from( "id4" ) ) ).build() );

        final UnpublishRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( progressReporter, Mockito.times( 1 ) ).progress( Mockito.anyInt(), progressArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Unpublish content" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( 4, progressArgumentCaptor.getValue().intValue() );
        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"3 items are unpublished\"}", resultMessage );
    }

    @Test
    public void create_message_single()
        throws Exception
    {
        final UnpublishContentsResult result = UnpublishContentsResult.create().
            addUnpublished( contents.get( 0 ).getId() ).
            setContentPath( contents.get( 0 ).getPath() ).
            build();

        Set<String> ids = Collections.singleton( contents.get( 0 ).getId().toString() );

        Mockito.when( params.getIds() ).thenReturn( ids );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( contents ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.from( ids ) ).build() );
        Mockito.when( contentService.unpublishContent( Mockito.isA( UnpublishContentParams.class ) ) ).thenReturn( result );
        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).
            thenReturn( CompareContentResults.create().build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Item \\\"content1\\\" is unpublished.\"}", resultMessage );
    }

    @Test
    public void create_message_none()
        throws Exception
    {
        final UnpublishContentsResult result = UnpublishContentsResult.create().build();

        Set<String> ids = Collections.emptySet();

        Mockito.when( params.getIds() ).thenReturn( ids );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( contents ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().contents( ContentIds.from( ids ) ).build() );
        Mockito.when( contentService.unpublishContent( Mockito.isA( UnpublishContentParams.class ) ) ).thenReturn( result );
        Mockito.when( contentService.compare( Mockito.isA( CompareContentsParams.class ) ) ).
            thenReturn( CompareContentResults.create().build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "{\"state\":\"WARNING\",\"message\":\"Nothing to unpublish.\"}", resultMessage );
    }

}
