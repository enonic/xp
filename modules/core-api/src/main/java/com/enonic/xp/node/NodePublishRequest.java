package com.enonic.xp.node;

import com.google.common.annotations.Beta;

@Beta
public class NodePublishRequest
{
    private final NodeId nodeId;

    private final NodePublishReason reason;

    private final NodeId initialReasonNodeId;

    private NodePublishRequest( final NodeId nodeId, final NodePublishReason reason, final NodeId initialReasonNodeId )
    {
        this.nodeId = nodeId;
        this.reason = reason;
        this.initialReasonNodeId = initialReasonNodeId;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    public boolean reasonParentFor()
    {
        return ( this.reason instanceof NodePublishReasonIsParent );
    }

    public boolean reasonChildOf()
    {
        return ( this.reason instanceof NodePublishReasonIsChild );
    }

    public boolean reasonRequested()
    {
        return ( this.reason instanceof NodePublishReasonRequested );
    }

    public boolean reasonReferredFrom()
    {
        return ( this.reason instanceof NodePublishReasonIsReferred );
    }

    public NodePublishReason getReason()
    {
        return reason;
    }

    public NodeId getInitialReasonNodeId()
    {
        return initialReasonNodeId;
    }

    public static NodePublishRequest requested( final NodeId nodeId, final NodeId initialReasonNodeId )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonRequested(), initialReasonNodeId );
    }

    public static NodePublishRequest parentFor( final NodeId nodeId, final NodeId parentOf, final NodeId initialReasonNodeId )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonIsParent( parentOf ), initialReasonNodeId );
    }

    public static NodePublishRequest childOf( final NodeId nodeId, final NodeId childOf, final NodeId initialReasonNodeId )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonIsChild( childOf ), initialReasonNodeId );
    }

    public static NodePublishRequest referredFrom( final NodeId nodeId, final NodeId referredFrom, final NodeId initialReasonNodeId )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonIsReferred( referredFrom ), initialReasonNodeId );
    }
}
