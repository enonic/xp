package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.tasks.Task;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true, service = TransportRequestHandler.class)
public final class SendEventRequestHandler
    implements TransportRequestHandler<SendEventRequest>
{
    private TransportService transportService;

    private EventPublisher eventPublisher;

    @Activate
    public void activate()
    {
    }

    @Deactivate
    public void deactivate()
    {

    }

    @Override
    public void messageReceived( final SendEventRequest request, final TransportChannel channel, final Task task )
        throws Exception
    {
        final Event receivedEvent = request.getEvent();
        final Event forwardedEvent = Event.create( receivedEvent ).distributed( false ).localOrigin( false ).build();
        this.eventPublisher.publish( forwardedEvent );

    }

    @Reference
    public void setTransportService( final TransportService transportService )
    {
        this.transportService = transportService;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }

}
