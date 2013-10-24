package com.enonic.wem.api.entity;


public class CreateNodeResult
{
    private final Node persistedNode;

    public CreateNodeResult( final Node persistedNode )
    {
        this.persistedNode = persistedNode;
    }

    public Node getPersistedNode()
    {
        return persistedNode;
    }
}
