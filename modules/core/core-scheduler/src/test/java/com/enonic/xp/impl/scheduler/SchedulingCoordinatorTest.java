package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.hazelcast.replicatedmap.ReplicatedMap;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.impl.scheduler.distributed.PlannedRun;
import com.enonic.xp.scheduler.ScheduledJobName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SchedulingCoordinatorTest
{
    private static final ScheduledJobName JOB = ScheduledJobName.from( "job" );

    private static final PlannedRun NEXT_RUN = new PlannedRun( Instant.parse( "2026-01-01T10:31:00Z" ), "task-1", "version-1" );

    private static final UUID LOCAL = UUID.randomUUID();

    private static final UUID OTHER = UUID.randomUUID();

    @Mock
    private ClusterConfig clusterConfig;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private HazelcastInstance hazelcastInstance;

    private final Map<String, PlannedRun> nextRuns = new ConcurrentHashMap<>();

    private final Map<UUID, Map<String, String>> schedulingAttributes = new HashMap<>();

    @BeforeEach
    void setUp()
    {
        // built before stubbing: these are mocks themselves, and stubbing one inside a when() call
        // leaves the outer stubbing unfinished
        final IMap<String, PlannedRun> nextRunsMap = mapOf( nextRuns );
        final ReplicatedMap<UUID, Map<String, String>> attributesMap = replicatedMapOf( schedulingAttributes );

        when( hazelcastInstance.<String, PlannedRun>getMap( "com.enonic.xp.scheduler.nextRun" ) ).thenReturn( nextRunsMap );
        when( hazelcastInstance.<UUID, Map<String, String>>getReplicatedMap( "com.enonic.xp.scheduler" ) ).thenReturn( attributesMap );
    }

    @SuppressWarnings("unchecked")
    private <V> IMap<String, V> mapOf( final Map<String, V> backing )
    {
        final IMap<String, V> map = mock( IMap.class );
        when( map.get( anyString() ) ).thenAnswer( invocation -> backing.get( invocation.<String>getArgument( 0 ) ) );
        when( map.keySet() ).thenAnswer( invocation -> Set.copyOf( backing.keySet() ) );
        doAnswer( invocation -> {
            backing.put( invocation.getArgument( 0 ), invocation.getArgument( 1 ) );
            return null;
        } ).when( map ).set( anyString(), any() );
        doAnswer( invocation -> {
            backing.remove( invocation.<String>getArgument( 0 ) );
            return null;
        } ).when( map ).delete( anyString() );
        return map;
    }

    @SuppressWarnings("unchecked")
    private <K, V> ReplicatedMap<K, V> replicatedMapOf( final Map<K, V> backing )
    {
        final ReplicatedMap<K, V> map = mock( ReplicatedMap.class );
        when( map.get( any() ) ).thenAnswer( invocation -> backing.get( invocation.getArgument( 0 ) ) );
        when( map.put( any(), any() ) ).thenAnswer( invocation -> backing.put( invocation.getArgument( 0 ),
                                                                               invocation.getArgument( 1 ) ) );
        return map;
    }

    private SchedulingCoordinator coordinator( final boolean clustered, final boolean hazelcastAvailable )
    {
        return coordinator( clustered, hazelcastAvailable, true );
    }

    private SchedulingCoordinator coordinator( final boolean clustered, final boolean hazelcastAvailable, final boolean acceptScheduling )
    {
        when( clusterConfig.isEnabled() ).thenReturn( clustered );

        final SchedulingCoordinator coordinator =
            new SchedulingCoordinator( clusterConfig, hazelcastAvailable ? hazelcastInstance : null );
        coordinator.activate( schedulingConfig( acceptScheduling ) );
        return coordinator;
    }

    private SchedulingConfig schedulingConfig( final boolean acceptScheduling )
    {
        final SchedulingConfig config = mock( SchedulingConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( config.acceptScheduling() ).thenReturn( acceptScheduling );
        return config;
    }

    private void members( final UUID... uuids )
    {
        final Set<Member> members = new LinkedHashSet<>();
        for ( final UUID uuid : uuids )
        {
            members.add( member( uuid ) );
        }
        final Member localMember = member( LOCAL );

        when( hazelcastInstance.getCluster().getMembers() ).thenReturn( members );
        when( hazelcastInstance.getCluster().getLocalMember() ).thenReturn( localMember );
    }

    private Member member( final UUID uuid )
    {
        final Member member = mock( Member.class );
        when( member.getUuid() ).thenReturn( uuid );
        return member;
    }

    private void accepts( final UUID uuid, final boolean accept )
    {
        schedulingAttributes.put( uuid, Map.of( "scheduling-enabled", String.valueOf( accept ) ) );
    }

    @Test
    void localMode()
    {
        final SchedulingCoordinator coordinator = coordinator( false, false );

        assertNull( coordinator.plannedRun( JOB ) );
        coordinator.plannedRun( JOB, NEXT_RUN );
        assertEquals( NEXT_RUN, coordinator.plannedRun( JOB ) );

        coordinator.forget( JOB );
        assertNull( coordinator.plannedRun( JOB ) );

        coordinator.plannedRun( JOB, NEXT_RUN );
        coordinator.retain( Set.of( ScheduledJobName.from( "other" ) ) );
        assertNull( coordinator.plannedRun( JOB ) );

        coordinator.plannedRun( JOB, NEXT_RUN );
        coordinator.plannedRun( JOB, null );
        assertNull( coordinator.plannedRun( JOB ) );
    }

    @Test
    void clusteredMode()
    {
        members( LOCAL );
        final SchedulingCoordinator coordinator = coordinator( true, true );

        coordinator.plannedRun( JOB, NEXT_RUN );
        assertEquals( NEXT_RUN, coordinator.plannedRun( JOB ) );
        assertEquals( NEXT_RUN, nextRuns.get( JOB.getValue() ) );

        coordinator.forget( JOB );
        assertNull( coordinator.plannedRun( JOB ) );

        coordinator.plannedRun( JOB, NEXT_RUN );
        final ScheduledJobName other = ScheduledJobName.from( "other" );
        coordinator.plannedRun( other, NEXT_RUN );
        coordinator.retain( Set.of( other ) );
        assertNull( coordinator.plannedRun( JOB ) );
        assertEquals( NEXT_RUN, coordinator.plannedRun( other ) );

        coordinator.plannedRun( JOB, NEXT_RUN );
        coordinator.plannedRun( JOB, null );
        assertNull( coordinator.plannedRun( JOB ) );
    }

    @Test
    void clusteredModeWithoutHazelcast()
    {
        final SchedulingCoordinator coordinator = coordinator( true, false );

        // scheduling waits for Hazelcast rather than proceeding as though this member were alone
        assertThrows( IllegalStateException.class, () -> coordinator.plannedRun( JOB ) );
        assertThrows( IllegalStateException.class, () -> coordinator.plannedRun( JOB, NEXT_RUN ) );
        assertThrows( IllegalStateException.class, () -> coordinator.forget( JOB ) );
        assertThrows( IllegalStateException.class, () -> coordinator.retain( Set.of() ) );
    }

    @Test
    void isLeader_notClustered()
    {
        // the flag is a way to pick between nodes, and one node has nothing to pick from
        assertTrue( coordinator( false, false, false ).isLeader() );
    }

    @Test
    void isLeader_clusteredWithoutHazelcast()
    {
        assertFalse( coordinator( true, false ).isLeader() );
    }

    @Test
    void isLeader_publishesWhatItAccepts()
    {
        members( LOCAL );
        coordinator( true, true, false );

        assertEquals( Map.of( "scheduling-enabled", "false" ), schedulingAttributes.get( LOCAL ) );
    }

    @Test
    void isLeader_firstAcceptingMember()
    {
        members( LOCAL, OTHER );
        final SchedulingCoordinator coordinator = coordinator( true, true );
        accepts( OTHER, true );

        assertTrue( coordinator.isLeader() );
    }

    @Test
    void isLeader_notFirstAcceptingMember()
    {
        members( OTHER, LOCAL );
        final SchedulingCoordinator coordinator = coordinator( true, true );
        accepts( OTHER, true );

        assertFalse( coordinator.isLeader() );
    }

    @Test
    void isLeader_skipsMembersThatDoNotAccept()
    {
        members( OTHER, LOCAL );
        final SchedulingCoordinator coordinator = coordinator( true, true );
        accepts( OTHER, false );

        assertTrue( coordinator.isLeader() );
    }

    @Test
    void isLeader_waitsForAMemberThatHasNotPublished()
    {
        members( OTHER, LOCAL );
        final SchedulingCoordinator coordinator = coordinator( true, true );

        // the older member may well accept scheduling and simply not have said so yet - taking the
        // duty over its head is how two members end up ticking the same schedule
        assertFalse( coordinator.isLeader() );

        accepts( OTHER, false );
        assertTrue( coordinator.isLeader() );
    }

    @Test
    void isLeader_noMemberAccepts()
    {
        members( LOCAL, OTHER );
        final SchedulingCoordinator coordinator = coordinator( true, true, false );
        accepts( OTHER, false );

        assertFalse( coordinator.isLeader() );
    }

    @Test
    void reconfigurationIsAppliedWithoutARestart()
    {
        members( LOCAL );
        final SchedulingCoordinator coordinator = coordinator( true, true );
        assertTrue( coordinator.isLeader() );

        coordinator.modify( schedulingConfig( false ) );

        assertFalse( coordinator.isLeader() );
    }

    @Test
    void deactivateRefusesRatherThanFallsSilent()
    {
        members( LOCAL );
        final SchedulingCoordinator coordinator = coordinator( true, true );
        assertEquals( Map.of( "scheduling-enabled", "true" ), schedulingAttributes.get( LOCAL ) );

        coordinator.deactivate();

        // a member whose scheduler is stopping says so, instead of leaving a hole that would hold
        // back every member behind it in join order
        assertEquals( Map.of( "scheduling-enabled", "false" ), schedulingAttributes.get( LOCAL ) );
    }
}
