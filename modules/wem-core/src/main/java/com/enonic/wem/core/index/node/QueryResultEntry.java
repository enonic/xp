package com.enonic.wem.core.index.node;

public class QueryResultEntry
{
    private final float score;

    private final String id;

    protected QueryResultEntry( final float score, final String id )
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
