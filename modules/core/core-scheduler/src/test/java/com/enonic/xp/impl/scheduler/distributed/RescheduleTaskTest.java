package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.SchedulerExecutorService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RescheduleTaskTest
{
    @Mock(stubOnly = true)
    ServiceReference<SchedulerService> serviceReference;

    @Mock(stubOnly = true)
    ServiceReference<SchedulerExecutorService> executorReference;

    @Captor
    ArgumentCaptor<SchedulableTaskImpl> taskCaptor;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private SchedulerExecutorService schedulerExecutorService;

    @Mock(stubOnly = true)
    private BundleContext bundleContext;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        final Bundle bundle = OsgiSupportMock.mockBundle();

        when( bundle.getBundleContext() ).thenReturn( bundleContext );

        when( bundleContext.getServiceReferences( SchedulerService.class, null ) ).thenReturn( List.of( serviceReference ) );
        when( bundleContext.getServiceReferences( SchedulerExecutorService.class, null ) ).thenReturn( List.of( executorReference ) );

        when( bundleContext.getService( serviceReference ) ).thenReturn( schedulerService );
        when( bundleContext.getService( executorReference ) ).thenReturn( schedulerExecutorService );
    }

    @AfterEach
    void tearDown()
    {
        OsgiSupportMock.reset();
    }

    @Test
    public void rescheduleWithDoneAndDisabledTasks()
    {
        mockFutures();
        mockJobs();

        final RescheduleTask task = createAndRunTask();
        assertEquals( "rescheduleTask", task.getName() );

        verify( schedulerExecutorService, times( 2 ) ).schedule( taskCaptor.capture(), anyLong(), isA( TimeUnit.class ) );

        assertEquals( 2, taskCaptor.getAllValues().size() );
        assertEquals( "task2", taskCaptor.getAllValues().get( 0 ).getName() );
        assertEquals( "task3", taskCaptor.getAllValues().get( 1 ).getName() );
    }

    @Test
    public void firstJobRescheduleFailedButSecondIsOk()
    {
        mockFutures();
        mockJobs();

        when( schedulerExecutorService.schedule( isA( SchedulableTask.class ), anyLong(), isA( TimeUnit.class ) ) ).
            thenThrow( RuntimeException.class ).
            then( a -> null );

        createAndRunTask();

        verify( schedulerExecutorService, times( 2 ) ).schedule( taskCaptor.capture(), anyLong(), isA( TimeUnit.class ) );

        assertEquals( 2, taskCaptor.getAllValues().size() );
        assertEquals( "task2", taskCaptor.getAllValues().get( 0 ).getName() );
        assertEquals( "task3", taskCaptor.getAllValues().get( 1 ).getName() );
    }

    private RescheduleTask createAndRunTask()
    {
        final RescheduleTask task = new RescheduleTask();

        task.run();

        return task;
    }

    private void mockFutures()
    {
        final ScheduledTaskHandler handler1 = mock( ScheduledTaskHandler.class );
        final ScheduledTaskHandler handler2 = mock( ScheduledTaskHandler.class );
        final ScheduledTaskHandler handler3 = mock( ScheduledTaskHandler.class );
        final ScheduledTaskHandler handler4 = mock( ScheduledTaskHandler.class );

        final IScheduledFuture<?> future1 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future2 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future3 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future4 = mock( IScheduledFuture.class );

        when( future1.getHandler() ).thenReturn( handler1 );
        when( future2.getHandler() ).thenReturn( handler2 );
        when( future3.getHandler() ).thenReturn( handler3 );
        when( future4.getHandler() ).thenReturn( handler4 );

        when( handler1.getTaskName() ).thenReturn( "task1" );
        when( handler2.getTaskName() ).thenReturn( "task2" );
        when( handler3.getTaskName() ).thenReturn( "task3" );
        when( handler4.getTaskName() ).thenReturn( "task4" );

        when( future1.isDone() ).thenReturn( true );
        when( future2.isDone() ).thenReturn( true );
        when( future3.isDone() ).thenReturn( true );

        Map<String, ScheduledFuture<?>> futures = new HashMap<>(
            Map.of( handler1.getTaskName(), future1, handler2.getTaskName(), future2, handler3.getTaskName(), future3,
                    handler4.getTaskName(), future4 ) );

        when( schedulerExecutorService.getAllFutures() ).
            thenAnswer( invocation -> futures.keySet() );

        doAnswer( invocation -> {
            final Set<String> doneTasks = futures.entrySet().stream().
                filter( entry -> entry.getValue().isDone() ).
                map( Map.Entry::getKey ).
                collect( Collectors.toSet() );

            doneTasks.forEach( futures::remove );

            return doneTasks;
        } ).when( schedulerExecutorService ).disposeAllDone();

        doAnswer( invocation -> {

            final ScheduledFuture<?> future = futures.remove( (String) invocation.getArgument( 0 ) );
            return future != null;
        } ).when( schedulerExecutorService ).dispose( isA( String.class ) );

    }

    private void mockJobs()
    {
        final ScheduledJob job1 = ScheduledJob.create().
            name( ScheduledJobName.from( "task1" ) ).
            calendar( CronCalendarImpl.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getDefault() ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            config( new PropertyTree() ).
            enabled( false ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            build();

        final ScheduledJob job2 = ScheduledJob.create().
            name( ScheduledJobName.from( "task2" ) ).
            calendar( CronCalendarImpl.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getDefault() ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task2" ) ).
            config( new PropertyTree() ).
            enabled( true ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:modifier" ) ).
            createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-02-25T10:44:53.170079900Z" ) ).
            build();

        final ScheduledJob job3 = ScheduledJob.create().
            name( ScheduledJobName.from( "task3" ) ).
            calendar( OneTimeCalendarImpl.create().
                value( Instant.now().minus( Duration.of( 1, ChronoUnit.SECONDS ) ) ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task3" ) ).
            config( new PropertyTree() ).
            enabled( true ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:creator" ) ).
            createdTime( Instant.parse( "2021-02-26T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-02-26T10:44:33.170079900Z" ) ).
            build();

        final ScheduledJob job4 = ScheduledJob.create().
            name( ScheduledJobName.from( "task4" ) ).
            calendar( CronCalendarImpl.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getDefault() ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task4" ) ).
            config( new PropertyTree() ).
            creator( PrincipalKey.from( "user:system:creator" ) ).
            modifier( PrincipalKey.from( "user:system:modifier" ) ).
            createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) ).
            modifiedTime( Instant.parse( "2021-02-25T11:44:33.170079900Z" ) ).
            enabled( true ).
            build();

        when( schedulerService.list() ).
            thenReturn( List.of( job1, job2, job3, job4 ) );
    }

}
