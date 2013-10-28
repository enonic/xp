package com.enonic.wem.api.command.entity;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.NodePath;

public class NodeDeleteCommands
{
    public DeleteNodeByPath byPath( final NodePath nodePath )
    {
        return new DeleteNodeByPath( nodePath );
    }

    public DeleteNodeById byId( final EntityId id )
    {
        return new DeleteNodeById( id );
    }

}
