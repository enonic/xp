package com.enonic.xp.node;

public class NodeHit
{
    private final NodeId nodeId;

    private final float score;

    public NodeHit( final NodeId nodeId, final float score )
    {
        this.nodeId = nodeId;
        this.score = score;
    }

    public static NodeHit from( final String nodeId, final float score )
    {
        return new NodeHit( NodeId.from( nodeId ), score );
    }


    public static NodeHit from( final String nodeId )
    {
        return new NodeHit( NodeId.from( nodeId ), 0 );
    }


    public NodeId getNodeId()
    {
        return nodeId;
    }

    public float getScore()
    {
        return score;
    }
}
