package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.BaseTransportRequestHandler;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequestHandler;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.Event2;
import com.enonic.xp.event.EventPublisher;

@Component(immediate = true, service = TransportRequestHandler.class, property = {"action=xp/event"})
public final class SendEventRequestHandler
    extends BaseTransportRequestHandler<SendEventRequest>
{
    private EventPublisher eventPublisher;

    @Override
    public SendEventRequest newInstance()
    {
        return new SendEventRequest();
    }

    @Override
    public void messageReceived( final SendEventRequest request, final TransportChannel channel )
    {
        final Event2 receivedEvent = request.getEvent();
        final Event2 forwardedEvent = Event2.create( receivedEvent ).distributed( false ).build();
        this.eventPublisher.publish( forwardedEvent );
    }

    @Override
    public String executor()
    {
        return ThreadPool.Names.MANAGEMENT;
    }

    @Reference
    public void setEventPublisher( final EventPublisher eventPublisher )
    {
        this.eventPublisher = eventPublisher;
    }
}
