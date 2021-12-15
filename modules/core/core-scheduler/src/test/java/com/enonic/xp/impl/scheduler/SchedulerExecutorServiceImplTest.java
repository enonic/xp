package com.enonic.xp.impl.scheduler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchedulerExecutorServiceImplTest
{
    private LocalSystemScheduler localScheduler;

    private ClusteredSystemScheduler clusteredScheduler;

    private SchedulerExecutorServiceImpl service;

    @Mock(stubOnly = true)
    private HazelcastInstance hazelcastInstance;

    @Mock
    private IScheduledExecutorService hazelcastExecutorService;

    @Mock(stubOnly = true)
    private ClusterConfig clusterConfig;

    @BeforeEach
    public void setUp()
    {
        localScheduler = new LocalSystemScheduler();

        when( hazelcastInstance.getScheduledExecutorService( "scheduler" ) ).thenReturn( hazelcastExecutorService );
        clusteredScheduler = new ClusteredSystemScheduler( hazelcastInstance );
    }

    @Test
    void localSchedule()
        throws Exception
    {
        setLocal();

        final SchedulableTask task = mockTask( "task1" );

        service.schedule( task, 1, TimeUnit.SECONDS );

        assertEquals( 1, service.getAllFutures().size() );
        assertTrue( service.getAllFutures().contains( task.getName() ) );
        assertTrue( service.get( task.getName() ).isPresent() );
    }

    @Test
    void localAlreadyScheduled()
        throws Exception
    {
        setLocal();

        final SchedulableTask task = mockTask( "task1" );

        service.schedule( task, 1, TimeUnit.HOURS );
        assertThrows( IllegalStateException.class, () -> service.schedule( task, 1, TimeUnit.HOURS ) );
        assertThrows( IllegalStateException.class, () -> service.scheduleAtFixedRate( task, 0, 1, TimeUnit.HOURS ) );
    }

    @Test
    public void localAtFixedRate()
        throws Exception
    {
        setLocal();

        final SchedulableTask task = mockTask( "task1" );
        service.scheduleAtFixedRate( task, 0, 10, TimeUnit.MILLISECONDS );

        Thread.sleep( 50 );
        verify( task, atLeast( 4 ) ).run();
    }

    @Test
    public void localDispose()
        throws Exception
    {
        setLocal();

        final SchedulableTask task = mockTask( "task1" );

        final ScheduledFuture<?> future = service.schedule( task, 1, TimeUnit.SECONDS );

        assertFalse( future.isDone() );

        service.dispose( task.getName() );

        assertTrue( future.isDone() );
        assertTrue( service.getAllFutures().isEmpty() );
        assertTrue( service.get( task.getName() ).isEmpty() );
    }

    @Test
    public void localDisposeAllDone()
        throws Exception
    {
        setLocal();

        final SchedulableTask task1 = mockTask( "task1" );
        final SchedulableTask task2 = mockTask( "task2" );

        service.schedule( task1, 0, TimeUnit.MILLISECONDS );
        service.scheduleAtFixedRate( task2, 0, 10, TimeUnit.SECONDS );

        Thread.sleep( 100 );

        assertEquals( 2, service.getAllFutures().size() );
        assertTrue( service.get("task1").isPresent() );

        service.disposeAllDone();

        assertEquals( 1, service.getAllFutures().size() );
        assertTrue( service.get("task1").isEmpty() );
    }

    @Test
    void localCancelled()
        throws Exception
    {
        setLocal();

        final SchedulableTask task = mockTask( "task1" );
        final ScheduledFuture<?> future = service.scheduleAtFixedRate( task, 100, 10000, TimeUnit.MILLISECONDS );
        future.cancel( true );

        Thread.sleep( 200 );
        verify( task, never() ).run();
    }

    @Test
    public void localDeactivate()
        throws Exception
    {
        setLocal();

        final SchedulableTask task = mockTask( "task1" );

        localScheduler.deactivate();
        assertThrows( RejectedExecutionException.class, () -> service.schedule( task, 1, TimeUnit.MILLISECONDS ) );
    }

    @Test
    public void localExceptionDoesNotFailExecutor()
        throws Exception
    {
        setLocal();

        final SchedulableTask task1 = mockTask( "task1" );
        final SchedulableTask task2 = mockTask( "task2" );

        doThrow( NullPointerException.class ).when( task1 ).run();
        ScheduledFuture<?> future = service.schedule( task1, 1, TimeUnit.MILLISECONDS );

        assertThrows( ExecutionException.class, future::get );

        doThrow( Error.class ).when( task2 ).run();
        future = service.schedule( task2, 1, TimeUnit.MILLISECONDS );

        assertThrows( ExecutionException.class, future::get );
    }

    @Test
    void clusterSchedule()
        throws Exception
    {
        setCluster( 0 );

        final SchedulableTask task = mockTask( "task1" );

        service.schedule( task, 1, TimeUnit.SECONDS );

        verify( hazelcastExecutorService, times( 1 ) ).schedule( task, 1, TimeUnit.SECONDS );
    }

    @Test
    public void clusterAtFixedRate()
        throws Exception
    {
        setCluster( 0 );

        final SchedulableTask task = mockTask( "task1" );
        service.scheduleAtFixedRate( task, 0, 10, TimeUnit.MILLISECONDS );

        verify( hazelcastExecutorService, times( 1 ) ).scheduleAtFixedRate( task, 0, 10, TimeUnit.MILLISECONDS );
    }

    @Test
    public void clusterDispose()
        throws Exception
    {
        setCluster( 0 );

        final ScheduledTaskHandler handler = mock( ScheduledTaskHandler.class );

        final IScheduledFuture<?> future = mock( IScheduledFuture.class );
        when( future.getHandler() ).thenReturn( handler );
        when( handler.getTaskName() ).thenReturn( "task1" );
        when( future.isDone() ).thenReturn( false );

        final Map futures = new HashMap( Map.of( mock( Member.class ), List.of( future ) ) );

        when( hazelcastExecutorService.getAllScheduledFutures() ).thenReturn( futures );

        service.dispose( "task1" );

        verify( future, times( 1 ) ).dispose();

    }

    @Test
    public void clusterDisposeAllDone()
        throws Exception
    {
        setCluster( 0 );

        final ScheduledTaskHandler handler1 = mock( ScheduledTaskHandler.class );
        final ScheduledTaskHandler handler2 = mock( ScheduledTaskHandler.class );

        final IScheduledFuture<?> future1 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future2 = mock( IScheduledFuture.class );

        when( future1.getHandler() ).thenReturn( handler1 );
        when( future2.getHandler() ).thenReturn( handler2 );

        when( handler1.getTaskName() ).thenReturn( "task1" );
        when( handler2.getTaskName() ).thenReturn( "task2" );

        when( future1.isDone() ).thenReturn( false );
        when( future2.isDone() ).thenReturn( true );

        final Map futures = new HashMap( Map.of( mock( Member.class ), List.of( future1, future2 ) ) );

        when( hazelcastExecutorService.getAllScheduledFutures() ).thenReturn( futures );

        service.disposeAllDone();

        verify( future2, times( 1 ) ).dispose();
        verify( future1, times( 0 ) ).dispose();
    }

    @Test
    public void clusterGetAllFutures()
        throws Exception
    {
        setCluster( 0 );

        final ScheduledTaskHandler handler1 = mock( ScheduledTaskHandler.class );
        final ScheduledTaskHandler handler2 = mock( ScheduledTaskHandler.class );

        final IScheduledFuture<?> future1 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future2 = mock( IScheduledFuture.class );

        when( future1.getHandler() ).thenReturn( handler1 );
        when( future2.getHandler() ).thenReturn( handler2 );

        when( handler1.getTaskName() ).thenReturn( "task1" );
        when( handler2.getTaskName() ).thenReturn( "task2" );

        when( future1.isDone() ).thenReturn( false );
        when( future2.isDone() ).thenReturn( true );

        final Map futures = new HashMap( Map.of( mock( Member.class ), List.of( future1 ), mock( Member.class ), List.of( future2 ) ) );

        when( hazelcastExecutorService.getAllScheduledFutures() ).thenReturn( futures );

        final Set<String> futuresMap = service.getAllFutures();

        assertEquals( 2, futuresMap.size() );

        assertTrue( service.get( "task1" ).isPresent() );
        assertTrue( service.get( "task2" ).isPresent() );
    }

    @Test
    public void clusterUnset()
        throws Exception
    {
        setCluster( 0 );

        final SchedulableTask task = mockTask( "task1" );

        service.unsetClusteredScheduler( clusteredScheduler );
        assertThrows( RuntimeException.class, () -> service.schedule( task, 1, TimeUnit.MILLISECONDS ) );
    }

    @Test
    public void clusterTimeout()
        throws Exception
    {
        setCluster( 6000 );

        final SchedulableTask task = mockTask( "task1" );

        service.unsetClusteredScheduler( clusteredScheduler );
        assertThrows( RuntimeException.class, () -> service.schedule( task, 1, TimeUnit.MILLISECONDS ) );
    }

    private void setLocal()
    {
        when( clusterConfig.isEnabled() ).thenReturn( false );
        service = new SchedulerExecutorServiceImpl( localScheduler, clusterConfig );
    }

    private void setCluster( final long timeout )
        throws Exception
    {
        when( clusterConfig.isEnabled() ).thenReturn( true );
        service = new SchedulerExecutorServiceImpl( localScheduler, clusterConfig );

        if ( timeout > 0 )
        {
            Executors.newSingleThreadScheduledExecutor()
                .schedule( () -> service.setClusteredScheduler( clusteredScheduler ), timeout, TimeUnit.MILLISECONDS );
        }
        else
        {
            service.setClusteredScheduler( clusteredScheduler );
        }
    }

    private SchedulableTask mockTask( final String name )
    {
        final SchedulableTask task = mock( SchedulableTask.class );
        when( task.getName() ).thenReturn( name );

        return task;
    }
}
