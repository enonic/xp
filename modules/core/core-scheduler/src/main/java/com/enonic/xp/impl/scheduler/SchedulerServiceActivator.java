package com.enonic.xp.impl.scheduler;

import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.scheduledexecutor.DuplicateTaskException;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;

import com.enonic.xp.impl.scheduler.distributed.RescheduleTask;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.scheduler.SchedulerService;

@Component(immediate = true)
public final class SchedulerServiceActivator
{
    private static final Logger LOG = LoggerFactory.getLogger( SchedulerServiceActivator.class );

    private final RepositoryService repositoryService;

    private final IndexService indexService;

    private final NodeService nodeService;

    private final HazelcastInstance hazelcastInstance;

    private ServiceRegistration<SchedulerService> service;

    @Activate
    public SchedulerServiceActivator( @Reference final RepositoryService repositoryService, @Reference final IndexService indexService,
                                      @Reference final NodeService nodeService, @Reference final HazelcastInstance hazelcastInstance )
    {
        this.repositoryService = repositoryService;
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.hazelcastInstance = hazelcastInstance;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final SchedulerServiceImpl schedulerService =
            new SchedulerServiceImpl( indexService, repositoryService, nodeService, hazelcastInstance );

        schedulerService.initialize();
        service = context.registerService( SchedulerService.class, schedulerService, null );

        final IScheduledExecutorService schedulerExecutorService = hazelcastInstance.getScheduledExecutorService( "scheduler" );

        try
        {
            schedulerExecutorService.scheduleAtFixedRate( new RescheduleTask(), 0, 1, TimeUnit.SECONDS );
        }
        catch ( DuplicateTaskException e )
        {
            LOG.debug( "RescheduleTask has been scheduled already.", e );
        }
    }

    @Deactivate
    public void deactivate()
    {
        service.unregister();
    }
}
