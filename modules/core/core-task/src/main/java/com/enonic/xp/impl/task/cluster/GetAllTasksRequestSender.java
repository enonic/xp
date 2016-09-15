package com.enonic.xp.impl.task.cluster;

import java.util.List;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.task.TaskInfo;

@Component(immediate = true)
public final class GetAllTasksRequestSender
{
    public static final String ACTION = "xp/getalltasks";

    private ClusterService clusterService;

    private TransportService transportService;

    public List<TaskInfo> getAllTasks()
    {
        final TransportRequest transportRequest = new GetAllTasksRequest();
        final DiscoveryNodes discoveryNodes = this.clusterService.state().nodes();
        final GetAllTasksResponseHandler responseHandler = new GetAllTasksResponseHandler( discoveryNodes.size() );
        for ( final DiscoveryNode discoveryNode : discoveryNodes )
        {
            this.transportService.sendRequest( discoveryNode, ACTION, transportRequest, responseHandler );
        }
        return responseHandler.getTaskInfos();
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
