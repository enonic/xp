package com.enonic.wem.core.index.query;

import com.enonic.wem.api.entity.EntityId;

public class NodeQueryResultEntry
{
    private final float score;

    private final EntityId id;

    protected NodeQueryResultEntry( final float score, final String id )
    {
        this.score = score;
        this.id = EntityId.from( id );
    }

    public float getScore()
    {
        return score;
    }

    public EntityId getId()
    {
        return id;
    }

}
