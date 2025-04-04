package com.enonic.xp.core.impl.event.cluster;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ClusterEventSender
    implements EventListener
{
    public static final String ACTION = "xp/event";

    private final ITopic<Event> topic;

    @Activate
    public ClusterEventSender( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.topic = hazelcastInstance.getTopic( ACTION );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null && event.isDistributed() )
        {
            topic.publish( event );
        }
    }
}
