package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.PublishContentJson;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.PublishContentResult;
import com.enonic.xp.content.PushContentParams;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class PublishRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private PublishContentJson params;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.params = Mockito.mock( PublishContentJson.class );
    }

    @Override
    protected PublishRunnableTask createAndRunTask()
    {
        final PublishRunnableTask task = PublishRunnableTask.create().
            params( params ).
            description( "Publish content" ).
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
        final PublishContentResult result = PublishContentResult.create().
            setPushed( ContentIds.from( contents.get( 0 ).getId() ) ).
            setDeleted( ContentIds.from( contents.get( 1 ).getId() ) ).
            setDeletedPath( contents.get( 1 ).getPath() ).
            setFailed( ContentIds.from( contents.get( 2 ).getId() ) ).
            build();

        Mockito.when( params.getIds() ).thenReturn(
            contents.stream().map( content -> content.getId().toString() ).collect( Collectors.toSet() ) );
        Mockito.when( params.getExcludedIds() ).thenReturn( Collections.emptySet() );
        Mockito.when( params.getExcludeChildrenIds() ).thenReturn( Collections.emptySet() );

        Mockito.when( contentService.publish( Mockito.isA( PushContentParams.class ) ) ).thenReturn( result );

        Mockito.when( contentService.getById( Mockito.eq( contents.get( 0 ).getId() ) ) ).thenReturn( contents.get( 0 ) );
        Mockito.when( contentService.getById( Mockito.eq( contents.get( 1 ).getId() ) ) ).thenReturn( contents.get( 1 ) );
        Mockito.when( contentService.getById( Mockito.eq( contents.get( 2 ).getId() ) ) ).thenReturn( contents.get( 2 ) );

        final PublishRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "Publish content" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals(
            "{\"state\":\"WARNING\",\"message\":\"2 items are published ( \\\"content2\\\" deleted ). Item \\\"content3\\\" could not be published.\"}",
            resultMessage );
    }

    private String runTask( final PublishContentResult result )
    {
        Mockito.when( params.getIds() ).thenReturn( Collections.singleton( contents.get( 0 ).getId().toString() ) );

        Mockito.when( contentService.publish( Mockito.isA( PushContentParams.class ) ) ).thenReturn( result );
        Mockito.when( contentService.getById( Mockito.isA( ContentId.class ) ) ).thenReturn( contents.get( 0 ) );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        return contentQueryArgumentCaptor.getAllValues().get( 1 );
    }

    @Test
    public void create_message_single_published()
        throws Exception
    {
        final PublishContentResult result = PublishContentResult.create().
            setPushed( ContentIds.from( contents.get( 0 ).getId() ) ).
            build();

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Item \\\"content1\\\" is published.\"}", runTask( result ) );
    }

    @Test
    public void create_message_single_deleted()
        throws Exception
    {
        final PublishContentResult result = PublishContentResult.create().
            setDeleted( ContentIds.from( contents.get( 0 ).getId() ) ).
            setDeletedPath( contents.get( 0 ).getPath() ).
            build();

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Item \\\"content1\\\" is deleted.\"}", runTask( result ) );
    }

    @Test
    public void create_message_single_failed()
        throws Exception
    {
        final PublishContentResult result = PublishContentResult.create().
            setFailed( ContentIds.from( contents.get( 0 ).getId(), contents.get( 1 ).getId() ) ).
            build();

        Assert.assertEquals( "{\"state\":\"ERROR\",\"message\":\"Failed to publish 2 items. \"}", runTask( result ) );
    }

    @Test
    public void create_message_none()
        throws Exception
    {
        final PublishContentResult result = PublishContentResult.create().build();

        Assert.assertEquals( "{\"state\":\"WARNING\",\"message\":\"Nothing to publish.\"}", runTask( result ) );
    }
}
