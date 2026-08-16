package com.enonic.xp.impl.scheduler;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ScheduleCalendar;
import com.enonic.xp.scheduler.ScheduleCalendarType;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.impl.scheduler.distributed.PlannedRun;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.security.auth.AuthenticationToken;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.impl.scheduler.distributed.FixedRateCalendarImpl;
import com.enonic.xp.task.SubmitTaskParams;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doThrow;
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
    private SchedulerServiceImpl schedulerService;

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
        schedulingCoordinator = new SchedulingCoordinator( mock( ClusterConfig.class ) );
        task = new RescheduleTask( schedulerService, nodeService, taskService, securityService, clusterService, schedulingCoordinator,
                                   clock );

        when( clusterService.isLeader() ).thenReturn( true );
        when( clusterService.inCluster( isA( ApplicationKey.class ) ) ).thenReturn( true );
        when( nodeService.getByPath( isA( NodePath.class ) ) ).thenReturn( mockNode() );
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenReturn( mockNode() );
    }

    private void mockJobs( final ScheduledJob... jobs )
    {
        final List<ScheduledJobEntry> entries =
            Stream.of( jobs ).map( job -> new ScheduledJobEntry( job, new NodeVersionId() ) ).collect( Collectors.toList() );
        mockEntries( entries );
    }

    private void mockEntries( final List<ScheduledJobEntry> entries )
    {
        when( schedulerService.listEntries() ).thenReturn( entries );
        for ( final ScheduledJobEntry entry : entries )
        {
            when( schedulerService.get( entry.job().getName() ) ).thenReturn( entry.job() );
            when( schedulerService.versionId( entry.job().getName() ) ).thenReturn( entry.versionId() );
            // the stored node is at the version the tick listed, so a run of it is recorded
            when( nodeService.getByPath( new NodePath( NodePath.ROOT, NodeName.from( entry.job().getName().getValue() ) ) ) ).thenReturn(
                mockNode( entry.versionId() ) );
        }
    }

    @Test
    void submitOldOneTimeTask()
    {
        mockJobs( cronJob( "task1", "* * * * *", null ), cronJob( "task2", "* * * * *", null ),
                  oneTimeJob( "task3", NOW.minusSeconds( 1 ) ), cronJob( "task4", "* * * * *", null ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "123" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "task3", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void submitInOrder()
    {
        mockJobs( oneTimeJob( "job-1", NOW.minusSeconds( 2 ) ), oneTimeJob( "job-2", NOW ), oneTimeJob( "job-3", NOW.minusSeconds( 1 ) ) );
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
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ), oneTimeJob( "job2", NOW ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( RuntimeException.class )
            .thenReturn( TaskId.from( "1" ) )
            .thenReturn( TaskId.from( "2" ) );

        task.run();

        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );

        // job2 has run and is marked in the coordinator; job1 is still due and is retried on the next tick
        task.run();

        verify( taskService, times( 3 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getAllValues().get( 0 ).getDescriptorKey().getName() );
        assertEquals( "job2", taskCaptor.getAllValues().get( 1 ).getDescriptorKey().getName() );
        assertEquals( "job1", taskCaptor.getAllValues().get( 2 ).getDescriptorKey().getName() );
    }

    @Test
    void jobSubmitFailedWithError()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new Error() ).thenReturn( TaskId.from( "1" ) );

        task.run();

        // the run is recorded right away, without retries
        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void retryFailedMultipleTimes()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new RuntimeException() );

        for ( int i = 0; i <= 10; i++ )
        {
            task.run();
        }

        verify( taskService, times( 11 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );

        // the give-up is recorded - no further attempts even though the job list still reports no lastRun
        task.run();
        verify( taskService, times( 11 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void submitJobAsUser()
    {
        final PrincipalKey user = PrincipalKey.ofUser( IdProviderKey.system(), "my-user" );

        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ), null, user ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );
        when( securityService.authenticate( tokenCaptor.capture() ) ).thenReturn( mock( AuthenticationInfo.class ) );

        task.run();

        assertEquals( "system", tokenCaptor.getValue().getIdProvider().toString() );
        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void submitCronJob()
    {
        mockJobs( cronJob( "job1", "* * * * *", Instant.parse( "2021-02-26T10:44:33.170079900Z" ) ),
                  cronJob( "job2", "* * * * *", NOW ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job1", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void jobWasRemoved()
    {
        mockJobs( cronJob( "job1", "* * * * *", null ), cronJob( "job2", "* * * * *", null ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        clock.plusSeconds( 61 );
        mockJobs( cronJob( "job2", "* * * * *", null ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( taskCaptor.capture() );
        assertEquals( "job2", taskCaptor.getValue().getDescriptorKey().getName() );
    }

    @Test
    void jobWasModified()
    {
        mockJobs( cronJob( "job1", "1 1 1 1 1", null ) );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        // modify clears lastRun and creates a new node version; the coordinator forgets the planned run
        schedulingCoordinator.forget( ScheduledJobName.from( "job1" ) );
        mockJobs( cronJob( "job1", "* * * * *", null ) );

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

        verify( schedulerService, never() ).listEntries();
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

        mockJobs( job );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();
        clock.plusMillis( 1 );
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        assertEquals( nextMinute, schedulingCoordinator.plannedRun( job.getName() ).nextRun() );
    }

    @Test
    void missedCronRunCaughtUpOnStart()
    {
        clock.set( Instant.parse( "2026-01-01T11:00:40Z" ) );

        final ScheduledJob ranBefore = cronJob( "ran-before", "0 * * * *", Instant.parse( "2026-01-01T10:15:00Z" ) );
        final ScheduledJob neverRan =
            jobBuilder( "never-ran", cronCalendar( "0 * * * *" ) ).modifiedTime( Instant.parse( "2026-01-01T10:30:00Z" ) ).build();

        mockJobs( ranBefore, neverRan );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void neverRunCronJobNotDueBeforeFirstSlot()
    {
        clock.set( Instant.parse( "2026-01-01T10:45:00Z" ) );

        mockJobs( jobBuilder( "never-ran", cronCalendar( "0 * * * *" ) ).modifiedTime( Instant.parse( "2026-01-01T10:30:00Z" ) ).build() );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void neverRunCronJobWithoutModifiedTimeNotDue()
    {
        mockJobs( jobBuilder( "job1", cronCalendar( "* * * * *" ) ).modifiedTime( null ).createdTime( null ).build() );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void oneTimeJobNotResubmittedWhenRecordingRunFails()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );
        when( nodeService.update( isA( UpdateNodeParams.class ) ) ).thenThrow( new RuntimeException() );

        task.run();
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void ephemeralOneTimeJobDeletedAfterRun()
    {
        mockJobs( jobBuilder( "job1", OneTimeCalendarImpl.create().value( NOW.minusSeconds( 1 ) ).deleteAfterRun( true ).build() ).build() );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( schedulerService, times( 1 ) ).delete( ScheduledJobName.from( "job1" ) );
        // deleted instead of leaving a tombstone behind
        verify( nodeService, never() ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void ephemeralOneTimeJobKeptWhenSubmissionNeverSucceeds()
    {
        mockJobs( jobBuilder( "job1", OneTimeCalendarImpl.create().value( NOW.minusSeconds( 1 ) ).deleteAfterRun( true ).build() ).build() );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new RuntimeException() );

        for ( int i = 0; i <= 11; i++ )
        {
            task.run();
        }

        // a job that never ran is evidence of the failure - it stays, and records the give-up
        verify( schedulerService, never() ).delete( isA( ScheduledJobName.class ) );
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void ephemeralOneTimeJobRecordsRunWhenDeleteFails()
    {
        mockJobs( jobBuilder( "job1", OneTimeCalendarImpl.create().value( NOW.minusSeconds( 1 ) ).deleteAfterRun( true ).build() ).build() );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );
        when( schedulerService.delete( isA( ScheduledJobName.class ) ) ).thenThrow( new RuntimeException() );

        task.run();

        // a failed cleanup must not become a re-run, so the tombstone is written instead
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void cronJobsAreNeverDeletedAfterRun()
    {
        mockJobs( jobBuilder( "job1", cronCalendar( "* * * * *" ) ).lastRun( NOW.minusSeconds( 90 ) ).build() );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( schedulerService, never() ).delete( isA( ScheduledJobName.class ) );
    }

    @Test
    void oneTimeJobNotRerunWhenTombstoneIsAVersionAttribute()
    {
        // the listing carries no version attributes, so the job looks as if it had never run
        final ScheduledJob listed = oneTimeJob( "job1", NOW.minusSeconds( 1 ) );
        final ScheduledJob stored = jobBuilder( "job1", OneTimeCalendarImpl.create().value( NOW.minusSeconds( 1 ) ).build() ).lastRun(
            NOW.minusSeconds( 30 ) ).build();

        final ScheduledJobEntry entry = new ScheduledJobEntry( listed, new NodeVersionId() );
        when( schedulerService.listEntries() ).thenReturn( List.of( entry ) );
        when( schedulerService.versionId( listed.getName() ) ).thenReturn( entry.versionId() );
        when( schedulerService.get( listed.getName() ) ).thenReturn( stored );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void runIsRecordedWhenTheSharedPlanCannotBeWritten()
    {
        final SchedulingCoordinator failing = mock( SchedulingCoordinator.class );
        doThrow( new RuntimeException() ).when( failing ).plannedRun( isA( ScheduledJobName.class ), isA( PlannedRun.class ) );
        task = new RescheduleTask( schedulerService, nodeService, taskService, securityService, clusterService, failing, clock );

        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        // the tombstone is what keeps the job from running again, so it is written regardless
        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void suspendedTaskSubmitsNothing()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.suspend();
        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        task.resume();
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void resumeDiscardsPlansOfJobsThatWereReplaced()
    {
        final ScheduledJob job = cronJob( "job1", "* * * * *", NOW.minusSeconds( 90 ) );
        mockJobs( job );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();
        assertNotNull( schedulingCoordinator.plannedRun( job.getName() ) );

        // a restored job of the same name is a different job - its predecessor's plan must not apply
        task.suspend();
        task.resume();
        task.run();

        assertNull( schedulingCoordinator.plannedRun( ScheduledJobName.from( "gone" ) ) );
        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void runIsRecordedFromWhenItStartsNotFromTheStartOfTheTick()
    {
        final ScheduledJob job = jobBuilder( "job1", FixedRateCalendarImpl.create().duration( Duration.ofMinutes( 1 ) ).build() )
            .modifiedTime( NOW.minusSeconds( 120 ) )
            .build();
        final List<ScheduledJobEntry> entries = List.of( new ScheduledJobEntry( job, new NodeVersionId() ) );
        mockEntries( entries );

        // the tick spends half a minute listing jobs before it gets round to submitting this one
        when( schedulerService.listEntries() ).thenAnswer( invocation -> {
            clock.plusSeconds( 30 );
            return entries;
        } );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        // one interval after the run actually started, not after the tick did
        assertEquals( NOW.plusSeconds( 90 ), schedulingCoordinator.plannedRun( job.getName() ).nextRun() );
    }

    @Test
    void runOfAJobThatChangedWhileInFlightIsNotRecorded()
    {
        final ScheduledJob job = cronJob( "job1", "* * * * *", NOW.minusSeconds( 90 ) );
        mockJobs( job );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        // the job is modified while the task is being submitted, so the node is at another version
        // by the time the run would be recorded
        when( schedulerService.versionId( job.getName() ) ).thenReturn( new NodeVersionId() );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        // neither the plan nor the run state of the job it used to be may be written back
        assertNull( schedulingCoordinator.plannedRun( job.getName() ) );
        verify( nodeService, never() ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void disabledJobNotSubmitted()
    {
        mockJobs( jobBuilder( "job1", OneTimeCalendarImpl.create().value( NOW.minusSeconds( 1 ) ).build() ).enabled( false ).build() );

        task.run();

        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void cronJobToleratesMissingRunStateFetch()
    {
        final ScheduledJob job = cronJob( "job1", "* * * * *", null );
        mockEntries( List.of( new ScheduledJobEntry( job, new NodeVersionId() ) ) );

        task.run();

        verify( schedulerService, times( 1 ) ).get( job.getName() );
        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void runStateFetchedOncePerJobVersion()
    {
        final ScheduledJob job = cronJob( "job1", "* * * * *", NOW );

        mockJobs( job );

        task.run();
        task.run();
        task.run();

        verify( schedulerService, times( 1 ) ).get( job.getName() );
        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void cronSkipsOccurrenceWhilePreviousTaskRuns()
    {
        mockJobs( cronJob( "job1", "* * * * *", NOW.minusSeconds( 90 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );

        final TaskInfo running = mock( TaskInfo.class );
        when( running.isDone() ).thenReturn( false );
        when( taskService.getTaskInfo( TaskId.from( "1" ) ) ).thenReturn( running );

        // next occurrence is due, but the previous task is still in flight - the occurrence is skipped
        clock.plusSeconds( 61 );
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        final PlannedRun skipped = schedulingCoordinator.plannedRun( ScheduledJobName.from( "job1" ) );
        assertEquals( "1", skipped.lastTaskId() );
        assertTrue( skipped.nextRun().isAfter( clock.instant() ) );

        // the previous task finished - the following occurrence runs
        when( running.isDone() ).thenReturn( true );
        clock.plusSeconds( 61 );
        task.run();

        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void fixedRateHoldsWhilePreviousTaskRuns()
    {
        final ScheduledJob job = jobBuilder( "job1", FixedRateCalendarImpl.create().duration( Duration.ofMinutes( 1 ) ).build() ).build();

        mockJobs( job );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) ).thenReturn( TaskId.from( "2" ) );

        // never run: first execution is due one interval after the modification time
        task.run();
        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );

        clock.plusSeconds( 61 );
        task.run();
        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );

        final TaskInfo running = mock( TaskInfo.class );
        when( running.isDone() ).thenReturn( false );
        when( taskService.getTaskInfo( TaskId.from( "1" ) ) ).thenReturn( running );

        // due again, but the previous task still runs - the run holds and the plan does not advance
        final Instant plannedBefore = schedulingCoordinator.plannedRun( job.getName() ).nextRun();
        clock.plusSeconds( 61 );
        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
        assertEquals( plannedBefore, schedulingCoordinator.plannedRun( job.getName() ).nextRun() );

        // the previous task finished - the held run fires on the next tick
        when( running.isDone() ).thenReturn( true );
        task.run();

        verify( taskService, times( 2 ) ).submitTask( isA( SubmitTaskParams.class ) );

        // fixed-rate jobs persist no run state
        verify( nodeService, never() ).update( isA( UpdateNodeParams.class ) );
        verify( nodeService, never() ).getByPath( isA( NodePath.class ) );
    }

    @Test
    void previousTaskIdPassedToSubmittedTask()
    {
        mockJobs( cronJob( "job1", "* * * * *", NOW.minusSeconds( 90 ), TaskId.from( "prev-task" ) ) );

        final List<Object> seenAttributes = new java.util.ArrayList<>();
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenAnswer( invocation -> {
            seenAttributes.add( ContextAccessor.current().getAttribute( RescheduleTask.SCHEDULE_LAST_TASK_ID_ATTRIBUTE ) );
            return TaskId.from( "1" );
        } );

        task.run();

        assertEquals( List.of( "prev-task" ), seenAttributes );

        // the next run receives the task submitted by this run
        clock.plusSeconds( 61 );
        task.run();

        assertEquals( List.of( "prev-task", "1" ), seenAttributes );
    }

    @Test
    void firstRunHasNoPreviousTaskId()
    {
        mockJobs( cronJob( "job1", "* * * * *", NOW.minusSeconds( 90 ) ) );

        final List<Object> seenAttributes = new java.util.ArrayList<>();
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenAnswer( invocation -> {
            seenAttributes.add( ContextAccessor.current().getAttribute( RescheduleTask.SCHEDULE_LAST_TASK_ID_ATTRIBUTE ) );
            return TaskId.from( "1" );
        } );

        task.run();

        assertEquals( 1, seenAttributes.size() );
        assertNull( seenAttributes.get( 0 ) );
    }

    @Test
    void jobDormantWhileApplicationNotStarted()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( clusterService.inCluster( isA( ApplicationKey.class ) ) ).thenReturn( false );

        task.run();
        task.run();

        // no submission, no failure, nothing recorded - the occurrence stays pending
        verify( taskService, never() ).submitTask( isA( SubmitTaskParams.class ) );
        assertNull( schedulingCoordinator.plannedRun( ScheduledJobName.from( "job1" ) ) );

        // the application arrives - the pending occurrence fires
        when( clusterService.inCluster( isA( ApplicationKey.class ) ) ).thenReturn( true );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
    }

    @Test
    void jobWithMissingDescriptorFailsLoudlyWhileItsApplicationRuns()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenThrow( new RuntimeException( "no such task" ) );

        for ( int i = 0; i <= 11; i++ )
        {
            task.run();
        }

        // a job referring to a task its running application does not provide is misconfigured,
        // so it is retried and then given up on rather than left dormant
        verify( taskService, times( 11 ) ).submitTask( isA( SubmitTaskParams.class ) );
        verify( nodeService, times( 1 ) ).update( isA( UpdateNodeParams.class ) );
    }

    @Test
    void jobNotDormantWhenAnotherMemberRunsTheApplication()
    {
        mockJobs( oneTimeJob( "job1", NOW.minusSeconds( 1 ) ) );
        when( clusterService.inCluster( isA( ApplicationKey.class ) ) ).thenReturn( true );
        when( taskService.submitTask( isA( SubmitTaskParams.class ) ) ).thenReturn( TaskId.from( "1" ) );

        task.run();

        verify( taskService, times( 1 ) ).submitTask( isA( SubmitTaskParams.class ) );
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
        when( schedulerService.listEntries() ).thenThrow( new RuntimeException() );

        for ( int i = 0; i < 10; i++ )
        {
            task.run();
        }

        verify( schedulerService, times( 10 ) ).listEntries();
    }

    @Test
    void tickErrorDoesNotPropagate()
    {
        when( schedulerService.listEntries() ).thenThrow( new Error() );

        task.run();

        verify( schedulerService, times( 1 ) ).listEntries();
    }

    private ScheduledJob oneTimeJob( final String name, final Instant value )
    {
        return oneTimeJob( name, value, null, null );
    }

    private ScheduledJob oneTimeJob( final String name, final Instant value, final Instant lastRun, final PrincipalKey user )
    {
        return jobBuilder( name, OneTimeCalendarImpl.create().value( value ).build() ).lastRun( lastRun ).user( user ).build();
    }

    private ScheduledJob cronJob( final String name, final String cron, final Instant lastRun )
    {
        return jobBuilder( name, cronCalendar( cron ) ).lastRun( lastRun ).build();
    }

    private ScheduledJob cronJob( final String name, final String cron, final Instant lastRun, final TaskId lastTaskId )
    {
        return jobBuilder( name, cronCalendar( cron ) ).lastRun( lastRun ).lastTaskId( lastTaskId ).build();
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
        return mockNode( new NodeVersionId() );
    }

    private Node mockNode( final NodeVersionId versionId )
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
            .nodeVersionId( versionId )
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
