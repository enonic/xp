package com.enonic.wem.api.command.entity;


import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.Nodes;

public class GetNodesByParent
    extends Command<Nodes>
{
    private final NodePath parent;

    public GetNodesByParent( final NodePath parent )
    {
        this.parent = parent;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( parent, "parent must be specified" );
    }

    public NodePath getParent()
    {
        return this.parent;
    }
}


