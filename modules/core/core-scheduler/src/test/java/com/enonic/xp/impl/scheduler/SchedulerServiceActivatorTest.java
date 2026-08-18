package com.enonic.xp.impl.scheduler;

import java.util.Set;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.scheduler.CalendarService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJobName;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.task.TaskService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
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
    private InternalRepositoryService repositoryService;

    @Mock(stubOnly = true)
    private TaskService taskService;

    @Mock(stubOnly = true)
    private SecurityService securityService;

    @Mock(stubOnly = true)
    private ClusterService clusterService;

    @Mock(stubOnly = true)
    private SchedulingCoordinator schedulingCoordinator;

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
        when( schedulerConfig.jobs() ).thenReturn( Set.of() );

        activator =
            new SchedulerServiceActivator( repositoryService, indexService, nodeService, taskService,
                                           securityService, clusterService, schedulingCoordinator, schedulerConfig, auditLogSupport );

        when( bundleContext.registerService( same( SchedulerService.class ), any( SchedulerService.class ), isNull() ) ).thenReturn(
            service );
    }

    @Test
    void lifecycle()
    {
        activator.activate( bundleContext );

        activator.deactivate();
        verify( service ).unregister();
    }

    @Test
    void configuredJobsAreNotWrittenOnActivation()
    {
        final CreateScheduledJobParams jobParams = CreateScheduledJobParams.create()
            .name( ScheduledJobName.from( "name" ) )
            .descriptor( DescriptorKey.from( "appKey:descriptorName" ) )
            .calendar( calendarService.cron( "* * * * *", TimeZone.getDefault() ) )
            .config( new PropertyTree() )
            .build();

        when( schedulerConfig.jobs() ).thenReturn( Set.of( jobParams ) );

        activator.activate( bundleContext );
        activator.deactivate();

        // the configured jobs belong to whichever member ends up scheduling, and are written by its
        // tick - see RescheduleTaskTest
        verify( nodeService, never() ).create( isA( CreateNodeParams.class ) );
    }
}
