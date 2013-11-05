package com.enonic.wem.api.command.entity;

import com.enonic.wem.api.entity.Node;

public class UpdateNodeResult
{
    private final Node persistedNode;

    public UpdateNodeResult( final Node persistedNode )
    {
        this.persistedNode = persistedNode;
    }

    public Node getPersistedNode()
    {
        return persistedNode;
    }
}
