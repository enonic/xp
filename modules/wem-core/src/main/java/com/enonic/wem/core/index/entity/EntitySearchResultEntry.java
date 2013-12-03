package com.enonic.wem.core.index.entity;

public class EntitySearchResultEntry
{
    private final float score;

    private final String id;

    public EntitySearchResultEntry( final float score, final String id )
    {
        this.score = score;
        this.id = id;
    }
}
