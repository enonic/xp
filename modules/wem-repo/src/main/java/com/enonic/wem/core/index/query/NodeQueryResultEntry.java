package com.enonic.wem.core.index.query;

import com.enonic.wem.repo.NodeId;

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
