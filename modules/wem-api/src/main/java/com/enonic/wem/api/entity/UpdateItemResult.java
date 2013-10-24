package com.enonic.wem.api.entity;

public class UpdateItemResult
{
    private final Node persistedNode;

    public UpdateItemResult( final Node persistedNode )
    {
        this.persistedNode = persistedNode;
    }

    public Node getPersistedNode()
    {
        return persistedNode;
    }
}
