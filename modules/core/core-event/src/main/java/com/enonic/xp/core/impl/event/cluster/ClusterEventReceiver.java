package com.enonic.xp.core.impl.event.cluster;

import java.util.UUID;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.cluster.Member;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true)
public final class ClusterEventReceiver
    implements MessageListener<Event>
{
    private final EventPublisher eventPublisher;

    private final ITopic<Event> topic;

    private UUID registrationId;

    @Activate
    public ClusterEventReceiver( @Reference final HazelcastInstance hazelcastInstance, @Reference final EventPublisher eventPublisher )
    {
        this.topic = hazelcastInstance.getTopic( ClusterEventSender.ACTION );
        this.eventPublisher = eventPublisher;
    }

    @Activate
    public void activate()
    {
        registrationId = topic.addMessageListener( this );
    }

    @Deactivate
    public void deactivate()
    {
        topic.removeMessageListener( registrationId );
    }

    @Override
    public void onMessage( final Message<Event> message )
    {
        final Member publishingMember = message.getPublishingMember();
        if ( publishingMember == null || !publishingMember.localMember() )
        {
            final Event forwardedEvent = Event.create( message.getMessageObject() ).
                distributed( false ).
                localOrigin( false ).
                build();
            this.eventPublisher.publish( forwardedEvent );
        }
    }
}
