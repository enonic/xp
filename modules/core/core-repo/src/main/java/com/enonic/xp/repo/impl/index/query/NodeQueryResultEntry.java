package com.enonic.xp.repo.impl.index.query;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.repo.impl.ReturnValues;

public class NodeQueryResultEntry
{
    private final float score;

    private final NodeId id;

    private final ReturnValues returnValues;

    public NodeQueryResultEntry( final float score, final String id, final ReturnValues returnValues )
    {
        this.score = score;
        this.id = NodeId.from( id );
        this.returnValues = returnValues;
    }

    public float getScore()
    {
        return score;
    }

    public NodeId getId()
    {
        return id;
    }

    public ReturnValues getReturnValues()
    {
        return returnValues;
    }
}
