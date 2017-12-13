package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.admin.impl.rest.resource.content.json.DuplicateContentJson;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentAlreadyMovedException;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentQuery;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.Contents;
import com.enonic.xp.content.DuplicateContentParams;
import com.enonic.xp.content.DuplicateContentsResult;
import com.enonic.xp.content.FindContentIdsByQueryResult;
import com.enonic.xp.content.GetContentByIdsParams;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

public class DuplicateRunnableTaskTest
{
    private AuthenticationInfo authInfo;

    private ContentService contentService;

    private TaskService taskService;

    private List<Content> contents;

    private ProgressReporter progressReporter;

    private DuplicateContentJson params;

    private ArgumentCaptor<String> contentQueryArgumentCaptor;

    @Before
    public final void setUp()
        throws Exception
    {
        this.authInfo = AuthenticationInfo.create().user( User.ANONYMOUS ).build();
        this.contents = Lists.newArrayList(
            Content.create().id( ContentId.from( "id1" ) ).path( "/content/id1" ).name( "content1" ).displayName( "Content 1" ).build(),
            Content.create().id( ContentId.from( "id2" ) ).path( "/content/id2" ).name( "content2" ).displayName( "Content 2" ).build(),
            Content.create().id( ContentId.from( "id3" ) ).path( "/content/id3" ).name( "content3" ).displayName( "Content 3" ).build() );
        this.contentService = Mockito.mock( ContentService.class );
        this.taskService = Mockito.mock( TaskService.class );
        this.progressReporter = Mockito.mock( ProgressReporter.class );
        this.params = Mockito.mock( DuplicateContentJson.class );
        this.contentQueryArgumentCaptor = ArgumentCaptor.forClass( String.class );
    }

    private DuplicateRunnableTask createAndRunTask()
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
