package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.DeleteContentJson;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.DeleteContentParams;
import com.enonic.xp.content.DeleteContentsResult;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class DeleteRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private DeleteContentJson params;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        final Content child = Content.create().
            id( ContentId.from( "id4" ) ).
            path( "/content/content1/content4" ).
            name( "content4" ).
            displayName( "Content 4" ).
            parentPath( ContentPath.from( "/content/content1" ) ).
            build();
        this.contents.add( child );
        this.params = Mockito.mock( DeleteContentJson.class );
    }

    @Override
    protected DeleteRunnableTask createAndRunTask()
    {
        final DeleteRunnableTask task = DeleteRunnableTask.create().
            params( params ).
            description( "Delete content" ).
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
        Mockito.when( params.getContentPaths() ).thenReturn(
            contents.stream().map( content -> content.getPath().toString() ).collect( Collectors.toSet() ) );
        Mockito.when( contentService.deleteWithoutFetch( Mockito.isA( DeleteContentParams.class ) ) ).
            thenReturn(
                DeleteContentsResult.create().addDeleted( contents.get( 0 ).getId() ).addDeleted( contents.get( 3 ).getId() ).build() ).
            thenReturn( DeleteContentsResult.create().addPending( contents.get( 1 ).getId() ).build() ).
            thenThrow( new ContentNotFoundException( contents.get( 2 ).getPath(), Branch.from( "master" ) ) );
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).thenReturn( contents.get( 2 ) );

        final DeleteRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ), Mockito.eq( "Delete content" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "2 items are deleted. 1 item is marked for deletion. 1 item failed to be deleted.", resultMessage );
    }

    @Test
    public void create_message_single()
        throws Exception
    {
        Mockito.when( params.getContentPaths() ).thenReturn(
            contents.subList( 3, 4 ).stream().map( content -> content.getPath().toString() ).collect( Collectors.toSet() ) );
        Mockito.when( contentService.deleteWithoutFetch( Mockito.isA( DeleteContentParams.class ) ) ).
            thenReturn( DeleteContentsResult.create().addDeleted( contents.get( 3 ).getId() ).build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "The item is deleted.", resultMessage );
    }

    @Test
    public void create_message_single_pending()
        throws Exception
    {
        Mockito.when( params.getContentPaths() ).thenReturn(
            contents.subList( 3, 4 ).stream().map( content -> content.getPath().toString() ).collect( Collectors.toSet() ) );
        Mockito.when( contentService.deleteWithoutFetch( Mockito.isA( DeleteContentParams.class ) ) ).
            thenReturn( DeleteContentsResult.create().addPending( contents.get( 3 ).getId() ).build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "The item is marked for deletion.", resultMessage );
    }

    @Test
    public void create_message_single_failed()
        throws Exception
    {
        Mockito.when( params.getContentPaths() ).thenReturn(
            contents.subList( 3, 4 ).stream().map( content -> content.getPath().toString() ).collect( Collectors.toSet() ) );
        Mockito.when( contentService.deleteWithoutFetch( Mockito.isA( DeleteContentParams.class ) ) ).
            thenThrow( new ContentNotFoundException( contents.get( 3 ).getPath(), Branch.from( "master" ) ) );
        Mockito.when( contentService.getByPath( Mockito.isA( ContentPath.class ) ) ).thenReturn( contents.get( 3 ) );

        exception.expect( RuntimeException.class );
        exception.expectMessage( "Content could not be deleted." );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( Mockito.anyString() );
    }

    @Test
    public void create_message_none()
        throws Exception
    {
        Mockito.when( params.getContentPaths() ).thenReturn( Collections.emptySet() );

        exception.expect( RuntimeException.class );
        exception.expectMessage( "Nothing to delete." );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( Mockito.anyString() );
    }
}
