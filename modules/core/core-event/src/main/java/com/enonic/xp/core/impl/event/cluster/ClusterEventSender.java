package com.enonic.xp.core.impl.event.cluster;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.EmptyTransportResponseHandler;
import org.elasticsearch.transport.TransportException;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@Component(immediate = true)
public final class ClusterEventSender
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ClusterEventSender.class );

    public static final String ACTION = "xp/event";

    private ClusterService clusterService;

    private TransportService transportService;

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null && event.isDistributed() )
        {
            final SendEventRequest transportRequest = new SendEventRequest( event );
            send( transportRequest );
        }
    }

    private void send( final SendEventRequest transportRequest )
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

    private void send( final SendEventRequest transportRequest, final DiscoveryNode node )
    {
        final EmptyTransportResponseHandler responseHandler = new EmptyTransportResponseHandler( ThreadPool.Names.SAME )
        {
            @Override
            public void handleException( final TransportException exp )
            {
                LOG.warn( "Failed to send Event to {} {}", node, transportRequest.getEvent(), exp );
            }
        };
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
