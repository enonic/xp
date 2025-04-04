package com.enonic.xp.core.impl.event.cluster;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterEventReceiverTest
{
    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ITopic<Event> topic;

    @BeforeEach
    void setUp()
    {
        when( hazelcastInstance.<Event>getTopic( ClusterEventSender.ACTION ) ).thenReturn( topic );
    }

    @Test
    void testMessageReceived()
    {
        final ClusterEventReceiver clusterEventReceiver = new ClusterEventReceiver( hazelcastInstance, eventPublisher );
        //Creates an event
        Event event = Event.create( "eventType" ).
            timestamp( 123L ).
            distributed( true ).
            value( "key1", "value1" ).
            value( "key2", 1234L ).build();

        //Passes the event received to SendEventRequestHandler
        Message<Event> message = new Message<>( "", event, System.currentTimeMillis(), null );
        clusterEventReceiver.onMessage( message );

        //Checks that the event was correctly published
        ArgumentCaptor<Event> argumentCaptor = ArgumentCaptor.forClass( Event.class );
        verify( this.eventPublisher ).publish( argumentCaptor.capture() );
        final Event eventForwarded = argumentCaptor.getValue();
        assertEquals( eventForwarded.getType(), event.getType() );
        assertEquals( eventForwarded.getTimestamp(), event.getTimestamp() );
        assertFalse( eventForwarded.isDistributed() );
        assertEquals( eventForwarded.getData(), event.getData() );
    }

    @Test
    void testLocalMemberIgnored()
    {
        final ClusterEventReceiver clusterEventReceiver = new ClusterEventReceiver( hazelcastInstance, eventPublisher );
        final Event event = Event.create( "eventType" ).build();

        final Member publishingMember = mock( Member.class );
        when( publishingMember.localMember() ).thenReturn( true );

        Message<Event> message = new Message<>( "", event, System.currentTimeMillis(), publishingMember );
        clusterEventReceiver.onMessage( message );

        verifyNoInteractions( this.eventPublisher );
    }

    @Test
    void lifecycle()
    {
        final ClusterEventReceiver clusterEventReceiver = new ClusterEventReceiver( hazelcastInstance, eventPublisher );

        final UUID uuid = UUID.randomUUID();
        when( topic.addMessageListener( notNull() ) ).thenReturn( uuid );

        clusterEventReceiver.activate();

        clusterEventReceiver.deactivate();
        verify( topic ).removeMessageListener( uuid );
    }
}
