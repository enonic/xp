package com.enonic.wem.repo.internal.index.query;

import com.enonic.xp.core.node.NodeId;

public class NodeQueryResultEntry
{
    private final float score;

    private final NodeId id;

    public NodeQueryResultEntry( final float score, final String id )
    {
        this.score = score;
        this.id = NodeId.from( id );
    }

    public float getScore()
    {
        return score;
    }

    public NodeId getId()
    {
        return id;
    }

}
