package com.enonic.xp.impl.scheduler.distributed;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.Member;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.SchedulerName;
import com.enonic.xp.scheduler.SchedulerService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RescheduleTaskTest
{
    @Mock(stubOnly = true)
    ServiceReference<SchedulerService> serviceReference;

    @Captor
    ArgumentCaptor<SchedulableTask> taskCaptor;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private IScheduledExecutorService schedulerExecutorService;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock(stubOnly = true)
    private BundleContext bundleContext;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        when( hazelcastInstance.getScheduledExecutorService( isA( String.class ) ) ).thenReturn( schedulerExecutorService );

        final Bundle bundle = OsgiSupportMock.mockBundle();

        when( bundle.getBundleContext() ).thenReturn( bundleContext );
        when( bundleContext.getServiceReferences( SchedulerService.class, null ) ).thenReturn( List.of( serviceReference ) );
        when( bundleContext.getService( serviceReference ) ).thenReturn( schedulerService );
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

        when( schedulerExecutorService.schedule( isA( Runnable.class ), anyLong(), isA( TimeUnit.class ) ) ).
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
        task.setHazelcastInstance( hazelcastInstance );

        task.run();

        return task;
    }

    private void mockFutures()
    {
        final ScheduledTaskHandler handler4 = mock( ScheduledTaskHandler.class );

        final IScheduledFuture<?> future1 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future2 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future3 = mock( IScheduledFuture.class );
        final IScheduledFuture<?> future4 = mock( IScheduledFuture.class );

        when( future4.getHandler() ).thenReturn( handler4 );

        when( handler4.getTaskName() ).thenReturn( "task4" );

        when( future1.isDone() ).thenReturn( true );
        when( future2.isDone() ).thenReturn( true );
        when( future3.isDone() ).thenReturn( true );

        final Map futures = Map.of( mock( Member.class ), List.of( future1, future2 ), mock( Member.class ), List.of( future3, future4 ) );
        when( schedulerExecutorService.getAllScheduledFutures() ).
            thenReturn( futures );

    }

    private void mockJobs()
    {
        final ScheduledJob job1 = ScheduledJob.create().
            name( SchedulerName.from( "task1" ) ).
            calendar( CronCalendar.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getDefault() ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task1" ) ).
            payload( new PropertyTree() ).
            enabled( false ).
            build();

        final ScheduledJob job2 = ScheduledJob.create().
            name( SchedulerName.from( "task2" ) ).
            calendar( CronCalendar.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getDefault() ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task2" ) ).
            payload( new PropertyTree() ).
            enabled( true ).
            build();

        final ScheduledJob job3 = ScheduledJob.create().
            name( SchedulerName.from( "task3" ) ).
            calendar( OneTimeCalendar.create().
                value( Instant.now().minus( Duration.of( 1, ChronoUnit.SECONDS ) ) ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task3" ) ).
            payload( new PropertyTree() ).
            enabled( true ).
            build();

        final ScheduledJob job4 = ScheduledJob.create().
            name( SchedulerName.from( "task4" ) ).
            calendar( CronCalendar.create().
                value( "* * * * *" ).
                timeZone( TimeZone.getDefault() ).
                build() ).
            descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), "task4" ) ).
            payload( new PropertyTree() ).
            enabled( true ).
            build();

        when( schedulerService.list() ).
            thenReturn( List.of( job1, job2, job3, job4 ) );
    }

}
