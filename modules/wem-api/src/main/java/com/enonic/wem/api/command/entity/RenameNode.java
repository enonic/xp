package com.enonic.wem.api.command.entity;

import com.google.common.base.Preconditions;

import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;

public class RenameNode
    extends Command<Node>
{
    private final EntityId id;

    private final NodeName nodeName;

    public RenameNode( final EntityId id, final NodeName nodeName )
    {
        this.id = id;
        this.nodeName = nodeName;
    }

    public EntityId getId()
    {
        return id;
    }

    public NodeName getNodeName()
    {
        return nodeName;
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( id );
        Preconditions.checkNotNull( nodeName );
    }
}
