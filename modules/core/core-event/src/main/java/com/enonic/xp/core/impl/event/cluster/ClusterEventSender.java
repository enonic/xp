package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.EmptyTransportResponseHandler;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.Event2;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ClusterEventSender
    implements EventListener
{
    public static final String ACTION = "xp/event";

    private ClusterService clusterService;

    private TransportService transportService;

    @Override
    public void onEvent( final Event event )
    {
        if ( event instanceof Event2 )
        {
            Event2 event2 = (Event2) event;
            if ( event2.isDistributed() )
            {
                final TransportRequest transportRequest = new SendEventRequest( event2 );
                send( transportRequest );
            }
        }
    }

    private void send( final TransportRequest transportRequest )
    {
        final DiscoveryNode localNode = this.clusterService.localNode();
        for ( final DiscoveryNode node : this.clusterService.state().nodes() )
        {
            if ( !node.equals( localNode ) )
            {
                send( transportRequest, node );
            }
        }
    }

    private void send( final TransportRequest transportRequest, final DiscoveryNode node )
    {
        final EmptyTransportResponseHandler responseHandler = new EmptyTransportResponseHandler( ThreadPool.Names.MANAGEMENT );
        this.transportService.sendRequest( node, ACTION, transportRequest, responseHandler );
    }

    @Reference
    public void setClusterService( final ClusterService clusterService )
    {
        this.clusterService = clusterService;
    }

    @Reference
    public void setTransportService( final TransportService transportService )
    {
        this.transportService = transportService;
    }
}
