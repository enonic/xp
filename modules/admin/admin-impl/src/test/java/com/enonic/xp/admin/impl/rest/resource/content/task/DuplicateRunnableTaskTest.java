package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentsJson;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
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
    private DuplicateContentsJson params;

    @Before
    @Override
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.params = Mockito.mock( DuplicateContentsJson.class );
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

        final Content child4 =
            Content.create().id( ContentId.from( "id4" ) ).path( ContentPath.from( contents.get( 0 ).getPath(), "content4" ) ).name(
                "content4" ).displayName( "Content 4" ).build();
        final Content child5 = Content.create().id( ContentId.from( "id5" ) ).path( ContentPath.from( child4.getPath(), "content5" ) ).name(
            "content5" ).displayName( "Content 5" ).build();
        final ContentIds childrenIds = ContentIds.from( child4.getId(), child5.getId() );

        Mockito.when( params.getContents() ).thenReturn(
            contents.stream().map( content -> new DuplicateContentJson( content.getId().toString(), true ) ).collect(
                Collectors.toList() ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn( Contents.from( contents ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 3 ).build() );
        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenReturn(
                DuplicateContentsResult.create().addDuplicated( contents.get( 0 ).getId() ).addDuplicated( childrenIds ).setContentName(
                    contents.get( 0 ).getDisplayName() ).build() ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) ).
            thenThrow( new ContentAlreadyMovedException( contents.get( 2 ).getDisplayName(), contents.get( 2 ).getPath() ) );

        final DuplicateRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Duplicate content" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals(
            "{\"state\":\"WARNING\",\"message\":\"4 items are duplicated ( Already duplicated: \\\"content3\\\" ). Item \\\"id2\\\" could not be duplicated.\"}",
            resultMessage );
    }

    @Test
    public void create_message_single()
        throws Exception
    {
        Mockito.when( params.getContents() ).thenReturn(
            contents.subList( 0, 1 ).stream().map( content -> new DuplicateContentJson( content.getId().toString(), true ) ).collect(
                Collectors.toList() ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn(
            Contents.from( contents.subList( 0, 1 ) ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 1 ).build() );
        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenReturn( DuplicateContentsResult.create().addDuplicated( contents.get( 0 ).getId() ).setContentName(
                contents.get( 0 ).getDisplayName() ).setSourceContentPath( contents.get( 0 ).getPath() ).build() );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Item \\\"content1\\\" is duplicated.\"}", resultMessage );
    }

    @Test
    public void create_message_failed()
        throws Exception
    {
        Mockito.when( params.getContents() ).thenReturn(
            contents.subList( 0, 2 ).stream().map( content -> new DuplicateContentJson( content.getId().toString(), true ) ).collect(
                Collectors.toList() ) );
        Mockito.when( contentService.getByIds( Mockito.isA( GetContentByIdsParams.class ) ) ).thenReturn(
            Contents.from( contents.subList( 0, 2 ) ) );
        Mockito.when( contentService.find( Mockito.isA( ContentQuery.class ) ) ).thenReturn(
            FindContentIdsByQueryResult.create().totalHits( 1 ).build() );
        Mockito.when( contentService.duplicate( Mockito.isA( DuplicateContentParams.class ) ) ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) ).
            thenThrow( new ContentNotFoundException( contents.get( 1 ).getPath(), Branch.from( "master" ) ) );

        createAndRunTask();

        Mockito.verify( progressReporter, Mockito.times( 2 ) ).info( contentQueryArgumentCaptor.capture() );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 1 );

        Assert.assertEquals( "{\"state\":\"ERROR\",\"message\":\"Failed to duplicate 2 items.\"}", resultMessage );
    }
}
