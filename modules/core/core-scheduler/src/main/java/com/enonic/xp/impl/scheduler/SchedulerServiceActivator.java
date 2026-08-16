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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.concurrent.ThreadFactoryImpl;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeIdExistsException;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.internal.InternalRepositoryService;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.task.TaskService;

@Component(immediate = true)
public final class SchedulerServiceActivator
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SchedulerServiceActivator.class );

    // core-repo keeps RepositoryEvents to itself, so the event types are named here as elsewhere
    private static final String RESTORE_INITIALIZED_EVENT_TYPE = "repository.restoreInitialized";

    private static final String RESTORED_EVENT_TYPE = "repository.restored";

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

    private volatile RescheduleTask rescheduleTask;

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

    private static Context adminContext()
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .authInfo( AuthenticationInfo.create()
                           .principals( RoleKeys.ADMIN )
                           .user( User.create().key( PrincipalKey.ofSuperUser() ).login( PrincipalKey.ofSuperUser().getId() ).build() )
                           .build() )
            .build();
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final SchedulerServiceImpl schedulerService = new SchedulerServiceImpl( nodeService, schedulingCoordinator, auditLogSupport );

        SchedulerRepoInitializer.create().setIndexService( indexService ).setRepositoryService( repositoryService ).build().initialize();

        createConfigJobs( schedulerService );

        this.schedulerServiceReg = context.registerService( SchedulerService.class, schedulerService, null );

        rescheduleTask =
            new RescheduleTask( schedulerService, nodeService, taskService, securityService, clusterService, schedulingCoordinator,
                                Clock.systemUTC() );

        ticker = Executors.newSingleThreadScheduledExecutor( new ThreadFactoryImpl( "system-scheduler-thread-%d" ) );
        ticker.scheduleWithFixedDelay( rescheduleTask, 0, 1, TimeUnit.SECONDS );
    }

    @Override
    public void onEvent( final Event event )
    {
        // the scheduler repository is deleted and restored underneath us, so nothing should be
        // submitted from a half-replaced set of jobs
        final RescheduleTask task = rescheduleTask;
        if ( task == null )
        {
            return;
        }
        if ( event.isType( RESTORE_INITIALIZED_EVENT_TYPE ) )
        {
            task.suspend();
        }
        else if ( event.isType( RESTORED_EVENT_TYPE ) )
        {
            task.resume();
        }
    }

    private void createConfigJobs( final SchedulerService schedulerService )
    {
        adminContext().runWith( () -> schedulerConfig.jobs().forEach( job -> {
            try
            {
                if ( indexService.isMaster() )
                {
                    schedulerService.create( job );
                }
            }
            catch ( NodeAlreadyExistAtPathException | NodeIdExistsException e )
            {
                LOG.debug( String.format( "[%s] job already exist.", job.getName().getValue() ), e );
            }
        } ) );
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
