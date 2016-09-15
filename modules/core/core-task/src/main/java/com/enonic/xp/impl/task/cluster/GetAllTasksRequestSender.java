package com.enonic.xp.impl.task.cluster;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.EmptyTransportResponseHandler;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public final class GetAllTasksRequestSender
{
    public static final String ACTION = "xp/getalltasks";

    private ClusterService clusterService;

    private TransportService transportService;

    public void getAllTasks()
    {
        final TransportRequest transportRequest = new GetAllTasksRequest();
        final DiscoveryNode localNode = this.clusterService.localNode();
        for ( final DiscoveryNode node : this.clusterService.state().nodes() )
        {
            send( transportRequest, node );
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
