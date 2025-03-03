package com.enonic.xp.core.impl.hazelcast.status.objects;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.net.MediaType;
import com.hazelcast.cluster.Member;
import com.hazelcast.collection.IQueue;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.cp.CPSubsystem;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.map.IMap;
import com.hazelcast.scheduledexecutor.IScheduledExecutorService;
import com.hazelcast.scheduledexecutor.IScheduledFuture;
import com.hazelcast.scheduledexecutor.ScheduledTaskHandler;
import com.hazelcast.scheduledexecutor.ScheduledTaskStatistics;
import com.hazelcast.topic.ITopic;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.support.JsonTestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastObjectsReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

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
        final IScheduledExecutorService scheduledExecutorService = mock( IScheduledExecutorService.class );
        when( scheduledExecutorService.getName() ).thenReturn( "scheduledExecutorService" );

        final Member member = mock( Member.class );
        when( member.getUuid() ).thenReturn( UUID.fromString( "2792e6b2-a8e4-45f7-ad89-eaaa40353c39" ) );
        final IScheduledFuture<Object> scheduledFuture = mock( IScheduledFuture.class );
        final ScheduledTaskHandler scheduledTaskHandler = mock( ScheduledTaskHandler.class );
        when( scheduledFuture.getHandler() ).thenReturn( scheduledTaskHandler );
        when( scheduledTaskHandler.getTaskName() ).thenReturn( "some-task" );
        when( scheduledExecutorService.getAllScheduledFutures() ).thenReturn( Map.of( member, List.of( scheduledFuture ) ) );

        final ScheduledTaskStatistics stats = mock( ScheduledTaskStatistics.class );
        when( scheduledFuture.getStats() ).thenReturn( stats );

        when( stats.getTotalRuns() ).thenReturn( 3L );
        when( scheduledFuture.getDelay( TimeUnit.SECONDS ) ).thenReturn( 123L );
        when( scheduledFuture.isDone(  ) ).thenReturn( true );
        //when( scheduledFuture.isCancelled(  ) ).thenReturn( false );

        final ITopic<?> topic = mock( ITopic.class );
        when( topic.getName() ).thenReturn( "topic" );
        final FencedLock lock = mock( FencedLock.class );
        when( lock.getName() ).thenReturn( "lock" );
        when( lock.getLockCount() ).thenReturn( 30 );
        when( lock.isLocked() ).thenReturn( true );

        when( hazelcastInstance.getMap( "map" ) ).thenReturn( (IMap<Object, Object>) map );
        when( hazelcastInstance.getQueue( "queue" ) ).thenReturn( (IQueue<Object>) queue );
        when( cpSubsystem.getLock( "lock" ) ).thenReturn( lock );

        when( hazelcastInstance.getDistributedObjects() ).thenReturn(
            List.of( map, queue, scheduledExecutorService, executorService, topic, lock ) );

        assertJson( "hazelcast_objects.json", hazelcastClusterReporter );
    }

    private void assertJson( final String fileName, StatusReporter reporter )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
    }
}
