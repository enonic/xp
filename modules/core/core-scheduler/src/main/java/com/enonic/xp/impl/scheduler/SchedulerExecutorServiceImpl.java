package com.enonic.xp.impl.scheduler;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.core.internal.concurrent.DynamicReference;
import com.enonic.xp.impl.scheduler.distributed.SchedulableTask;

@Component(immediate = true)
public final class SchedulerExecutorServiceImpl
    implements SchedulerExecutorService
{
    private final SystemScheduler localScheduler;

    private final DynamicReference<SystemScheduler> clusteredSchedulerRef = new DynamicReference<>();

    private final boolean clusterEnabled;

    @Activate
    public SchedulerExecutorServiceImpl( @Reference(target = "(local=true)") final SystemScheduler localScheduler,
                                         final @Reference ClusterConfig config )
    {
        this.localScheduler = localScheduler;
        this.clusterEnabled = config.isEnabled();
    }

    @Override
    public void disposeAllDone()
    {
        getScheduler().disposeAllDone();
    }

    @Override
    public Set<String> getAllFutures()
    {
        return getScheduler().getAllFutures();
    }

    @Override
    public Optional<? extends ScheduledFuture<?>> get( final String name )
    {
        return getScheduler().get( name );
    }

    @Override
    public void dispose( final String name )
    {
        getScheduler().dispose( name );
    }

    public ScheduledFuture<?> schedule( SchedulableTask task, long delay, TimeUnit unit )
    {
        return getScheduler().schedule( task, delay, unit );
    }

    public ScheduledFuture<?> scheduleAtFixedRate( final SchedulableTask task, final long initialDelay, final long period,
                                                   final TimeUnit unit )
    {
        return getScheduler().scheduleAtFixedRate( task, initialDelay, period, unit );
    }

    private SystemScheduler getScheduler()
    {
        if ( clusterEnabled )
        {
            try
            {
                return clusteredSchedulerRef.get( 5, TimeUnit.SECONDS );
            }
            catch ( InterruptedException | TimeoutException e )
            {
                throw new RuntimeException( e );
            }
        }
        return localScheduler;
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, target = "(!(local=true))")
    public void setClusteredScheduler( final SystemScheduler scheduler )
    {
        this.clusteredSchedulerRef.set( scheduler );
    }

    public void unsetClusteredScheduler( final SystemScheduler scheduler )
    {
        this.clusteredSchedulerRef.reset();
    }
}
