package com.enonic.xp.impl.scheduler;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.scheduler.ScheduledJobName;

@Component(immediate = true)
public class SchedulingCoordinatorImpl
    implements SchedulingCoordinator
{
    private static final String NEXT_RUN_MAP_NAME = "com.enonic.xp.scheduler.nextRun";

    private final boolean clusterEnabled;

    private final ConcurrentMap<String, PlannedRun> localNextRuns = new ConcurrentHashMap<>();

    private volatile HazelcastInstance hazelcastInstance;

    @Activate
    public SchedulingCoordinatorImpl( @Reference final ClusterConfig clusterConfig )
    {
        this.clusterEnabled = clusterConfig.isEnabled();
    }

    @Reference(cardinality = ReferenceCardinality.OPTIONAL, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
    public void setHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    public void unsetHazelcastInstance( final HazelcastInstance hazelcastInstance )
    {
        if ( this.hazelcastInstance == hazelcastInstance )
        {
            this.hazelcastInstance = null;
        }
    }

    @Override
    public PlannedRun plannedRun( final ScheduledJobName name )
    {
        if ( !clusterEnabled )
        {
            return localNextRuns.get( name.getValue() );
        }
        final HazelcastInstance hazelcast = this.hazelcastInstance;
        return hazelcast != null ? hazelcast.<String, PlannedRun>getMap( NEXT_RUN_MAP_NAME ).get( name.getValue() ) : null;
    }

    @Override
    public void plannedRun( final ScheduledJobName name, final PlannedRun value )
    {
        if ( value == null )
        {
            forget( name );
            return;
        }
        if ( !clusterEnabled )
        {
            localNextRuns.put( name.getValue(), value );
            return;
        }
        final HazelcastInstance hazelcast = this.hazelcastInstance;
        if ( hazelcast != null )
        {
            hazelcast.<String, PlannedRun>getMap( NEXT_RUN_MAP_NAME ).set( name.getValue(), value );
        }
    }

    @Override
    public void forget( final ScheduledJobName name )
    {
        if ( !clusterEnabled )
        {
            localNextRuns.remove( name.getValue() );
            return;
        }
        final HazelcastInstance hazelcast = this.hazelcastInstance;
        if ( hazelcast != null )
        {
            hazelcast.<String, PlannedRun>getMap( NEXT_RUN_MAP_NAME ).delete( name.getValue() );
        }
    }

    @Override
    public void retain( final Set<ScheduledJobName> names )
    {
        final Set<String> keys = names.stream().map( ScheduledJobName::getValue ).collect( Collectors.toSet() );
        if ( !clusterEnabled )
        {
            localNextRuns.keySet().retainAll( keys );
            return;
        }
        final HazelcastInstance hazelcast = this.hazelcastInstance;
        if ( hazelcast != null )
        {
            final IMap<String, PlannedRun> nextRuns = hazelcast.getMap( NEXT_RUN_MAP_NAME );
            nextRuns.keySet().stream().filter( key -> !keys.contains( key ) ).forEach( nextRuns::delete );
        }
    }
}
