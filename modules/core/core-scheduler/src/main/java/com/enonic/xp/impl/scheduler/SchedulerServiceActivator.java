package com.enonic.xp.impl.scheduler;

import java.time.Clock;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.core.internal.concurrent.ThreadFactoryImpl;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.task.TaskService;

@Component(immediate = true)
/**
 * A repository restore needs no handling here: it uninstalls every application bundle when it
 * starts and restarts the framework when it finishes - see {@code ClusterRestarter} in core-repo -
 * so the scheduler is taken down with it and comes back with nothing left over.
 */
public final class SchedulerServiceActivator
{
    private final InternalRepositoryService repositoryService;

    private final IndexService indexService;

    private final NodeService nodeService;

    private final TaskService taskService;

    private final SecurityService securityService;

    private final ClusterService clusterService;

    private final SchedulingCoordinator schedulingCoordinator;

    private final SchedulerConfig schedulerConfig;

    private final ScheduleAuditLogSupport auditLogSupport;

    private ServiceRegistration<SchedulerService> schedulerServiceReg;

    private ScheduledExecutorService ticker;

    @Activate
    public SchedulerServiceActivator( @Reference final InternalRepositoryService repositoryService,
                                      @Reference final IndexService indexService, @Reference final NodeService nodeService,
                                      @Reference final TaskService taskService,
                                      @Reference final SecurityService securityService,
                                      @Reference(target = "(local=true)") final ClusterService clusterService,
                                      @Reference final SchedulingCoordinator schedulingCoordinator,
                                      @Reference final SchedulerConfig schedulerConfig,
                                      @Reference final ScheduleAuditLogSupport auditLogSupport )
    {
        this.repositoryService = repositoryService;
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.taskService = taskService;
        this.securityService = securityService;
        this.clusterService = clusterService;
        this.schedulingCoordinator = schedulingCoordinator;
        this.schedulerConfig = schedulerConfig;
        this.auditLogSupport = auditLogSupport;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final SchedulerServiceImpl schedulerService = new SchedulerServiceImpl( nodeService, schedulingCoordinator, auditLogSupport );

        SchedulerRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        this.schedulerServiceReg = context.registerService( SchedulerService.class, schedulerService, null );

        // the jobs in this node's configuration are created by the tick, not here: which member
        // schedules is not known this early, and only that one writes them
        ticker = Executors.newSingleThreadScheduledExecutor( new ThreadFactoryImpl( "system-scheduler-thread-%d" ) );
        ticker.scheduleWithFixedDelay(
            new RescheduleTask( schedulerService, nodeService, taskService, securityService, clusterService, schedulingCoordinator,
                                schedulerConfig, Clock.systemUTC() ), 0, 1, TimeUnit.SECONDS );
    }

    @Deactivate
    public void deactivate()
    {
        if ( ticker != null )
        {
            ticker.shutdownNow();
        }
        if ( schedulerServiceReg != null )
        {
            schedulerServiceReg.unregister();
        }
    }
}
