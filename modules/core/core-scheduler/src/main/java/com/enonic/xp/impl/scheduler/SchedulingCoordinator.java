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
import com.enonic.xp.impl.scheduler.distributed.PlannedRun;
import com.enonic.xp.scheduler.ScheduledJobName;

/**
 * Shares planned job executions between cluster members, so a job is not submitted
 * twice for the same occurrence, planned occurrences survive a scheduler failover,
 * and the task of the previous run is known when the next run is due.
 */
@Component(service = SchedulingCoordinator.class, immediate = true)
public class SchedulingCoordinator
{
    private static final String NEXT_RUN_MAP_NAME = "com.enonic.xp.scheduler.nextRun";

    private final boolean clusterEnabled;

    private final ConcurrentMap<String, PlannedRun> localNextRuns = new ConcurrentHashMap<>();

    private volatile HazelcastInstance hazelcastInstance;

    @Activate
    public SchedulingCoordinator( @Reference final ClusterConfig clusterConfig )
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

    /**
     * Planned execution of a job, or null when not known
     * (never run since the coordinator state was created, or state was lost).
     * For a one-time job a non-null value means its only execution was already submitted.
     */
    public PlannedRun plannedRun( final ScheduledJobName name )
    {
        if ( !clusterEnabled )
        {
            return localNextRuns.get( name.getValue() );
        }
        return nextRuns().get( name.getValue() );
    }

    /**
     * Records the planned execution of a job.
     */
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
        nextRuns().set( name.getValue(), value );
    }

    /**
     * Discards the planned execution of a job, e.g. when the job is modified or deleted.
     */
    public void forget( final ScheduledJobName name )
    {
        if ( !clusterEnabled )
        {
            localNextRuns.remove( name.getValue() );
            return;
        }
        nextRuns().delete( name.getValue() );
    }

    /**
     * Discards planned executions of all jobs except the given ones.
     */
    public void retain( final Set<ScheduledJobName> names )
    {
        final Set<String> keys = names.stream().map( ScheduledJobName::getValue ).collect( Collectors.toSet() );
        if ( !clusterEnabled )
        {
            localNextRuns.keySet().retainAll( keys );
            return;
        }
        final IMap<String, PlannedRun> nextRuns = nextRuns();
        nextRuns.keySet().stream().filter( key -> !keys.contains( key ) ).forEach( nextRuns::delete );
    }

    private IMap<String, PlannedRun> nextRuns()
    {
        final HazelcastInstance hazelcast = this.hazelcastInstance;
        if ( hazelcast == null )
        {
            // a clustered installation coordinates through Hazelcast or not at all: carrying on
            // without it would let each member schedule as though it were alone
            throw new IllegalStateException( "Cannot resolve Hazelcast to coordinate scheduling" );
        }
        return hazelcast.getMap( NEXT_RUN_MAP_NAME );
    }
}
