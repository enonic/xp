package com.enonic.xp.impl.scheduler.distributed;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.TimeZone;

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

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.core.internal.osgi.OsgiSupportMock;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.impl.scheduler.ScheduledJobPropertyNames;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RescheduleTaskTest
{
    @Mock(stubOnly = true)
    ServiceReference<TaskService> taskReference;

    @Mock(stubOnly = true)
    ServiceReference<NodeService> nodeReference;

    @Mock(stubOnly = true)
    ServiceReference<SchedulerService> schedulerReference;

    @Mock(stubOnly = true)
    ServiceReference<SecurityService> securityReference;

    @Captor
    ArgumentCaptor<SubmitTaskParams> taskCaptor;

    @Captor
    ArgumentCaptor<AuthenticationToken> tokenCaptor;

    @Mock
    private SchedulerService schedulerService;

    @Mock
    private TaskService taskService;

    @Mock
    private NodeService nodeService;

    @Mock
    private SecurityService securityService;

    @Mock
    private BundleContext bundleContext;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        RescheduleTask.clock = Clock.fixed( Instant.now(), ZoneOffset.UTC );

        final Bundle bundle = OsgiSupportMock.mockBundle();

        when( bundle.getBundleContext() ).thenReturn( bundleContext );

        when( bundleContext.getServiceReferences( SchedulerService.class, null ) ).thenReturn( List.of( schedulerReference ) );
        when( bundleContext.getServiceReferences( TaskService.class, null ) ).thenReturn( List.of( taskReference ) );
        when( bundleContext.getServiceReferences( NodeService.class, null ) ).thenReturn( List.of( nodeReference ) );
        when( bundleContext.getServiceReferences( SecurityService.class, null ) ).thenReturn( List.of( securityReference ) );

        when( bundleContext.getService( nodeReference ) ).thenReturn( nodeService );
        when( bundleContext.getService( taskReference ) ).thenReturn( taskService );
        when( bundleContext.getService( schedulerReference ) ).thenReturn( schedulerService );
        when( bundleContext.getService( securityReference ) ).thenReturn( securityService );
    }

    @AfterEach
    void tearDown()
    {
        OsgiSupportMock.reset();
    }

    @Test
    public void submitOldOneTimeTask()
    {
        mockJobs();
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "123" ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        createAndRunTask();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "task3", taskCaptor.getValue().getDescriptorKey().getName() );
    }


    @Test
    public void submitInOrder()
    {
        final Instant now = RescheduleTask.clock.instant();

        final ScheduledJob job1 = mockOneTimeJob( "job-1", now.minus( 2, ChronoUnit.SECONDS ) );
        final ScheduledJob job2 = mockOneTimeJob( "job-2", now );
        final ScheduledJob job3 = mockOneTimeJob( "job-3", now.minus( 1, ChronoUnit.SECONDS ) );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2, job3 ) );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) )
            .thenReturn( TaskId.from( "2" ) )
            .thenReturn( TaskId.from( "3" ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        createAndRunTask();

        verify( taskService, times( 3 ) ).submitTask( taskCaptor.capture() );

        assertEquals( "job-1", taskCaptor.getAllValues().get( 0 ).getDescriptorKey().getName() );
        assertEquals( "job-3", taskCaptor.getAllValues().get( 1 ).getDescriptorKey().getName() );
        assertEquals( "job-2", taskCaptor.getAllValues().get( 2 ).getDescriptorKey().getName() );
    }

    @Test
    public void jobSubmitFailedButRetried()
    {
        final Instant now = RescheduleTask.clock.instant();

        ScheduledJob job1 = mockOneTimeJob( "job1", now.minus( 1, ChronoUnit.SECONDS ) );
        ScheduledJob job2 = mockOneTimeJob( "job2", now );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( RuntimeException.class )
            .thenReturn( TaskId.from( "1" ) );

        createAndRunTask();

        verify( taskService, times( 2 ) ).submitTask( taskCaptor.capture() );

        assertEquals( "job1", taskCaptor.getAllValues().get( 0 ).getDescriptorKey().getName() );
        assertEquals( "job2", taskCaptor.getAllValues().get( 1 ).getDescriptorKey().getName() );

        job1 = mockOneTimeJob( "job1", now.minus( 1, ChronoUnit.SECONDS ), now );
        job2 = mockOneTimeJob( "job2", now, now );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        createAndRunTask();

        verify( taskService, times( 3 ) ).submitTask( taskCaptor.capture() );

        assertEquals( "job1", taskCaptor.getAllValues().get( 2 ).getDescriptorKey().getName() );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new Error() );

        createAndRunTask();
        verify( taskService, times( 3 ) ).submitTask( taskCaptor.capture() );
    }

    @Test
    public void jobSubmitFailedWithError()
    {
        final Instant now = RescheduleTask.clock.instant();
        ScheduledJob job1 = mockOneTimeJob( "job1", now.minus( 1, ChronoUnit.SECONDS ) );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new Error() ).thenReturn( TaskId.from( "1" ) );

        createAndRunTask();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );

        job1 = mockOneTimeJob( "job1", now.minus( 1, ChronoUnit.SECONDS ), now );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );
        createAndRunTask();
        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
    }

    @Test
    public void retryFailedMultipleTimes()
    {
        final Instant now = Instant.now();
        ScheduledJob job1 = mockOneTimeJob( "job1", now.minus( 1, ChronoUnit.SECONDS ) );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new RuntimeException() );

        for ( int i = 0; i <= 10; i++ )
        {
            createAndRunTask();
        }
        verify( taskService, times( 11 ) ).submitTask( taskCaptor.capture() );
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    public void submitJobAsUser()
    {
        final Instant now = Instant.now();
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.createDefault(), "my-user" );

        ScheduledJob job1 = mockOneTimeJob( "job1", now.minus( 1, ChronoUnit.SECONDS ), user );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );
        when( securityService.authenticate( tokenCaptor.capture() ) ).thenReturn( mock( AuthenticationInfo.class ) );

        createAndRunTask();

        assertEquals( "default", tokenCaptor.getValue().getIdProvider().toString() );
        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
    }

    @Test
    public void submitCronJob()
    {
        ScheduledJob job1 = mockCronJob( "job1", "* * * * *", Instant.parse( "2021-02-26T10:44:33.170079900Z" ) );
        ScheduledJob job2 = mockCronJob( "job2", "* * * * *", Instant.now() );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        final Node node = mockNode();
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( node );

        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) ).thenReturn( TaskId.from( "2" ) );
        when( securityService.authenticate( tokenCaptor.capture() ) ).thenReturn( mock( AuthenticationInfo.class ) );

        createAndRunTask();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    public void jobWasRemoved()
    {
        final Instant plus = null;

        ScheduledJob job1 = mockCronJob( "job1", "* * * * *", plus );
        ScheduledJob job2 = mockCronJob( "job2", "* * * * *", plus );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        createAndRunTask();

        RescheduleTask.clock = Clock.offset( RescheduleTask.clock, Duration.of( 61, ChronoUnit.SECONDS ) );

        when( schedulerService.list() ).thenReturn( List.of( job2 ) );

        createAndRunTask();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job2", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    public void jobWasModified()
    {
        final Instant plus = null;

        ScheduledJob job = mockCronJob( "job1", "1 1 1 1 1", plus );

        when( schedulerService.list() ).thenReturn( List.of( job ) );

        createAndRunTask();

        ScheduledJob modified = mockCronJob( "job1", "* * * * *", plus );
        when( schedulerService.list() ).thenReturn( List.of( modified ) );

        createAndRunTask();

        RescheduleTask.clock = Clock.offset( RescheduleTask.clock, Duration.of( 61, ChronoUnit.SECONDS ) );

        createAndRunTask();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getValue().getDescriptorKey().getName() );
    }


    @Test
    public void testName()
    {
        assertEquals( "rescheduleTask", new RescheduleTask().getName() );
    }

    @Test
    public void serviceIsDown()
        throws Exception
    {
        when( bundleContext.getServiceReferences( SchedulerService.class, null ) ).thenReturn( List.of() );

        mockJobs();

        for ( int i = 0; i < 10; i++ )
        {
            createAndRunTask();
        }
        verify( bundleContext, times( 10 ) ).getServiceReferences( SchedulerService.class, null );
    }

    private RescheduleTask createAndRunTask()
    {
        final RescheduleTask task = new RescheduleTask();

        task.run();

        return task;
    }

    private void mockJobs()
    {
        final ScheduledJob job1 = mockCronJob( "task1", "* * * * *" );

        final ScheduledJob job2 = mockCronJob( "task2", "* * * * *" );

        final ScheduledJob job3 = mockOneTimeJob( "task3", Instant.now().minus( Duration.of( 1, ChronoUnit.SECONDS ) ) );

        final ScheduledJob job4 = mockCronJob( "task4", "* * * * *" );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2, job3, job4 ) );
    }

    private ScheduledJob mockOneTimeJob( final String scheduledJobName, final Instant instant )
    {
        return mockOneTimeJob( scheduledJobName, instant, null, null );
    }

    private ScheduledJob mockOneTimeJob( final String scheduledJobName, final Instant instant, final Instant lastRun )
    {
        return mockOneTimeJob( scheduledJobName, instant, lastRun, null );
    }

    private ScheduledJob mockOneTimeJob( final String scheduledJobName, final Instant instant, final PrincipalKey user )
    {
        return mockOneTimeJob( scheduledJobName, instant, null, user );
    }

    private ScheduledJob mockOneTimeJob( final String scheduledJobName, final Instant instant, final Instant lastRun,
                                         final PrincipalKey user )
    {
        return ScheduledJob.create()
            .name( ScheduledJobName.from( scheduledJobName ) )
            .calendar( OneTimeCalendarImpl.create().value( instant ).build() )
            .descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), scheduledJobName ) )
            .config( new PropertyTree() )
            .enabled( true )
            .creator( PrincipalKey.from( "user:system:creator" ) )
            .modifier( PrincipalKey.from( "user:system:creator" ) )
            .createdTime( Instant.parse( "2021-02-26T10:44:33.170079900Z" ) )
            .modifiedTime( Instant.parse( "2021-02-26T10:44:33.170079900Z" ) )
            .lastRun( lastRun )
            .user( user )
            .build();
    }

    private ScheduledJob mockCronJob( final String scheduledJobName, final String cron )
    {
        return mockCronJob( scheduledJobName, cron, null );
    }

    private ScheduledJob mockCronJob( final String scheduledJobName, final String cron, final Instant lastRun )
    {
        return ScheduledJob.create()
            .name( ScheduledJobName.from( scheduledJobName ) )
            .calendar( CronCalendarImpl.create().value( cron ).timeZone( TimeZone.getDefault() ).build() )
            .descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), scheduledJobName ) )
            .config( new PropertyTree() )
            .enabled( true )
            .creator( PrincipalKey.from( "user:system:creator" ) )
            .modifier( PrincipalKey.from( "user:system:modifier" ) )
            .createdTime( Instant.parse( "2021-02-25T10:44:33.170079900Z" ) )
            .modifiedTime( Instant.now() )
            .lastRun( lastRun )
            .build();
    }

    private Node mockNode()
    {
        final PropertyTree jobData = new PropertyTree();

        final PropertySet calendar = new PropertySet();
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TYPE, "ONE_TIME" );
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, "2021-02-25T10:44:33.170079900Z" );

        jobData.addString( ScheduledJobPropertyNames.DESCRIPTOR, "app:key" );
        jobData.addBoolean( ScheduledJobPropertyNames.ENABLED, true );
        jobData.addSet( ScheduledJobPropertyNames.CALENDAR, calendar );
        jobData.addSet( ScheduledJobPropertyNames.CONFIG, new PropertySet() );
        jobData.setString( ScheduledJobPropertyNames.CREATOR, "user:system:creator" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIER, "user:system:modifier" );
        jobData.setString( ScheduledJobPropertyNames.CREATED_TIME, "2021-02-26T10:44:33.170079900Z" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIED_TIME, "2021-03-26T10:44:33.170079900Z" );

        return Node.create().id( NodeId.from( "abc" ) ).name( "test" ).parentPath( NodePath.ROOT ).data( jobData ).build();

    }

}
