package com.enonic.xp.impl.scheduler;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import com.enonic.xp.cluster.ClusterConfig;
import com.enonic.xp.impl.scheduler.distributed.PlannedRun;
import com.enonic.xp.scheduler.ScheduledJobName;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    private static final PlannedRun NEXT_RUN = new PlannedRun( Instant.parse( "2026-01-01T10:31:00Z" ), "task-1" );

    @Mock
    private ClusterConfig clusterConfig;

    @Mock
    private HazelcastInstance hazelcastInstance;

    private final Map<String, PlannedRun> nextRuns = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp()
    {
        final IMap<String, PlannedRun> map = mapOf( nextRuns );
        when( hazelcastInstance.<String, PlannedRun>getMap( "com.enonic.xp.scheduler.nextRun" ) ).thenReturn( map );
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

    private SchedulingCoordinator coordinator( final boolean clustered, final boolean hazelcastAvailable )
    {
        when( clusterConfig.isEnabled() ).thenReturn( clustered );
        final SchedulingCoordinator coordinator = new SchedulingCoordinator( clusterConfig );
        if ( hazelcastAvailable )
        {
            coordinator.setHazelcastInstance( hazelcastInstance );
        }
        return coordinator;
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
    void unbindClearsOnlyTheBoundInstance()
    {
        final SchedulingCoordinator coordinator = coordinator( true, true );

        coordinator.plannedRun( JOB, NEXT_RUN );
        assertEquals( NEXT_RUN, coordinator.plannedRun( JOB ) );

        // a greedy rebind unbinds the previous instance after the new one is already bound
        coordinator.unsetHazelcastInstance( mock( HazelcastInstance.class ) );
        assertEquals( NEXT_RUN, coordinator.plannedRun( JOB ) );

        coordinator.unsetHazelcastInstance( hazelcastInstance );
        assertNull( coordinator.plannedRun( JOB ) );
    }

    @Test
    void clusteredMode()
    {
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

        coordinator.plannedRun( JOB, NEXT_RUN );
        assertNull( coordinator.plannedRun( JOB ) );

        coordinator.forget( JOB );
        coordinator.retain( Set.of() );
    }
}
