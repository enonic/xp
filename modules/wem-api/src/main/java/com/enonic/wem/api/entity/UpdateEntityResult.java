package com.enonic.wem.api.entity;

public class UpdateEntityResult
{
    private final Node persistedNode;

    public UpdateEntityResult( final Node persistedNode )
    {
        this.persistedNode = persistedNode;
    }

    public Node getPersistedNode()
    {
        return persistedNode;
    }
}
