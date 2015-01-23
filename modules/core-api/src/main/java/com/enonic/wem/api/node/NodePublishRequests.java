package com.enonic.wem.api.node;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class NodePublishRequests
{
    private final Set<NodePublishRequest> nodePublishRequests;

    private final Map<NodeId, NodePublishRequest> nodePublishRequestMap;

    public int size()
    {
        return this.nodePublishRequests.size();
    }

    public NodePublishRequest get( final NodeId nodeId )
    {
        return nodePublishRequestMap.get( nodeId );
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( nodePublishRequestMap.keySet() );
    }

    public NodePublishRequests()
    {
        this.nodePublishRequestMap = Maps.newHashMap();
        this.nodePublishRequests = Sets.newHashSet();
    }

    public void add( final NodePublishRequest nodePublishRequest )
    {
        this.nodePublishRequestMap.put( nodePublishRequest.getNodeId(), nodePublishRequest );
        this.nodePublishRequests.add( nodePublishRequest );
    }


}
