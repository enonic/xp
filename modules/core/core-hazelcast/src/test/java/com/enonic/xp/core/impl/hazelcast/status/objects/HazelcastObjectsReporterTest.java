package com.enonic.xp.core.impl.hazelcast.status.objects;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.JsonNode;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.hazelcast.core.ITopic;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastObjectsReporterTest
    extends JsonStatusReporterTest
{
    @Mock
    HazelcastInstance hazelcastInstance;

    @Mock
    CPSubsystem cpSubsystem;

    @BeforeEach
    void setUp()
    {
        lenient().when( hazelcastInstance.getCPSubsystem() ).thenReturn( cpSubsystem );
    }

    @Test
    void getName()
    {
        final HazelcastObjectsReporter hazelcastClusterReporter = new HazelcastObjectsReporter( hazelcastInstance );
        assertEquals( "hazelcast.objects", hazelcastClusterReporter.getName() );
    }

    @Test
    void getReport()
        throws Exception
    {
        final HazelcastObjectsReporter hazelcastClusterReporter = new HazelcastObjectsReporter( hazelcastInstance );
        final IMap<?, ?> map = mock( IMap.class );
        when( map.getName() ).thenReturn( "map" );
        when( map.size() ).thenReturn( 10 );
        final IQueue<?> queue = mock( IQueue.class );
        when( queue.getName() ).thenReturn( "queue" );
        when( queue.size() ).thenReturn( 20 );
        final IExecutorService executorService = mock( IExecutorService.class );
        when( executorService.getName() ).thenReturn( "executorService" );
        final ITopic<?> topic = mock( ITopic.class );
        when( topic.getName() ).thenReturn( "topic" );
        final FencedLock lock = mock( FencedLock.class );
        when( lock.getName() ).thenReturn( "lock" );
        when( lock.getLockCount() ).thenReturn( 30 );
        when( lock.isLocked() ).thenReturn( true );

        when( hazelcastInstance.getMap( "map" ) ).thenReturn( (IMap<Object, Object>) map );
        when( hazelcastInstance.getQueue( "queue" ) ).thenReturn( (IQueue<Object>) queue );
        when( cpSubsystem.getLock( "lock" ) ).thenReturn( lock );

        when( hazelcastInstance.getDistributedObjects() ).thenReturn( List.of( map, queue, executorService, topic, lock ) );

        assertJson( "hazelcast_objects.json", hazelcastClusterReporter.getReport() );
    }

    private void assertJson( final String fileName, final JsonNode actualJson )
        throws Exception
    {
        final JsonNode expectedNode = parseJson( readFromFile( fileName ) );

        final String expectedStr = toJson( expectedNode );
        final String actualStr = toJson( actualJson );

        assertEquals( expectedStr, actualStr );
    }
}
