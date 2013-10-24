package com.enonic.wem.api.item;


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
