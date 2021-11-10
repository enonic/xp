package com.enonic.xp.impl.scheduler;

import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.concurrent.DynamicReference;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.impl.scheduler.distributed.RescheduleTask;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.scheduler.SchedulerService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component(immediate = true)
public final class SchedulerServiceActivator
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SchedulerServiceActivator.class );

    private final RepositoryService repositoryService;

    private final IndexService indexService;

    private final SchedulerExecutorService schedulerExecutorService;

    private final SchedulerConfig schedulerConfig;

    private final ScheduleAuditLogSupport auditLogSupport;

    private final SchedulerJobManager localJobManager;

    private final DynamicReference<SchedulerJobManager> clusterJobManagerRef;

    private final ClusterConfig clusterConfig;

    private ServiceRegistration<SchedulerService> schedulerServiceReg;

    @Activate
    public SchedulerServiceActivator( @Reference final RepositoryService repositoryService, @Reference final IndexService indexService,
                                      @Reference final SchedulerExecutorService schedulerExecutorService,
                                      @Reference final SchedulerConfig schedulerConfig,
                                      @Reference final ScheduleAuditLogSupport auditLogSupport,
                                      @Reference final ClusterConfig clusterConfig,
                                      @Reference(target = "(local=true)") final SchedulerJobManager schedulerJobManager )
    {
        this.repositoryService = repositoryService;
        this.indexService = indexService;
        this.schedulerExecutorService = schedulerExecutorService;
        this.schedulerConfig = schedulerConfig;
        this.auditLogSupport = auditLogSupport;
        this.clusterConfig = clusterConfig;
        this.localJobManager = schedulerJobManager;
        this.clusterJobManagerRef = new DynamicReference<>();
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
        final SchedulerServiceImpl schedulerService =
            new SchedulerServiceImpl( indexService, repositoryService, schedulerExecutorService, auditLogSupport, localJobManager,
                                      clusterJobManagerRef, clusterConfig );

        schedulerService.initialize();
        this.schedulerServiceReg = context.registerService( SchedulerService.class, schedulerService, null );

        try
        {
            if ( !schedulerExecutorService.getAllFutures().contains( RescheduleTask.NAME ) )
            {
                schedulerExecutorService.scheduleAtFixedRate( new RescheduleTask(), 0, 1, TimeUnit.SECONDS );
            }
        }
        catch ( Exception e )
        {
            LOG.debug( "RescheduleTask hasn't been started.", e );
        }

        createConfigJobs( schedulerService );

    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event.isType( "repository.restoreInitialized" ) )
        {
            schedulerExecutorService.dispose( RescheduleTask.NAME );
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
            catch ( NodeAlreadyExistAtPathException e )
            {
                LOG.debug( String.format( "[%s] job already exist.", job.getName().getValue() ), e );
            }
        } ) );
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, target = "(!(local=true))")
    public void setClusterJobManager( final SchedulerJobManager manager )
    {
        this.clusterJobManagerRef.set( manager );
    }

    public void unsetClusterJobManager( final SchedulerJobManager manager )
    {
        this.clusterJobManagerRef.reset();
    }

    @Deactivate
    public void deactivate()
    {
        schedulerServiceReg.unregister();
    }
}
