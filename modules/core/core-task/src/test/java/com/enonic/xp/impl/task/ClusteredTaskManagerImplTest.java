package com.enonic.xp.impl.task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;
import com.hazelcast.core.MemberSelector;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.OffloadedTaskCallable;
import com.enonic.xp.impl.task.distributed.TasksReporterCallable;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusteredTaskManagerImplTest
{
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IExecutorService executorService;

    private ClusteredTaskManagerImpl clusteredTaskManager;


    TaskConfig config;

    @BeforeEach
    void setUp()
    {
        config = mock( TaskConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( hazelcastInstance.getExecutorService( ClusteredTaskManagerImpl.ACTION ) ).thenReturn( executorService );
        clusteredTaskManager = new ClusteredTaskManagerImpl( hazelcastInstance );
        clusteredTaskManager.activate( config );
    }

    @Test
    void getTaskInfo()
    {
        final TaskId taskId = TaskId.from( "someTask" );
        final Member member1 = mock( Member.class );
        final Member member2 = mock( Member.class );
        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( member1, CompletableFuture.completedFuture( List.of( TaskInfo.create().id( taskId ).build() ) ), member2,
                    CompletableFuture.completedFuture( List.of() ) ) );

        final TaskInfo taskInfo = clusteredTaskManager.getTaskInfo( taskId );

        assertNotNull( taskInfo );
        assertEquals( taskId, taskInfo.getId() );
    }

    @Test
    void getTaskInfo_missing()
    {
        final TaskId taskId = TaskId.from( "someTask" );
        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( mock( Member.class ), CompletableFuture.completedFuture( List.of() ) ) );

        final TaskInfo taskInfo = clusteredTaskManager.getTaskInfo( taskId );

        assertNull( taskInfo );
    }

    @Test
    void getAllTasks_collect_data_from_all_members()
    {
        final TaskId taskId1 = TaskId.from( "someTask1" );
        final TaskId taskId2 = TaskId.from( "someTask2" );
        final Member member1 = mock( Member.class );
        final Member member2 = mock( Member.class );
        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( member1, CompletableFuture.completedFuture( List.of( TaskInfo.create().id( taskId1 ).build() ) ), member2,
                    CompletableFuture.completedFuture( List.of( TaskInfo.create().id( taskId2 ).build() ) ) ) );

        final List<TaskInfo> taskInfos = clusteredTaskManager.getAllTasks();

        assertNotNull( taskInfos );
        assertEquals( taskInfos.stream().map( TaskInfo::getId ).collect( Collectors.toSet() ), Set.of( taskId1, taskId2 ) );
    }

    @Test
    void getRunningTasks_collect_data_from_all_members()
    {
        final TaskId taskId1 = TaskId.from( "someTask1" );
        final TaskId taskId2 = TaskId.from( "someTask2" );
        final Member member1 = mock( Member.class );
        final Member member2 = mock( Member.class );
        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( member1, CompletableFuture.completedFuture( List.of( TaskInfo.create().id( taskId1 ).build() ) ), member2,
                    CompletableFuture.completedFuture( List.of( TaskInfo.create().id( taskId2 ).build() ) ) ) );

        final List<TaskInfo> taskInfos = clusteredTaskManager.getRunningTasks();

        assertNotNull( taskInfos );
        assertEquals( taskInfos.stream().map( TaskInfo::getId ).collect( Collectors.toSet() ), Set.of( taskId1, taskId2 ) );
    }

    @Test
    void submitTask()
        throws Exception
    {
        final DescribedTask task = mock( DescribedTask.class );
        when( task.getApplicationKey() ).thenReturn( ApplicationKey.from( "some.app" ) );

        final Future<Void> future = mock( Future.class );
        when( executorService.submit( any( OffloadedTaskCallable.class ), any( MemberSelector.class ) ) ).thenReturn( future );
        clusteredTaskManager.submitTask( task );
        final ArgumentCaptor<MemberSelector> argumentCaptor = ArgumentCaptor.forClass( MemberSelector.class );
        verify( executorService ).submit( any( OffloadedTaskCallable.class ), argumentCaptor.capture() );

        final Member memberSelectable = mock( Member.class );
        when( memberSelectable.getBooleanAttribute( "tasks-enabled" ) ).thenReturn( true );
        when( memberSelectable.getBooleanAttribute( "tasks-enabled-some.app" ) ).thenReturn( true );

        final Member memberDoesNotAcceptOffload = mock( Member.class );
        when( memberDoesNotAcceptOffload.getBooleanAttribute( "tasks-enabled" ) ).thenReturn( false );

        final Member memberDoesNotHaveApp = mock( Member.class );
        when( memberDoesNotHaveApp.getBooleanAttribute( "tasks-enabled" ) ).thenReturn( true );
        when( memberDoesNotHaveApp.getBooleanAttribute( "tasks-enabled-some.app" ) ).thenReturn( null );

        final MemberSelector memberSelector = argumentCaptor.getValue();
        assertTrue( memberSelector.select( memberSelectable ) );
        assertFalse( memberSelector.select( memberDoesNotAcceptOffload ) );
        assertFalse( memberSelector.select( memberDoesNotHaveApp ) );

        verify( future ).get( anyLong(), any( TimeUnit.class ) );
    }
}
