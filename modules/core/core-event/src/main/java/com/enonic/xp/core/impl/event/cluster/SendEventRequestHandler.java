package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.BaseTransportRequestHandler;
import org.elasticsearch.transport.TransportChannel;
import org.elasticsearch.transport.TransportRequestHandler;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = TransportRequestHandler.class, property = {"action=xp/event"})
// See https://github.com/enonic/cms/blob/master/cms-ee/cms-ee-core/src/main/java/com/enonic/cms/ee/cluster/SendClusterEventRequestHandler.java
public final class SendEventRequestHandler
    extends BaseTransportRequestHandler<SendEventRequest>
{
    @Override
    public SendEventRequest newInstance()
    {
        return new SendEventRequest();
    }

    @Override
    public void messageReceived( final SendEventRequest request, final TransportChannel channel )
        throws Exception
    {
    }

    @Override
    public String executor()
    {
        return ThreadPool.Names.MANAGEMENT;
    }
}
