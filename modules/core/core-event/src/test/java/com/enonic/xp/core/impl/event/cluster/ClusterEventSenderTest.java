package com.enonic.xp.core.impl.event.cluster;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;

import com.enonic.xp.event.Event;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClusterEventSenderTest
{
    private ClusterEventSender clusterEventSender;

    @Mock
    private HazelcastInstance hazelcastInstance;

    @Mock
    private ITopic<Event> topic;

    @BeforeEach
    void setUp()
    {
        when( hazelcastInstance.<Event>getTopic( ClusterEventSender.ACTION ) ).thenReturn( topic );

        clusterEventSender = new ClusterEventSender( hazelcastInstance );
    }

    @Test
    void onEvent()
    {
        final Event event = Event.create( "aaa" ).distributed( true ).build();

        this.clusterEventSender.onEvent( event );

        verify( topic ).publish( eq( event ) );
    }


    @Test
    void onNonDistributableEvent()
    {
        final Event event = Event.create( "aaa" ).build();

        this.clusterEventSender.onEvent( event );

        verifyNoInteractions( topic );
    }
}
