package com.enonic.xp.impl.task.cluster;

import java.util.List;

import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.cluster.node.DiscoveryNodes;
import org.elasticsearch.transport.TransportRequest;
import org.elasticsearch.transport.TransportRequestOptions;
import org.elasticsearch.transport.TransportService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;

@Component
public final class TaskTransportRequestSenderImpl
    implements TaskTransportRequestSender
{
    static final long TRANSPORT_REQUEST_TIMEOUT = 5_000l; //5s 

    public static final String ACTION = "xp/task";

    private ClusterService clusterService;

    private TransportService transportService;

    public List<TaskInfo> getByTaskId( final TaskId taskId )
    {
        final TransportRequest transportRequest = new TaskTransportRequest( TaskTransportRequest.Type.BY_ID, taskId );
        return send( transportRequest );
    }

    public List<TaskInfo> getRunningTasks()
    {
        final TransportRequest transportRequest = new TaskTransportRequest( TaskTransportRequest.Type.RUNNING, null );
        return send( transportRequest );
    }

    public List<TaskInfo> getAllTasks()
    {
        final TransportRequest transportRequest = new TaskTransportRequest( TaskTransportRequest.Type.ALL, null );
        return send( transportRequest );
    }

    private List<TaskInfo> send( final TransportRequest transportRequest )
    {
        final DiscoveryNodes discoveryNodes = this.clusterService.state().nodes();
        final TaskTransportResponseHandler responseHandler = new TaskTransportResponseHandler( discoveryNodes.size() );
        for ( final DiscoveryNode discoveryNode : discoveryNodes )
        {
            final TransportRequestOptions options = TransportRequestOptions.builder().withTimeout( TRANSPORT_REQUEST_TIMEOUT ).build();
            this.transportService.sendRequest( discoveryNode, ACTION, transportRequest, options, responseHandler );
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
