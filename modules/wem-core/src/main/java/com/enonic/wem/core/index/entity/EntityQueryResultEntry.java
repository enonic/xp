package com.enonic.wem.core.index.entity;

public class EntityQueryResultEntry
{
    private final float score;

    private final String id;

    public EntityQueryResultEntry( final float score, final String id )
    {
        this.score = score;
        this.id = id;
    }

    public float getScore()
    {
        return score;
    }

    public String getId()
    {
        return id;
    }
}
