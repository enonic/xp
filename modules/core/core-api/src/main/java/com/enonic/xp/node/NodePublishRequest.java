package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class NodePublishRequest
{
    private final NodeId nodeId;

    private final NodePublishReason reason;

    private NodePublishRequest( final NodeId nodeId, final NodePublishReason reason )
    {
        this.nodeId = nodeId;
        this.reason = reason;
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

    public static NodePublishRequest requested( final NodeId nodeId )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonRequested() );
    }

    public static NodePublishRequest parentFor( final NodeId nodeId, final NodeId parentOf )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonIsParent( parentOf ) );
    }

    public static NodePublishRequest childOf( final NodeId nodeId, final NodeId childOf )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonIsChild( childOf ) );
    }

    public static NodePublishRequest referredFrom( final NodeId nodeId, final NodeId referredFrom )
    {
        return new NodePublishRequest( nodeId, new NodePublishReasonIsReferred( referredFrom ) );
    }
}