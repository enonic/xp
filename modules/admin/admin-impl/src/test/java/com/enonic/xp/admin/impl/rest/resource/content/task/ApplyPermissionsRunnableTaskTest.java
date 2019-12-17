package com.enonic.xp.admin.impl.rest.resource.content.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.content.json.ApplyContentPermissionsJson;
import com.enonic.xp.content.ApplyContentPermissionsParams;
import com.enonic.xp.content.ApplyContentPermissionsResult;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentIds;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;
import com.enonic.xp.content.FindContentByParentParams;
import com.enonic.xp.content.FindContentIdsByParentResult;
import com.enonic.xp.content.UpdateContentParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.task.AbstractRunnableTaskTest;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

import static com.enonic.xp.security.acl.Permission.READ;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApplyPermissionsRunnableTaskTest
    extends AbstractRunnableTaskTest
{
    private ApplyContentPermissionsJson params;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final Content child = Content.create().
            id( ContentId.from( "content-id" ) ).
            path( "/content/content1/content4" ).
            name( "content4" ).
            displayName( "Content 4" ).
            parentPath( ContentPath.from( "/content/content1" ) ).
            build();
        this.contents.add( child );
        this.params = Mockito.mock( ApplyContentPermissionsJson.class );

        Mockito.when( this.params.getContentId() ).thenReturn( ContentId.from( "content-id" ) );
        Mockito.when( this.params.getPermissions() ).thenReturn( getTestPermissions() );
        Mockito.when( this.params.isInheritPermissions() ).thenReturn( true );

        final FindContentIdsByParentResult res =
            FindContentIdsByParentResult.create().contentIds( ContentIds.from( "content-id" ) ).hits( 1 ).totalHits( 1 ).build();
        Mockito.when( this.contentService.findIdsByParent( Mockito.isA( FindContentByParentParams.class ) ) ).thenReturn( res );
        Mockito.when( this.contentService.update( Mockito.isA( UpdateContentParams.class ) ) ).thenReturn( this.contents.get( 0 ) );
    }

    @Override
    protected ApplyPermissionsRunnableTask createAndRunTask()
    {
        final ApplyPermissionsRunnableTask task = ApplyPermissionsRunnableTask.create().
            params( params ).
            description( "Apply permissions" ).
            taskService( taskService ).
            contentService( contentService ).
            build();

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void message_empty()
        throws Exception
    {
        Mockito.when( this.contentService.applyPermissions( Mockito.isA( ApplyContentPermissionsParams.class ) ) ).thenReturn(
            ApplyContentPermissionsResult.create().build() );

        final ApplyPermissionsRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Apply permissions" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 0 );

        assertEquals( "{\"state\":\"WARNING\",\"message\":\"Nothing to edit.\"}", resultMessage );
    }

    @Test
    public void message_multiple_success()
        throws Exception
    {

        Mockito.when( this.contentService.applyPermissions( Mockito.isA( ApplyContentPermissionsParams.class ) ) ).thenReturn(
            ApplyContentPermissionsResult.create().setSucceedContents(
                ContentPaths.from( ContentPath.from( "a/b/content-id1" ), ContentPath.from( "a/b/content-id2" ) ) ).build() );

        final ApplyPermissionsRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Apply permissions" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 0 );

        assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Permissions for 2 items are applied.\"}", resultMessage );
    }

    @Test
    public void single_success()
        throws Exception
    {

        Mockito.when( this.contentService.applyPermissions( Mockito.isA( ApplyContentPermissionsParams.class ) ) ).thenReturn(
            ApplyContentPermissionsResult.create().setSucceedContents(
                ContentPaths.from( ContentPath.from( "a/b/content-id1" ) ) ).build() );

        final ApplyPermissionsRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Apply permissions" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 0 );

        assertEquals( "{\"state\":\"SUCCESS\",\"message\":\"Permissions for \\\"content-id1\\\" are applied.\"}", resultMessage );
    }

    @Test
    public void create_message_single_failed()
        throws Exception
    {

        Mockito.when( this.contentService.applyPermissions( Mockito.isA( ApplyContentPermissionsParams.class ) ) ).thenReturn(
            ApplyContentPermissionsResult.create().setSkippedContents( ContentPaths.from( this.contents.get( 0 ).getPath() ) ).build() );

        final ApplyPermissionsRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Apply permissions" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 0 );

        assertEquals( "{\"state\":\"ERROR\",\"message\":\"Permissions for \\\"content1\\\" could not be applied.\"}",
                             resultMessage );
    }

    @Test
    public void create_message_multiple_failed_and_one_succeed()
        throws Exception
    {

        Mockito.when( this.contentService.applyPermissions( Mockito.isA( ApplyContentPermissionsParams.class ) ) ).
            thenReturn( ApplyContentPermissionsResult.
                create().
                setSkippedContents( ContentPaths.from( this.contents.get( 0 ).getPath(), ContentPath.from( "id2" ) ) ).
                setSucceedContents( ContentPaths.create().add( ContentPath.from( "a/b/content1" ) ).build() ).
                build() );

        final ApplyPermissionsRunnableTask task = createAndRunTask();
        task.createTaskResult();

        Mockito.verify( progressReporter, Mockito.times( 1 ) ).info( contentQueryArgumentCaptor.capture() );
        Mockito.verify( taskService, Mockito.times( 1 ) ).submitTask( Mockito.isA( RunnableTask.class ),
                                                                      Mockito.eq( "Apply permissions" ) );

        final String resultMessage = contentQueryArgumentCaptor.getAllValues().get( 0 );

        assertEquals(
            "{\"state\":\"WARNING\",\"message\":\"Permissions for \\\"content1\\\" are applied. Failed to apply permissions for 2 items. \"}",
            resultMessage );
    }

    private AccessControlList getTestPermissions()
    {
        return AccessControlList.of( AccessControlEntry.create().principal( PrincipalKey.from( "user:system:admin" ) ).allowAll().build(),
                                     AccessControlEntry.create().principal( PrincipalKey.ofAnonymous() ).allow( READ ).build() );
    }
}
