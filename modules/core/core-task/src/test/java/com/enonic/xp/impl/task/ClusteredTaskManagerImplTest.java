package com.enonic.xp.impl.task;

import java.time.Instant;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;

import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MemberSelector;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.impl.task.distributed.DescribedTask;
import com.enonic.xp.impl.task.distributed.OffloadedTaskCallable;
import com.enonic.xp.impl.task.distributed.TasksReporterCallable;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusteredTaskManagerImplTest
{
    @Mock
    BundleContext bundleContext;

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
        clusteredTaskManager = new ClusteredTaskManagerImpl( bundleContext, hazelcastInstance );
        clusteredTaskManager.activate( config );
    }

    @Test
    void getTaskInfo()
    {
        final TaskId taskId = TaskId.from( "someTask" );
        final Member member1 = mock( Member.class );
        final Member member2 = mock( Member.class );
        final TaskInfo taskInfo =
            TaskInfo.create().id( taskId ).name( "name" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build();

        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( member1, CompletableFuture.completedFuture( List.of( taskInfo ) ), member2,
                    CompletableFuture.completedFuture( List.of() ) ) );

        final TaskInfo taskInfoResult = clusteredTaskManager.getTaskInfo( taskId );

        assertEquals( taskInfoResult, taskInfo );
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
        final TaskInfo taskInfo1 =
            TaskInfo.create().id( taskId1 ).name( "name" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build();
        final TaskInfo taskInfo2 =
            TaskInfo.create().id( taskId2 ).name( "name" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build();

        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( member1, CompletableFuture.completedFuture( List.of( taskInfo1 ) ), member2,
                    CompletableFuture.completedFuture( List.of( taskInfo2 ) ) ) );

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
        final TaskInfo taskInfo1 =
            TaskInfo.create().id( taskId1 ).name( "name" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build();
        final TaskInfo taskInfo2 =
            TaskInfo.create().id( taskId2 ).name( "name" ).application( ApplicationKey.SYSTEM ).startTime( Instant.now() ).build();

        when( executorService.submitToAllMembers( any( TasksReporterCallable.class ) ) ).thenReturn(
            Map.of( member1, CompletableFuture.completedFuture( List.of( taskInfo1 ) ), member2,
                    CompletableFuture.completedFuture( List.of( taskInfo2 ) ) ) );

        final List<TaskInfo> taskInfos = clusteredTaskManager.getRunningTasks();

        assertNotNull( taskInfos );
        assertEquals( taskInfos.stream().map( TaskInfo::getId ).collect( Collectors.toSet() ), Set.of( taskId1, taskId2 ) );
    }

    @Test
    void submitTask()
        throws Exception
    {
        final DescribedTask task = mock( DescribedTask.class );

        final Future<Void> future = mock( Future.class );
        when( executorService.submit( any( OffloadedTaskCallable.class ), any( MemberSelector.class ) ) ).thenReturn( future );
        clusteredTaskManager.submitTask( task );
        verify( executorService ).submit( any( OffloadedTaskCallable.class ), any( MemberSelector.class ) );

        verify( future ).get( anyLong(), any( TimeUnit.class ) );
    }
}
