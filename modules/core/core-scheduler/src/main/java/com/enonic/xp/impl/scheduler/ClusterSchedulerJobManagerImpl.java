package com.enonic.xp.impl.scheduler;

import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;

import com.enonic.xp.node.NodeService;
import com.enonic.xp.scheduler.CreateScheduledJobParams;
import com.enonic.xp.scheduler.ScheduledJob;
import com.enonic.xp.scheduler.ScheduledJobName;

@Component(immediate = true, service = SchedulerJobManager.class)
public final class ClusterSchedulerJobManagerImpl
    extends BaseSchedulerJobManager
{
    private final IMap<String, Boolean> jobs;

    @Activate
    public ClusterSchedulerJobManagerImpl( @Reference final NodeService nodeService, @Reference final HazelcastInstance hazelcastInstance )
    {
        super( nodeService );
        this.jobs = hazelcastInstance.getMap( "system.scheduler.jobs" );
    }

    public ScheduledJob create( final CreateScheduledJobParams params )
    {
        jobs.lock( params.getName().toString(), 10, TimeUnit.SECONDS );
        try
        {

            if ( jobs.putIfAbsent( params.getName().toString(), true, 1, TimeUnit.MINUTES ) == null )
            {
                return doCreate( params );
            }
            throw new RuntimeException( String.format( "job [%s] is already created", params.getName() ) );
        }
        finally
        {
            jobs.unlock( params.getName().toString() );
        }
    }

    public boolean delete( final ScheduledJobName name )
    {
        jobs.lock( name.toString(), 10, TimeUnit.SECONDS );
        try
        {
            final boolean result = doDelete( name );

            jobs.remove( name.toString() );

            return result;
        }
        finally
        {
            jobs.unlock( name.toString() );
        }
    }
}
