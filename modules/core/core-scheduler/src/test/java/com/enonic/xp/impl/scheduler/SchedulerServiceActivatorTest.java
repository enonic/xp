package com.enonic.xp.impl.scheduler;

import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.event.Event;
import com.enonic.xp.impl.scheduler.distributed.RescheduleTask;
import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.CronCalendar;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchedulerServiceActivatorTest
{
    @Mock(stubOnly = true)
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<SchedulerService> service;

    @Mock
    private NodeService nodeService;

    @Mock(stubOnly = true)
    private IndexService indexService;

    @Mock(stubOnly = true)
    private RepositoryService repositoryService;

    @Mock
    private SchedulerExecutorService schedulerExecutorService;

    @Mock(stubOnly = true)
    private SchedulerConfig schedulerConfig;

    @Mock(stubOnly = true)
    private ScheduleAuditLogSupport auditLogSupport;

    private SchedulerServiceActivator activator;

    private CalendarService calendarService;

    @BeforeEach
    void setUp()
    {
        calendarService = new CalendarServiceImpl();

        when( indexService.isMaster() ).thenReturn( true );
        when( indexService.waitForYellowStatus() ).thenReturn( true );

        activator = new SchedulerServiceActivator( repositoryService, indexService, nodeService, schedulerExecutorService, schedulerConfig,
                                                   auditLogSupport );

        when( bundleContext.registerService( same( SchedulerService.class ), any( SchedulerService.class ), isNull() ) ).
            thenReturn( service );
    }

    @Test
    void lifecycle()
    {
        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }

    @Test
    void rescheduleErrorDoesNotStop()
    {
        when( schedulerExecutorService.scheduleAtFixedRate( isA( SchedulableTask.class ), anyLong(), anyLong(),
                                                            isA( TimeUnit.class ) ) ).thenThrow( new RuntimeException( "ex message" ) );
        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }

    @Test
    void initWithJob()
    {
        final CreateScheduledJobParams jobParams = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getDefault() ) ).
            config( new PropertyTree() ).
            build();

        mockNode( jobParams );

        when( schedulerConfig.jobs() ).thenReturn( Set.of( jobParams ) );

        activator.activate( bundleContext );

        verify( nodeService, times( 1 ) ).create( isA( CreateNodeParams.class ) );
    }

    @Test
    void initWithExistJob()
    {
        final CreateScheduledJobParams jobParams = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getDefault() ) ).
            config( new PropertyTree() ).
            build();

        when( schedulerConfig.jobs() ).thenReturn( Set.of( jobParams ) );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenThrow(
            new NodeAlreadyExistAtPathException( new NodePath( NodePath.ROOT, NodeName.from( jobParams.getName().getValue() ) ),
                                                 RepositoryId.from( "repo" ), Branch.from( "branch" ) ) );

        activator.activate( bundleContext );

        verify( nodeService, times( 1 ) ).create( isA( CreateNodeParams.class ) );
    }

    @Test
    void initWithInvalidJob()
    {
        final CreateScheduledJobParams jobParams = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getDefault() ) ).
            config( new PropertyTree() ).
            build();

        when( schedulerConfig.jobs() ).thenReturn( Set.of( jobParams ) );

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenThrow( new RuntimeException() );

        assertThrows( RuntimeException.class, () -> activator.activate( bundleContext ) );
    }

    @Test
    void initWithAlreadyScheduledRescheduleTask()
    {
        final CreateScheduledJobParams jobParams = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getDefault() ) ).
            config( new PropertyTree() ).
            build();

        mockNode( jobParams );

        when( schedulerConfig.jobs() ).thenReturn( Set.of( jobParams ) );

        final ScheduledFuture<?> scheduledFuture = mock( ScheduledFuture.class );
        when( scheduledFuture.isDone() ).thenReturn( false );

        final Optional<? extends ScheduledFuture<?>> optional = Optional.of( scheduledFuture );
        doReturn( optional ).when( schedulerExecutorService ).get( RescheduleTask.NAME );

        activator.activate( bundleContext );

        verify( schedulerExecutorService, never() ).scheduleAtFixedRate( isA( SchedulableTask.class ), anyLong(), anyLong(),
                                                                         isA( TimeUnit.class ) );
    }

    @Test
    void rescheduleFinishedRescheduleTask()
    {
        final CreateScheduledJobParams jobParams = CreateScheduledJobParams.create().
            name( ScheduledJobName.from( "name" ) ).
            descriptor( DescriptorKey.from( "appKey:descriptorName" ) ).
            calendar( calendarService.cron( "* * * * *", TimeZone.getDefault() ) ).
            config( new PropertyTree() ).
            build();

        mockNode( jobParams );

        when( schedulerConfig.jobs() ).thenReturn( Set.of( jobParams ) );

        final ScheduledFuture<?> scheduledFuture = mock( ScheduledFuture.class );
        when( scheduledFuture.isDone() ).thenReturn( true );

        final Optional<? extends ScheduledFuture<?>> optional = Optional.of( scheduledFuture );
        doReturn( optional ).when( schedulerExecutorService ).get( RescheduleTask.NAME );

        activator.activate( bundleContext );

        verify( schedulerExecutorService, times(1) ).scheduleAtFixedRate( isA( SchedulableTask.class ), anyLong(), anyLong(),
                                                                         isA( TimeUnit.class ) );
    }

    @Test
    public void restoreInitialized()
        throws Exception
    {
        activator.onEvent( Event.create( "repository.restoreInitialized" ).
            build() );

        Mockito.verify( schedulerExecutorService, Mockito.times( 1 ) ).dispose( RescheduleTask.NAME );
    }


    private void mockNode( final CreateScheduledJobParams params )
    {
        final PropertyTree jobData = new PropertyTree();

        final PropertySet calendar = jobData.newSet();
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TYPE, params.getCalendar().getType().name() );
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_VALUE, ( (CronCalendar) params.getCalendar() ).getCronValue() );
        calendar.addString( ScheduledJobPropertyNames.CALENDAR_TIMEZONE, ( (CronCalendar) params.getCalendar() ).getTimeZone().getID() );

        jobData.addString( ScheduledJobPropertyNames.DESCRIPTOR, params.getDescriptor().toString() );
        jobData.addBoolean( ScheduledJobPropertyNames.ENABLED, params.isEnabled() );
        jobData.addSet( ScheduledJobPropertyNames.CALENDAR, calendar );
        jobData.addSet( ScheduledJobPropertyNames.CONFIG, params.getConfig().getRoot().copy( jobData ) );

        jobData.setString( ScheduledJobPropertyNames.CREATOR, "user:system:creator" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIER, "user:system:creator" );
        jobData.setString( ScheduledJobPropertyNames.CREATED_TIME, "2016-11-02T10:36:00Z" );
        jobData.setString( ScheduledJobPropertyNames.MODIFIED_TIME, "2016-11-02T10:36:00Z" );

        final Node job = Node.create().
            id( NodeId.from( "abc" ) ).
            name( params.getName().getValue() ).
            parentPath( NodePath.ROOT ).
            data( jobData ).
            build();

        when( nodeService.create( isA( CreateNodeParams.class ) ) ).thenReturn( job );
    }
}
