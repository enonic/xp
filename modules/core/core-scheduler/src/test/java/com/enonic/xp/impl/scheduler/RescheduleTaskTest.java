package com.enonic.xp.impl.scheduler;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.impl.scheduler.distributed.CronCalendarImpl;
import com.enonic.xp.impl.scheduler.distributed.OneTimeCalendarImpl;
import com.enonic.xp.node.ApplyVersionAttributesParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RescheduleTaskTest
{
    private static final Instant NOW = Instant.parse( "2026-01-01T10:30:30Z" );

    private static final TimeZone UTC = TimeZone.getTimeZone( "UTC" );

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
    private ClusterService clusterService;

    private SchedulingCoordinator schedulingCoordinator;

    private MutableClock clock;

    private RescheduleTask task;

    @BeforeEach
    void setUp()
    {
        clock = new MutableClock( NOW );
        schedulingCoordinator = new SchedulingCoordinatorImpl( mock( ClusterConfig.class ) );
        task = new RescheduleTask( schedulerService, nodeService, taskService, securityService, clusterService, schedulingCoordinator,
                                   clock );

        when( clusterService.isLeader() ).thenReturn( true );
        when( nodeService.getByPath( isA( NodePath.class ) ) ).thenReturn( mockNode() );
    }

    @Test
    void submitOldOneTimeTask()
    {
        mockJobs();
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "123" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "task3", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void submitInOrder()
    {
        final ScheduledJob job1 = oneTimeJob( "job-1", NOW.minusSeconds( 2 ) );
        final ScheduledJob job2 = oneTimeJob( "job-2", NOW );
        final ScheduledJob job3 = oneTimeJob( "job-3", NOW.minusSeconds( 1 ) );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2, job3 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) )
            .thenReturn( TaskId.from( "2" ) )
            .thenReturn( TaskId.from( "3" ) );

        task.run();

        verify( taskService, times( 3 ) ).submitTask( taskCaptor.capture() );

        assertEquals( "job-1", taskCaptor.getAllValues().get( 0 ).getDescriptorKey().getName() );
        assertEquals( "job-3", taskCaptor.getAllValues().get( 1 ).getDescriptorKey().getName() );
        assertEquals( "job-2", taskCaptor.getAllValues().get( 2 ).getDescriptorKey().getName() );
    }

    @Test
    void jobSubmitFailedButRetried()
    {
        final ScheduledJob job1 = oneTimeJob( "job1", NOW.minusSeconds( 1 ) );
        final ScheduledJob job2 = oneTimeJob( "job2", NOW );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( RuntimeException.class )
            .thenReturn( TaskId.from( "1" ) )
            .thenReturn( TaskId.from( "2" ) );

        task.run();

        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );

        // job2 has run; job1 is still due and is retried on the next tick
        when( schedulerService.list() ).thenReturn( List.of( job1, oneTimeJob( "job2", NOW, NOW ) ) );

        task.run();

        verify( taskService, times( 3 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getAllValues().get( 0 ).getDescriptorKey().getName() );
        assertEquals( "job2", taskCaptor.getAllValues().get( 1 ).getDescriptorKey().getName() );
        assertEquals( "job1", taskCaptor.getAllValues().get( 2 ).getDescriptorKey().getName() );
    }

    @Test
    void jobSubmitFailedWithError()
    {
        final ScheduledJob job1 = oneTimeJob( "job1", NOW.minusSeconds( 1 ) );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new Error() ).thenReturn( TaskId.from( "1" ) );

        task.run();

        // the run is recorded right away, without retries
        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( nodeService, times( 1 ) ).applyVersionAttributes( isA( ApplyVersionAttributesParams.class ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void retryFailedMultipleTimes()
    {
        final ScheduledJob job1 = oneTimeJob( "job1", NOW.minusSeconds( 1 ) );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new RuntimeException() );

        for ( int i = 0; i <= 10; i++ )
        {
            task.run();
        }

        verify( taskService, times( 11 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( nodeService, times( 1 ) ).applyVersionAttributes( isA( ApplyVersionAttributesParams.class ) );

        // the give-up is recorded - no further attempts even though the job list still reports no lastRun
        task.run();
        verify( taskService, times( 11 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void submitJobAsUser()
    {
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.system(), "my-user" );
        final ScheduledJob job1 = oneTimeJob( "job1", NOW.minusSeconds( 1 ), null, user );

        when( schedulerService.list() ).thenReturn( List.of( job1 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );
        when( securityService.authenticate( tokenCaptor.capture() ) ).thenReturn( mock( AuthenticationInfo.class ) );

        task.run();

        assertEquals( "system", tokenCaptor.getValue().getIdProvider().toString() );
        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void submitCronJob()
    {
        final ScheduledJob job1 = cronJob( "job1", "* * * * *", Instant.parse( "2021-02-26T10:44:33.170079900Z" ) );
        final ScheduledJob job2 = cronJob( "job2", "* * * * *", NOW );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void jobWasRemoved()
    {
        final ScheduledJob job1 = cronJob( "job1", "* * * * *", null );
        final ScheduledJob job2 = cronJob( "job2", "* * * * *", null );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2 ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        clock.plusSeconds( 61 );
        when( schedulerService.list() ).thenReturn( List.of( job2 ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job2", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void jobWasModified()
    {
        final ScheduledJob job = cronJob( "job1", "1 1 1 1 1", null );

        when( schedulerService.list() ).thenReturn( List.of( job ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        // modify clears lastRun and bumps modifiedTime; the coordinator forgets the planned run
        schedulingCoordinator.forget( job.getName() );
        when( schedulerService.list() ).thenReturn( List.of( cronJob( "job1", "* * * * *", null ) ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        clock.plusSeconds( 61 );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void notLeaderDoesNotSchedule()
    {
        when( clusterService.isLeader() ).thenReturn( false );

        task.run();

        verify( schedulerService, never() ).list();
    }

    @Test
    void cronJobNotSubmittedTwiceInSameSecond()
    {
        // models cron-utils returning an execution time that is not strictly after the input (#10854)
        final Instant now = Instant.parse( "2026-01-01T10:30:59.500Z" );
        final Instant modifiedTime = Instant.parse( "2026-01-01T10:29:00Z" );
        final Instant nextMinute = Instant.parse( "2026-01-01T10:31:00Z" );
        clock.set( now );

        final CronCalendar calendar = mock( CronCalendar.class );
        when( calendar.getType() ).thenReturn( ScheduleCalendarType.CRON );
        when( calendar.nextExecution( any( Instant.class ) ) ).thenAnswer( invocation -> {
            final Instant from = invocation.getArgument( 0 );
            return Optional.of( from.equals( now ) || from.equals( modifiedTime ) ? now : nextMinute );
        } );

        final ScheduledJob job = jobBuilder( "job1", calendar ).modifiedTime( modifiedTime ).build();

        when( schedulerService.list() ).thenReturn( List.of( job ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();
        clock.plusMillis( 1 );
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        assertEquals( nextMinute, schedulingCoordinator.nextRun( job.getName() ) );
    }

    @Test
    void missedCronRunCaughtUpOnStart()
    {
        clock.set( Instant.parse( "2026-01-01T11:00:40Z" ) );

        final ScheduledJob ranBefore = cronJob( "ran-before", "0 * * * *", Instant.parse( "2026-01-01T10:15:00Z" ) );
        final ScheduledJob neverRan =
            jobBuilder( "never-ran", cronCalendar( "0 * * * *" ) ).modifiedTime( Instant.parse( "2026-01-01T10:30:00Z" ) ).build();

        when( schedulerService.list() ).thenReturn( List.of( ranBefore, neverRan ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void neverRunCronJobNotDueBeforeFirstSlot()
    {
        clock.set( Instant.parse( "2026-01-01T10:45:00Z" ) );

        final ScheduledJob neverRan =
            jobBuilder( "never-ran", cronCalendar( "0 * * * *" ) ).modifiedTime( Instant.parse( "2026-01-01T10:30:00Z" ) ).build();

        when( schedulerService.list() ).thenReturn( List.of( neverRan ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void oneTimeJobNotResubmittedWhenRecordingRunFails()
    {
        final ScheduledJob job = oneTimeJob( "job1", NOW.minusSeconds( 1 ) );

        when( schedulerService.list() ).thenReturn( List.of( job ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );
        when( nodeService.applyVersionAttributes( isA( ApplyVersionAttributesParams.class ) ) ).thenThrow( new RuntimeException() );

        task.run();
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void disabledJobNotSubmitted()
    {
        final ScheduledJob job = jobBuilder( "job1", OneTimeCalendarImpl.create().value( NOW.minusSeconds( 1 ) ).build() )
            .enabled( false )
            .build();

        when( schedulerService.list() ).thenReturn( List.of( job ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void nextExecutionAfterIsStrictlyAfter()
    {
        assertEquals( Instant.parse( "2026-01-01T10:31:00Z" ),
                      RescheduleTask.nextExecutionAfter( cronCalendar( "* * * * *" ), Instant.parse( "2026-01-01T10:30:30Z" ) ) );

        assertTrue( RescheduleTask.nextExecutionAfter( cronCalendar( "* * * * *" ), Instant.parse( "2026-01-01T10:30:59.999Z" ) )
                        .isAfter( Instant.parse( "2026-01-01T10:30:59.999Z" ) ) );

        final Instant now = Instant.parse( "2026-01-01T10:30:59.500Z" );
        final ScheduleCalendar quirky = mock( ScheduleCalendar.class );
        when( quirky.nextExecution( any( Instant.class ) ) ).thenAnswer( invocation -> {
            final Instant from = invocation.getArgument( 0 );
            return Optional.of( from.equals( now ) ? now : Instant.parse( "2026-01-01T10:31:00Z" ) );
        } );

        assertEquals( Instant.parse( "2026-01-01T10:31:00Z" ), RescheduleTask.nextExecutionAfter( quirky, now ) );
    }

    @Test
    void schedulerServiceFailureDoesNotStopTicking()
    {
        when( schedulerService.list() ).thenThrow( new RuntimeException() );

        for ( int i = 0; i < 10; i++ )
        {
            task.run();
        }

        verify( schedulerService, times( 10 ) ).list();
    }

    private void mockJobs()
    {
        final ScheduledJob job1 = cronJob( "task1", "* * * * *", null );
        final ScheduledJob job2 = cronJob( "task2", "* * * * *", null );
        final ScheduledJob job3 = oneTimeJob( "task3", NOW.minusSeconds( 1 ) );
        final ScheduledJob job4 = cronJob( "task4", "* * * * *", null );

        when( schedulerService.list() ).thenReturn( List.of( job1, job2, job3, job4 ) );
    }

    private ScheduledJob oneTimeJob( final String name, final Instant value )
    {
        return oneTimeJob( name, value, null, null );
    }

    private ScheduledJob oneTimeJob( final String name, final Instant value, final Instant lastRun )
    {
        return oneTimeJob( name, value, lastRun, null );
    }

    private ScheduledJob oneTimeJob( final String name, final Instant value, final Instant lastRun, final PrincipalKey user )
    {
        return jobBuilder( name, OneTimeCalendarImpl.create().value( value ).build() ).lastRun( lastRun ).user( user ).build();
    }

    private ScheduledJob cronJob( final String name, final String cron, final Instant lastRun )
    {
        return jobBuilder( name, cronCalendar( cron ) ).lastRun( lastRun ).build();
    }

    private static CronCalendarImpl cronCalendar( final String cron )
    {
        return CronCalendarImpl.create().value( cron ).timeZone( UTC ).build();
    }

    private ScheduledJob.Builder jobBuilder( final String name, final ScheduleCalendar calendar )
    {
        return ScheduledJob.create()
            .name( ScheduledJobName.from( name ) )
            .calendar( calendar )
            .descriptor( DescriptorKey.from( ApplicationKey.from( "com.enonic.app.test" ), name ) )
            .config( new PropertyTree() )
            .enabled( true )
            .creator( PrincipalKey.from( "user:system:creator" ) )
            .modifier( PrincipalKey.from( "user:system:creator" ) )
            .createdTime( NOW )
            .modifiedTime( NOW );
    }

    private Node mockNode()
    {
        final PropertyTree jobData = new PropertyTree();

        final PropertySet calendar = jobData.newSet();
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TYPE, "ONE_TIME" );
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, "2021-02-25T10:44:33.170079900Z" );

        jobData.addString( ScheduledJobPropertyNames.DESCRIPTOR, "app:key" );
        jobData.addBoolean( ScheduledJobPropertyNames.ENABLED, true );
        jobData.addSet( ScheduledJobPropertyNames.CALENDAR, calendar );
        jobData.addSet( ScheduledJobPropertyNames.CONFIG, jobData.newSet() );
        jobData.setString( ScheduledJobPropertyNames.CREATOR, "user:system:creator" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIER, "user:system:modifier" );
        jobData.setString( ScheduledJobPropertyNames.CREATED_TIME, "2021-02-26T10:44:33.170079900Z" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIED_TIME, "2021-03-26T10:44:33.170079900Z" );

        return Node.create()
            .id( NodeId.from( "abc" ) )
            .name( "test" )
            .parentPath( NodePath.ROOT )
            .data( jobData )
            .nodeVersionId( new NodeVersionId() )
            .build();
    }

    private static final class MutableClock
        extends Clock
    {
        private Instant instant;

        MutableClock( final Instant instant )
        {
            this.instant = instant;
        }

        void set( final Instant instant )
        {
            this.instant = instant;
        }

        void plusSeconds( final long seconds )
        {
            this.instant = instant.plusSeconds( seconds );
        }

        void plusMillis( final long millis )
        {
            this.instant = instant.plusMillis( millis );
        }

        @Override
        public ZoneId getZone()
        {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone( final ZoneId zone )
        {
            return this;
        }

        @Override
        public Instant instant()
        {
            return instant;
        }
    }
}
