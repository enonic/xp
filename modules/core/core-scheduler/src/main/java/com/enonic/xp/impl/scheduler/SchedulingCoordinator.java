package com.enonic.xp.impl.scheduler;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicyOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.impl.scheduler.distributed.PlannedRun;
import com.enonic.xp.scheduler.ScheduledJobName;

/**
 * Shares planned job executions between cluster members, so a job is not submitted
 * twice for the same occurrence, planned occurrences survive a scheduler failover,
 * and the task of the previous run is known when the next run is due.
 * <p>
 * Also picks the member that ticks the schedule, among those configured to accept it (#9045).
 */
@Component(service = SchedulingCoordinator.class, immediate = true)
public class SchedulingCoordinator
{
    private static final Logger LOG = LoggerFactory.getLogger( SchedulingCoordinator.class );

    private static final String NEXT_RUN_MAP_NAME = "com.enonic.xp.scheduler.nextRun";

    private static final String SCHEDULING_MAP_NAME = "com.enonic.xp.scheduler";

    private static final String SCHEDULING_ENABLED_ATTRIBUTE_KEY = "scheduling-enabled";

    /**
     * How many ticks a cluster may go without a member willing and able to schedule before it is
     * reported. Members publish what they accept as they start, so a young cluster is expected to
     * have no leader for a moment - one that still has none by now is misconfigured or broken.
     */
    private static final int TICKS_WITHOUT_LEADER_TO_WARN = 60;

    private final boolean clusterEnabled;

    private final boolean acceptScheduling;

    private final ConcurrentMap<String, PlannedRun> localNextRuns = new ConcurrentHashMap<>();

    private final HazelcastInstance hazelcastInstance;

    private int ticksWithoutLeader;

    @Activate
    public SchedulingCoordinator( @Reference final ClusterConfig clusterConfig, @Reference final SchedulerConfig schedulerConfig,
                                  @Reference(cardinality = ReferenceCardinality.OPTIONAL,
                                      policyOption = ReferencePolicyOption.GREEDY) final HazelcastInstance hazelcastInstance )
    {
        // whether this installation is clustered does not change while it runs, and a clustered one
        // has Hazelcast shortly after it starts - so this is taken once, the way the rest of the
        // cluster-dependent components take it, rather than tracked as it comes and goes
        this.clusterEnabled = clusterConfig.isEnabled();
        this.acceptScheduling = schedulerConfig.acceptScheduling();
        this.hazelcastInstance = hazelcastInstance;
    }

    @Activate
    public void activate()
    {
        publishAcceptScheduling( acceptScheduling );
    }

    @Deactivate
    public void deactivate()
    {
        // published as a refusal rather than removed: an absent value means "not known yet" and
        // holds back the whole cluster, while a member whose scheduler is stopping is known to be
        // unable to tick. The entry outlives a member that leaves, but is never read again - the
        // election only asks about members that are currently in the cluster
        publishAcceptScheduling( false );
    }

    private void publishAcceptScheduling( final boolean accept )
    {
        if ( !clusterEnabled || hazelcastInstance == null )
        {
            return;
        }
        try
        {
            schedulingAttributes().put( localMemberUuid(), Map.of( SCHEDULING_ENABLED_ATTRIBUTE_KEY, String.valueOf( accept ) ) );
        }
        catch ( Exception e )
        {
            LOG.warn( "Failed to publish whether this node accepts scheduling", e );
        }
    }

    /**
     * Whether this member is the one to tick the schedule. Of the members that accept scheduling,
     * that is the one that joined first, so the duty passes on to the next in join order once it
     * leaves; the single node of a non-clustered installation always ticks.
     * <p>
     * A member that has not yet published whether it accepts scheduling holds back every member
     * behind it in join order, and a cluster where none accepts does not schedule at all: it is
     * better for a job to run late than for two members to run it at once. Called once per tick.
     */
    public boolean isLeader()
    {
        if ( !clusterEnabled )
        {
            // one node has nowhere to hand the schedule over to, so it keeps it whatever it accepts
            return true;
        }
        if ( hazelcastInstance == null )
        {
            // Hazelcast has not started yet - who leads is not known, so nobody acts as though it did
            return false;
        }

        final ReplicatedMap<UUID, Map<String, String>> attributes = schedulingAttributes();
        for ( final Member member : hazelcastInstance.getCluster().getMembers() )
        {
            final Map<String, String> memberAttributes = attributes.get( member.getUuid() );
            if ( memberAttributes == null )
            {
                // this member may well accept scheduling and simply not have said so yet - claiming
                // the duty over its head is how two members end up ticking the same schedule
                countTickWithoutLeader( "member [" + member.getUuid() + "] has not published whether it accepts scheduling" );
                return false;
            }
            if ( Boolean.parseBoolean( memberAttributes.get( SCHEDULING_ENABLED_ATTRIBUTE_KEY ) ) )
            {
                ticksWithoutLeader = 0;
                return member.getUuid().equals( localMemberUuid() );
            }
        }
        countTickWithoutLeader( "no member accepts scheduling" );
        return false;
    }

    private void countTickWithoutLeader( final String reason )
    {
        if ( ++ticksWithoutLeader == TICKS_WITHOUT_LEADER_TO_WARN )
        {
            LOG.warn( "Scheduled jobs are not running: {}", reason );
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

    private ReplicatedMap<UUID, Map<String, String>> schedulingAttributes()
    {
        return hazelcastInstance.getReplicatedMap( SCHEDULING_MAP_NAME );
    }

    private UUID localMemberUuid()
    {
        return hazelcastInstance.getCluster().getLocalMember().getUuid();
    }

    private IMap<String, PlannedRun> nextRuns()
    {
        if ( hazelcastInstance == null )
        {
            // only reachable before Hazelcast has started: a clustered installation coordinates
            // through it or not at all, since carrying on without it would let each member schedule
            // as though it were alone
            throw new IllegalStateException( "Cannot resolve Hazelcast to coordinate scheduling" );
        }
        return hazelcastInstance.getMap( NEXT_RUN_MAP_NAME );
    }
}
