package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.BaseTransportRequestHandler;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.Event2;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true, service = TransportRequestHandler.class)
public final class SendEventRequestHandler
    extends BaseTransportRequestHandler<SendEventRequest>
{
    private TransportService transportService;

    private EventPublisher eventPublisher;

    @Activate
    public void activate()
    {
        this.transportService.registerHandler( ClusterEventSender.ACTION, this );
    }

    @Deactivate
    public void deactivate()
    {
        this.transportService.removeHandler( ClusterEventSender.ACTION );
    }

    @Override
    public SendEventRequest newInstance()
    {
        return new SendEventRequest();
    }

    @Override
    public void messageReceived( final SendEventRequest request, final TransportChannel channel )
    {
        final Event2 receivedEvent = request.getEvent();
        final Event2 forwardedEvent = Event2.create( receivedEvent ).distributed( false ).localOrigin( false ).build();
        this.eventPublisher.publish( forwardedEvent );
    }

    @Override
    public String executor()
    {
        return ThreadPool.Names.MANAGEMENT;
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
