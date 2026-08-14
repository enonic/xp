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
class SchedulingCoordinatorImplTest
{
    private static final ScheduledJobName JOB = ScheduledJobName.from( "job" );

    private static final Instant NEXT_RUN = Instant.parse( "2026-01-01T10:31:00Z" );

    @Mock
    private ClusterConfig clusterConfig;

    @Mock
    private HazelcastInstance hazelcastInstance;

    private final Map<String, Instant> nextRuns = new ConcurrentHashMap<>();

    @BeforeEach
    void setUp()
    {
        final IMap<String, Instant> map = mapOf( nextRuns );
        when( hazelcastInstance.<String, Instant>getMap( "com.enonic.xp.scheduler.nextRun" ) ).thenReturn( map );
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

    private SchedulingCoordinatorImpl coordinator( final boolean clustered, final boolean hazelcastAvailable )
    {
        when( clusterConfig.isEnabled() ).thenReturn( clustered );
        final SchedulingCoordinatorImpl coordinator = new SchedulingCoordinatorImpl( clusterConfig );
        if ( hazelcastAvailable )
        {
            coordinator.setHazelcastInstance( hazelcastInstance );
        }
        return coordinator;
    }

    @Test
    void localMode()
    {
        final SchedulingCoordinatorImpl coordinator = coordinator( false, false );

        assertNull( coordinator.nextRun( JOB ) );
        coordinator.nextRun( JOB, NEXT_RUN );
        assertEquals( NEXT_RUN, coordinator.nextRun( JOB ) );

        coordinator.forget( JOB );
        assertNull( coordinator.nextRun( JOB ) );

        coordinator.nextRun( JOB, NEXT_RUN );
        coordinator.retain( Set.of( ScheduledJobName.from( "other" ) ) );
        assertNull( coordinator.nextRun( JOB ) );
    }

    @Test
    void clusteredMode()
    {
        final SchedulingCoordinatorImpl coordinator = coordinator( true, true );

        coordinator.nextRun( JOB, NEXT_RUN );
        assertEquals( NEXT_RUN, coordinator.nextRun( JOB ) );
        assertEquals( NEXT_RUN, nextRuns.get( JOB.getValue() ) );

        coordinator.forget( JOB );
        assertNull( coordinator.nextRun( JOB ) );

        coordinator.nextRun( JOB, NEXT_RUN );
        coordinator.retain( Set.of() );
        assertNull( coordinator.nextRun( JOB ) );

        coordinator.nextRun( JOB, NEXT_RUN );
        coordinator.nextRun( JOB, null );
        assertNull( coordinator.nextRun( JOB ) );
    }

    @Test
    void clusteredModeWithoutHazelcast()
    {
        final SchedulingCoordinatorImpl coordinator = coordinator( true, false );

        coordinator.nextRun( JOB, NEXT_RUN );
        assertNull( coordinator.nextRun( JOB ) );

        coordinator.forget( JOB );
        coordinator.retain( Set.of() );
    }
}
