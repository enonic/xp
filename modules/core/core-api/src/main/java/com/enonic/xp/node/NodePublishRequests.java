package com.enonic.xp.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodePublishRequests
    implements Iterable<NodePublishRequest>
{
    private final Set<NodePublishRequest> nodePublishRequests;

    private final Map<NodeId, NodePublishRequest> nodePublishRequestMap;

    private final Set<NodePublishRequest> publishAsParentFor = new HashSet<>();

    private final Set<NodePublishRequest> publishAsReferredTo = new HashSet<>();

    private final Set<NodePublishRequest> publishAsRequested = new HashSet<>();

    private final Set<NodePublishRequest> publishAsChildOf = new HashSet<>();

    public int size()
    {
        return this.nodePublishRequests.size();
    }

    public NodePublishRequest get( final NodeId nodeId )
    {
        return nodePublishRequestMap.get( nodeId );
    }

    public Set<NodePublishRequest> getPublishAsParentFor()
    {
        return publishAsParentFor;
    }

    public Set<NodePublishRequest> getPublishAsChildOf()
    {
        return publishAsChildOf;
    }

    public Set<NodePublishRequest> getPublishAsReferredTo()
    {
        return publishAsReferredTo;
    }

    public Set<NodePublishRequest> getPublishAsRequested()
    {
        return publishAsRequested;
    }

    @Override
    public Iterator<NodePublishRequest> iterator()
    {
        return this.nodePublishRequests.iterator();
    }

    public boolean hasPublishOutsideSelection()
    {
        return !this.publishAsReferredTo.isEmpty() || !this.publishAsParentFor.isEmpty();
    }

    public NodeIds getNodeIds()
    {
        return NodeIds.from( nodePublishRequestMap.keySet() );
    }


    public NodePublishRequests()
    {
        this.nodePublishRequestMap = new HashMap<>();
        this.nodePublishRequests = new HashSet<>();
    }

    public void add( final NodePublishRequest nodePublishRequest )
    {
        if ( nodePublishRequest.reasonParentFor() )
        {
            publishAsParentFor.add( nodePublishRequest );
        }
        else if ( nodePublishRequest.reasonReferredFrom() )
        {
            publishAsReferredTo.add( nodePublishRequest );
        }
        else if ( nodePublishRequest.reasonChildOf() )
        {
            publishAsChildOf.add( nodePublishRequest );
        }
        else
        {
            publishAsRequested.add( nodePublishRequest );
        }

        this.nodePublishRequestMap.put( nodePublishRequest.getNodeId(), nodePublishRequest );
        this.nodePublishRequests.add( nodePublishRequest );
    }


}