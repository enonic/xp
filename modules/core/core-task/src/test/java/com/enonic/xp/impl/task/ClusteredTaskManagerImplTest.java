package com.enonic.xp.impl.task;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.Member;

import com.enonic.xp.impl.task.distributed.TasksReporterCallable;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusteredTaskManagerImplTest
{
    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IExecutorService executorService;

    private ClusteredTaskManagerImpl clusteredTaskManager;

    @BeforeEach
    void setUp()
    {
        clusteredTaskManager = new ClusteredTaskManagerImpl( hazelcastInstance );
        when( hazelcastInstance.getExecutorService( ClusteredTaskManagerImpl.ACTION ) ).thenReturn( executorService );
        clusteredTaskManager.activate();
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
}