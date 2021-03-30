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

    private ServiceRegistration<SchedulerService> schedulerServiceReg;

    private final SchedulerExecutorService schedulerExecutorService;

    @Activate
    public SchedulerServiceActivator( @Reference final RepositoryService repositoryService, @Reference final IndexService indexService,
                                      @Reference final NodeService nodeService,
                                      @Reference final SchedulerExecutorService schedulerExecutorService )
    {
        this.repositoryService = repositoryService;
        this.indexService = indexService;
        this.nodeService = nodeService;
        this.schedulerExecutorService = schedulerExecutorService;
    }

    @Activate
    public void activate( final BundleContext context )
    {
        final SchedulerServiceImpl schedulerService =
            new SchedulerServiceImpl( indexService, repositoryService, nodeService, schedulerExecutorService );

        schedulerService.initialize();
        this.schedulerServiceReg = context.registerService( SchedulerService.class, schedulerService, null );

        try
        {
            schedulerExecutorService.scheduleAtFixedRate( new RescheduleTask(), 0, 1, TimeUnit.SECONDS );
        }
        catch ( Exception e )
        {
            LOG.debug( "RescheduleTask hasn't been started.", e );
        }
    }

    @Deactivate
    public void deactivate()
    {
        schedulerServiceReg.unregister();
    }
}
