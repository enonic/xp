package com.enonic.wem.api.entity;

import com.google.common.base.Preconditions;

public class GetNodesByParentParams
{
    private final NodePath parent;

    public GetNodesByParentParams( final NodePath parent )
    {
        this.parent = parent;
    }

    public void validate()
    {
        Preconditions.checkNotNull( parent, "parent must be specified" );
    }

    public NodePath getParent()
    {
        return this.parent;
    }
}
